package com.ashith.insurance.grievance_service.projection;

import java.math.BigDecimal;

public interface ResolutionStats {

    String getCategory();

    long getResolvedCount();

    BigDecimal getAvgHours();
}