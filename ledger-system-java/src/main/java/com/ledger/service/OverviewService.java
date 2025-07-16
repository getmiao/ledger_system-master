package com.ledger.service;

import com.ledger.controller.OverviewController.OverviewSummaryVO;
import com.ledger.model.entity.User;
 
public interface OverviewService {
    OverviewSummaryVO getOverviewSummary(String viewType, User loginUser);
} 