package com.example.back.service.appr.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.BudgetMapper;
import com.example.back.mapper.acc.DeptMapper;
import com.example.back.mapper.acc.ExpenseRptMapper;
import com.example.back.mapper.appr.ApprMapper;
import com.example.back.mapper.slip.SlipCfmMapper;
import com.example.back.service.appr.ApprService;
import com.example.back.service.slip.SlipCfmService;
import com.example.back.util.SecurityUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ApprServiceImpl implements ApprService {

    @Autowired
    private ExpenseRptMapper expenseRptMapper;

    @Autowired
    private SlipCfmMapper slipCfmMapper;

    @Autowired
    private SlipCfmService slipCfmService;

    @Autowired
    private ApprMapper apprMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private BudgetMapper budgetMapper;

    public int submitApproval(Map<String, Object> entity) {
        System.out.println("Processing approval submission: " + entity);
        ObjectMapper mapper = new ObjectMapper();

        String key = String.valueOf(entity.get("key"));
        String status = String.valueOf(entity.get("status"));
        String title = String.valueOf(entity.get("title"));
        String opinion = String.valueOf(entity.get("opinion"));
        String appr_type = String.valueOf(entity.get("appr_type"));
        String user_id = SecurityUtil.getCurrentUserId();

        System.out.println("appr_type : " + appr_type);

        Map<String, Object> statParam = new HashMap<String, Object>();
        if (appr_type.equals("expense")) {
            // 1. 지출결의서 상태 변경
            statParam.put("no", key);
            statParam.put("status", status);
            statParam.put("opinion", opinion);
            expenseRptMapper.updateApprovalStatus(statParam);
        } else if (appr_type.equals("slip")) {
            // 2. 전표 상태 변경
            statParam.put("slip_id", key);
            statParam.put("confirmed_by", user_id);
            slipCfmMapper.approve(statParam);
        }

        // 2. 결재 정보 저장
        Map<String, Object> apprParam = new HashMap<String, Object>();
        String apprUUID = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        apprParam.put("id", apprUUID);
        apprParam.put("document_type", appr_type);
        apprParam.put("source_id", key);
        apprParam.put("title", title);
        apprParam.put("drafter_id", user_id);
        apprParam.put("status", status);
        apprParam.put("opinion", opinion);

        // summary_content 생성
        String summary_content = "";
        StringBuilder sb = createSummaryContent(key, appr_type, opinion);
        summary_content = sb.toString();

        apprParam.put("summary_content", summary_content);

        apprMapper.insertApprovalInfo(apprParam);

        // 3. 결재선 저장
        List<Map<String, Object>> approvers = mapper.convertValue(
                entity.get("approvers"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (int i = 0; i < approvers.size(); i++) {
            Map<String, Object> apprLineParam = new HashMap<String, Object>();
            Map<String, Object> approver = approvers.get(i);

            apprLineParam.put("approval_id", apprUUID);
            apprLineParam.put("approver_id", approver.get("user_id"));
            apprLineParam.put("sequence", String.valueOf(approver.get("seq")));
            apprLineParam.put("type", appr_type);
            apprLineParam.put("status", 0);

            if (String.valueOf(approver.get("seq")).equals("1")) {
                apprLineParam.put("is_approve", "1");
            } else {
                apprLineParam.put("is_approve", "0");
            }

            apprMapper.insertApprovalLine(apprLineParam);
        }

        return 0;
    }

    @Override
    public List<Map<String, Object>> getNotifications(String userId) {
        List<Map<String, Object>> notiList = apprMapper.getNotifications(userId);

        for (int i = 0; i < notiList.size(); i++) {
            Map<String, Object> noti = notiList.get(i);
            String id = String.valueOf(noti.get("id"));
            String sourceId = String.valueOf(noti.get("sourceId"));
            String type = String.valueOf(noti.get("type"));

            if (type.equals("expense")) {// 지출결의
                Map<String, Object> params = new HashMap<>();
                params.put("no", sourceId);

                List<Map<String, Object>> expenseReports = expenseRptMapper.selectAllExpenseReports(params);
                if (expenseReports.size() > 0) {
                    Map<String, Object> temp = expenseReports.get(0);
                    // System.out.println("temp : " + temp);
                }
            } else if (type.equals("slip")) {// 회계
                Map<String, Object> params = new HashMap<>();
                params.put("no", sourceId);
            }

            List<Map<String, Object>> approvalLines = apprMapper.getApprLineList(id);
            noti.put("approvalLines", approvalLines);
        }

        return notiList;
    }

    @Override
    public int lineSubmitApproval(Map<String, Object> entity) {
        String id = String.valueOf(entity.get("doc_id"));
        int action = Integer.parseInt(String.valueOf(entity.get("action")));
        String comment = String.valueOf(entity.get("comment"));
        String type = String.valueOf(entity.get("type"));
        String userId = String.valueOf(entity.get("user_id"));
        String sourceId = String.valueOf(entity.get("source_id"));

        // 결재선 상태 변경
        Map<String, Object> params = new HashMap<>();
        params.put("approval_id", id);
        params.put("approver_id", userId);
        params.put("status", action);
        params.put("comment", comment);
        params.put("source_id", sourceId);

        // 최종결재 라인인지 체크
        int lastSequence = apprMapper.getLastSequence(params);
        int approverSequence = apprMapper.getApproverSequence(params);

        // 최종결재 라인이라면, 결재마스터 및 기타(지출결의나 회계) 마스터 상태 업데이트
        if (lastSequence == approverSequence) {
            if (type.equals("expense")) {
                Map<String, Object> expApprParams = new HashMap<>();
                expApprParams.put("no", sourceId);
                expApprParams.put("status", 3);
                expApprParams.put("opinion", comment);
                expenseRptMapper.updateApprovalStatus(expApprParams);
            } else if (type.equals("slip")) {
                Map<String, Object> slipApprParams = new HashMap<>();
                slipApprParams.put("slip_id", sourceId);
                slipApprParams.put("confirmed", userId);

                List<Map<String, Object>> list = slipCfmService.list(slipApprParams);
                if (list.size() > 0) {
                    Map<String, Object> temp = list.get(0);
                    slipApprParams.put("budget_code", temp.get("budgetCode"));
                    slipApprParams.put("amt", temp.get("totalAmount"));
                    slipCfmService.confirm(slipApprParams);
                }
            }

            Map<String, Object> apprParams = new HashMap<>();
            apprParams.put("id", id);
            apprParams.put("status", 3);
            apprMapper.updateApprovalStatus(apprParams);
        }

        return apprMapper.lineSubmitApproval(params);
    }

    public StringBuilder createSummaryContent(String sourceKey, String type, String opinion) {
        StringBuilder sb = new StringBuilder();

        System.out.println("sourceKey : " + sourceKey);
        System.out.println("type : " + type);

        // 💡 3자리 금액 콤마 포맷터 선언
        DecimalFormat df = new DecimalFormat("#,###");

        if (type.equals("expense")) {// 지출결의
            Map<String, Object> params = new HashMap<>();
            params.put("no", sourceKey);

            List<Map<String, Object>> expenseReports = expenseRptMapper.selectAllExpenseReports(params);
            if (expenseReports != null && expenseReports.size() > 0) {
                Map<String, Object> temp = expenseReports.get(0);
                // System.out.println("temp : " + temp);

                // ==================== [지출결의 텍스트 폼 생성] ====================
                sb.append("[지출결의 상세 요약]\n");

                // 1. 결의번호 (no)
                sb.append("• 결의번호: ").append(temp.get("no") != null ? temp.get("no") : "-").append("\n");

                // 2. 결의일자 (reportDate)
                sb.append("• 결의일자: ").append(temp.get("reportDate") != null ? temp.get("reportDate") : "-")
                        .append("\n");

                // 3. 총 결의금액 포맷팅 (totalAmount)
                if (temp.get("totalAmount") != null) {
                    try {
                        double amount = Double.parseDouble(temp.get("totalAmount").toString());
                        sb.append("• 총 결의금액: ").append(df.format(amount)).append("원\n");
                    } catch (Exception e) {
                        sb.append("• 총 결의금액: ").append(temp.get("totalAmount")).append("원\n");
                    }
                } else {
                    sb.append("• 총 결의금액: 0원\n");
                }

                // 4. 귀속부서 및 예산코드 (deptCode, budgetCode)
                String deptName = deptMapper.selectDeptNameById(String.valueOf(temp.get("deptCode")));
                String budgetName = budgetMapper.selectBudgetName(String.valueOf(temp.get("budgetCode")));

                sb.append("• 귀속부서: ").append(deptName != null ? deptName : "-").append("\n");
                sb.append("• 예산코드: ").append(budgetName != null ? budgetName : "-").append("\n");

                // 5. 지출목적 (title)
                sb.append("• 지출목적: ").append(temp.get("title") != null ? temp.get("title") : "지출결의 청구 건").append("\n");

                // 6. 결의의견 (opinion)
                sb.append("• 결의의견: ").append(opinion != null ? opinion : "등록된 의견이 없습니다.").append("\n");
                // ===================================================================

            } else {
                sb.append("[지출결의] 해당 번호의 데이터를 찾을 수 없습니다.");
            }
        } else if (type.equals("slip")) {// 회계
            Map<String, Object> params = new HashMap<>();
            params.put("slip_id", sourceKey);
            List<Map<String, Object>> slipInfo = slipCfmMapper.list(params);

            if (slipInfo != null && slipInfo.size() > 0) {
                Map<String, Object> temp = slipInfo.get(0);
                String resolutionId = String.valueOf(temp.get("resolutionId"));
                params.put("no", resolutionId);
                List<Map<String, Object>> expenseReports = expenseRptMapper.selectAllExpenseReports(params);
                Map<String, Object> expenseReport = new HashMap<>();
                if (expenseReports.size() > 0) {
                    expenseReport = expenseReports.get(0);
                }

                sb.append("[회계 전표 요약]\n");
                sb.append("• 전표번호: ").append(sourceKey).append("\n");

                // 1. 결의번호 (no)
                sb.append("• 결의번호: ").append(expenseReport.get("no") != null ? expenseReport.get("no") : "-")
                        .append("\n");

                // 2. 결의일자 (reportDate)
                sb.append("• 결의일자: ")
                        .append(expenseReport.get("reportDate") != null ? expenseReport.get("reportDate") : "-")
                        .append("\n");

                // 3. 총 결의금액 포맷팅 (totalAmount)
                if (expenseReport.get("totalAmount") != null) {
                    try {
                        double amount = Double.parseDouble(expenseReport.get("totalAmount").toString());
                        sb.append("• 총 결의금액: ").append(df.format(amount)).append("원\n");
                    } catch (Exception e) {
                        sb.append("• 총 결의금액: ").append(expenseReport.get("totalAmount")).append("원\n");
                    }
                } else {
                    sb.append("• 총 결의금액: 0원\n");
                }

                // 4. 귀속부서 및 예산코드 (deptCode, budgetCode)
                String deptName = deptMapper.selectDeptNameById(String.valueOf(expenseReport.get("deptCode")));
                String budgetName = budgetMapper.selectBudgetName(String.valueOf(expenseReport.get("budgetCode")));

                sb.append("• 귀속부서: ").append(deptName != null ? deptName : "-").append("\n");
                sb.append("• 예산코드: ").append(budgetName != null ? budgetName : "-").append("\n");

                // 5. 지출목적 (title)
                sb.append("• 지출목적: ")
                        .append(expenseReport.get("title") != null ? expenseReport.get("title") : "지출결의 청구 건")
                        .append("\n");

                // 6. 결의의견 (opinion)
                sb.append("• 결의의견: ").append(opinion != null ? opinion : "등록된 의견이 없습니다.").append("\n");
                // ===================================================================
            }
        }

        return sb;
    }

}
