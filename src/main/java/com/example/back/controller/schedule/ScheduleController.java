package com.example.back.controller.schedule;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.schedule.ScheduleService;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("list")
    public List<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> result = scheduleService.selectScheduleList(params);

        return result;
    }

    @PostMapping("save")
    public int save(@RequestBody Map<String, Object> saveMap) {
        int result = scheduleService.save(saveMap);
        return result;
    }

    @PostMapping("delete")
    public int delete(@RequestBody Map<String, Object> deleteMap) {
        int result = scheduleService.delete(deleteMap);
        return result;
    }

}
