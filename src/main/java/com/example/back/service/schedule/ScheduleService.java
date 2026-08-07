package com.example.back.service.schedule;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    public List<Map<String, Object>> selectScheduleList(Map<String, Object> params);

    public int save(Map<String, Object> saveMap);

    public int delete(Map<String, Object> deleteMap);
}
