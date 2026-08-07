package com.example.back.service.appr;

import java.util.List;
import java.util.Map;

public interface ApprService {
    public int submitApproval(Map<String, Object> entity);

    public List<Map<String, Object>> getNotifications(String userId);

    public int lineSubmitApproval(Map<String, Object> entity);
}
