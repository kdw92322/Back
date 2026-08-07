package com.example.back.service.schedule.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.schedule.ScheduleMapper;
import com.example.back.service.schedule.ScheduleService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public List<Map<String, Object>> selectScheduleList(Map<String, Object> params) {
        return scheduleMapper.selectScheduleList(params);
    }

    @Override
    public int save(Map<String, Object> saveMap) {
        System.out.println("saveMap : " + saveMap);
        int rtnVal = 0;

        try {
            if (saveMap.get("id") != null && !saveMap.get("id").toString().equals("")) {
                scheduleMapper.update(saveMap);
            } else {
                scheduleMapper.insert(saveMap);
            }
            rtnVal = 1;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("스케줄 저장 중 오류가 발생했습니다.");
        }

        return rtnVal;
    }

    @Override
    public int delete(Map<String, Object> deleteMap) {
        return scheduleMapper.delete(deleteMap);
    }
}
