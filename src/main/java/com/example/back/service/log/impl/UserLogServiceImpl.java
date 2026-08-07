package com.example.back.service.log.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.log.UserLogMapper;
import com.example.back.service.log.UserLogService;

@Service
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    private UserLogMapper userLogMapper;

    @Override
    public void insertUserLog(Map<String, Object> insertMap) {
        userLogMapper.insertUserLog(insertMap);
    }

}
