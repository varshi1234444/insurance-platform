package com.ashith.insurance.grievance_service.projection;

import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;

public interface CategoryStatusCount {

    GrievanceCategory getCategory();

    GrievanceStatus getStatus();

    long getTotal();
}