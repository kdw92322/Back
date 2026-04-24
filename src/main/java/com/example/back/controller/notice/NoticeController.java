package com.example.back.controller.notice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.back.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 작성을 위한 초기 데이터 조회
     * 프론트엔드에서 등록 폼을 열 때 호출하여 작성자(writer) 필드를 로그인 유저 아이디로 초기화합니다.
     */
    @GetMapping("/init")
    public Map<String, Object> getNoticeInitData() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> initData = new HashMap<>();
        initData.put("writer", userId);
        return initData;
    }

    /**
     * 공지사항 목록 조회
     */
    @GetMapping("/select")
    public List<Map<String, Object>> getNoticeList(@RequestParam Map<String, Object> params) {
        return noticeService.getNoticeList(params);
    }

    /**
     * 공지사항 저장 (등록 및 수정 통합 처리)
     * AuthAspect에 의해 'inserts', 'updates' 내의 데이터에 사용자 ID가 자동 주입됩니다.
     */
    @PostMapping("/insert")
    public void saveNotice(@ModelAttribute Map<String, Object> params) {
        noticeService.saveNotice(params);
    }

    /**
     * 공지사항 개별 수정
     */
    @PostMapping("/update")
    public void updateNotice(@ModelAttribute Map<String, Object> params) {
        noticeService.updateNotice(params);
    }

     /**
     * 공지사항 조회 수 증가
     */
    @PostMapping("/updateViewCount")
    public void updateViewCount(@RequestBody Map<String, Object> params) {
        noticeService.updateViewCount(params);
    }

    /**
     * 공지사항 삭제
     */
    @PostMapping("/delete")
    public void deleteNotice(@RequestBody Map<String, Object> params) {
        noticeService.deleteNotice(params);
    }

    @GetMapping("/test")
    public List<Map<String, Object>> test(@RequestParam Map<String, Object> params) {
        return noticeService.getNoticeList(params);
    }
}
