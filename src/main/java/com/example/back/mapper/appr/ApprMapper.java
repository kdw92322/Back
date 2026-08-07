package com.example.back.mapper.appr;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprMapper {
    public int insertApprovalInfo(Map<String, Object> param);

    public int updateApprovalStatus(Map<String, Object> param);

    public int insertApprovalLine(Map<String, Object> param);

    public List<Map<String, Object>> getNotifications(String userId);

    public List<Map<String, Object>> getApprLineList(String apprId);

    public int lineSubmitApproval(Map<String, Object> params);

    public int getLastSequence(Map<String, Object> params);

    public int getApproverSequence(Map<String, Object> params);

    public Map<String, Object> getApprTargetCheck(String userId);
}
