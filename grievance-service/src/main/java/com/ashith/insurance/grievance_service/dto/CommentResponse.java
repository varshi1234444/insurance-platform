package com.ashith.insurance.grievance_service.dto;

import com.ashith.insurance.grievance_service.entity.GrievanceComment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String author,
        String message,
        Instant createdAt) {

    public static CommentResponse from(GrievanceComment c) {
        return new CommentResponse(c.getId(), c.getAuthor(), c.getMessage(), c.getCreatedAt());
    }
}