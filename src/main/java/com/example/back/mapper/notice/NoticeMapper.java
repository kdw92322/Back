package com.example.back.mapper.notice;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {
    
    List<Map<String, Object>> getNoticeList(Map<String, Object> params);
    
    void createNotice(Map<String, Object> params);
    
    void updateNotice(Map<String, Object> params);
    
    void deleteNotice(Map<String, Object> params);

	void updateViewCount(Map<String, Object> params);
    
}