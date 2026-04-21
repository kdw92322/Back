package com.example.back.service.notice;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.mapper.notice.NoticeMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public List<Map<String, Object>> getNoticeList(Map<String, Object> params) {
        return noticeMapper.getNoticeList(params);
    }

    @Override
    @Transactional
    public void saveNotice(Map<String, Object> params) {
        // 1. 단일 데이터 등록/수정 처리
        if (params.get("id") == null && !params.containsKey("inserts")) {
            noticeMapper.createNotice(params);
        } else if (params.get("id") != null) {
            noticeMapper.updateNotice(params);
        }

        // 2. 대량 데이터(Grid 형태) 처리 - AuthAspect가 'inserts', 'updates' 리스트 내부까지 ID를 주입함
        if (params.containsKey("inserts") && params.get("inserts") instanceof List) {
            List<Map<String, Object>> inserts = (List<Map<String, Object>>) params.get("inserts");
            for (Map<String, Object> item : inserts) {
                noticeMapper.createNotice(item);
            }
        }
        if (params.containsKey("updates") && params.get("updates") instanceof List) {
            List<Map<String, Object>> updates = (List<Map<String, Object>>) params.get("updates");
            for (Map<String, Object> item : updates) {
                noticeMapper.updateNotice(item);
            }
        }
    }

    @Override
    public void updateNotice(Map<String, Object> params) {
        noticeMapper.updateNotice(params);
    }

    @Override
    public void updateViewCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        noticeMapper.updateViewCount(params);
    }

    @Override
    public void deleteNotice(Map<String, Object> params) {
        noticeMapper.deleteNotice(params);
    }

}