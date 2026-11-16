package com.ashith.insurance.grievance_service.service;

import com.ashith.insurance.grievance_service.dto.CategoryStatusCountResponse;
import com.ashith.insurance.grievance_service.dto.CommentRequest;
import com.ashith.insurance.grievance_service.dto.CommentResponse;
import com.ashith.insurance.grievance_service.dto.CreateGrievanceRequest;
import com.ashith.insurance.grievance_service.dto.GrievanceResponse;
import com.ashith.insurance.grievance_service.dto.PageResponse;
import com.ashith.insurance.grievance_service.dto.ResolutionStatsResponse;
import com.ashith.insurance.grievance_service.dto.SummaryResponse;
import com.ashith.insurance.grievance_service.entity.Grievance;
import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievanceComment;
import com.ashith.insurance.grievance_service.entity.GrievancePriority;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;
import com.ashith.insurance.grievance_service.projection.GrievanceSummary;
import com.ashith.insurance.grievance_service.repository.GrievanceCommentRepository;
import com.ashith.insurance.grievance_service.repository.GrievanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GrievanceService {

    private static final Map<GrievanceStatus, Set<GrievanceStatus>> ALLOWED_TRANSITIONS = Map.of(
            GrievanceStatus.OPEN, Set.of(GrievanceStatus.IN_PROGRESS),
            GrievanceStatus.IN_PROGRESS, Set.of(GrievanceStatus.RESOLVED),
            GrievanceStatus.RESOLVED, Set.of(GrievanceStatus.CLOSED, GrievanceStatus.IN_PROGRESS),
            GrievanceStatus.CLOSED, Set.of());

    private final GrievanceRepository grievanceRepository;
    private final GrievanceCommentRepository commentRepository;

    @Transactional
    public GrievanceResponse create(String email, CreateGrievanceRequest request) {
        Grievance saved = grievanceRepository.save(Grievance.builder()
                .customerEmail(email)
                .category(request.category())
                .priority(request.priority())
                .subject(request.subject())
                .description(request.description())
                .slaDeadline(Instant.now().plus(slaFor(request.priority())))
                .build());
        return GrievanceResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<GrievanceResponse> myGrievances(String email) {
        return grievanceRepository.findByCustomerEmailOrderByCreatedAtDesc(email)
                .stream().map(GrievanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GrievanceResponse getForUser(Long id, String email, boolean staff) {
        return GrievanceResponse.from(findAccessible(id, email, staff));
    }

    @Transactional(readOnly = true)
    public PageResponse<GrievanceResponse> search(GrievanceStatus status, GrievanceCategory category,
                                                  int days, Pageable pageable) {
        Instant from = Instant.now().minus(Duration.ofDays(days));
        Page<Grievance> page = grievanceRepository.search(status, category, from, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(GrievanceResponse::from).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public GrievanceResponse updateStatus(Long id, GrievanceStatus newStatus, String staffEmail) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grievance not found"));

        if (!ALLOWED_TRANSITIONS.get(grievance.getStatus()).contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot move a grievance from " + grievance.getStatus() + " to " + newStatus);
        }

        if (newStatus == GrievanceStatus.IN_PROGRESS && grievance.getAssignedTo() == null) {
            grievance.setAssignedTo(staffEmail);
        }
        if (newStatus == GrievanceStatus.RESOLVED) {
            grievance.setResolvedAt(Instant.now());
        }
        grievance.setStatus(newStatus);
        return GrievanceResponse.from(grievanceRepository.save(grievance));
    }

    @Transactional
    public CommentResponse addComment(Long id, String author, boolean staff, CommentRequest request) {
        Grievance grievance = findAccessible(id, author, staff);
        if (grievance.getStatus() == GrievanceStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This grievance is closed");
        }
        GrievanceComment saved = commentRepository.save(GrievanceComment.builder()
                .grievance(grievance)
                .author(author)
                .message(request.message())
                .build());
        return CommentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> comments(Long id, String email, boolean staff) {
        findAccessible(id, email, staff);
        return commentRepository.findByGrievanceIdOrderByCreatedAtAsc(id)
                .stream().map(CommentResponse::from).toList();
    }

    @Transactional
    public int escalateOverdue() {
        return grievanceRepository.escalateOverdue(Instant.now(),
                List.of(GrievanceStatus.OPEN, GrievanceStatus.IN_PROGRESS));
    }

    @Transactional(readOnly = true)
    public List<CategoryStatusCountResponse> categoryStatusCounts() {
        return grievanceRepository.countByCategoryAndStatus().stream()
                .map(r -> new CategoryStatusCountResponse(r.getCategory(), r.getStatus(), r.getTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResolutionStatsResponse> resolutionStats() {
        return grievanceRepository.resolutionStats().stream()
                .map(r -> new ResolutionStatsResponse(r.getCategory(), r.getResolvedCount(), r.getAvgHours()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(int days) {
        List<GrievanceSummary> rows = grievanceRepository.summaryForLastDays(days);
        if (rows.isEmpty()) {
            return new SummaryResponse(days, 0, 0, 0, 0, 0);
        }
        GrievanceSummary s = rows.get(0);
        return new SummaryResponse(days, s.getTotal(), s.getOpenCount(), s.getInProgressCount(),
                s.getResolvedCount(), s.getEscalatedCount());
    }

    private Grievance findAccessible(Long id, String email, boolean staff) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grievance not found"));
        if (!staff && !grievance.getCustomerEmail().equals(email)) {
            // Same message as "not found" so a customer cannot discover other customers' ticket ids.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grievance not found");
        }
        return grievance;
    }

    private Duration slaFor(GrievancePriority priority) {
        return switch (priority) {
            case HIGH -> Duration.ofHours(24);
            case MEDIUM -> Duration.ofHours(48);
            case LOW -> Duration.ofHours(72);
        };
    }
}