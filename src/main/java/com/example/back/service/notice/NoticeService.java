package com.example.back.service.notice;

import java.util.List;
import java.util.Map;

public interface NoticeService {
    /**
     * 공지사항 목록 조회
     */
    List<Map<String, Object>> getNoticeList(Map<String, Object> params);

    void saveNotice(Map<String, Object> params);

    void updateNotice(Map<String, Object> params);

    void updateViewCount(Map<String, Object> params);

    void deleteNotice(Map<String, Object> params);
}
