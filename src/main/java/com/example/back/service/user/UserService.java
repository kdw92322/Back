package com.example.back.service.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.user.UserMapper;

@Service
public class UserService {

    @Autowired
	private UserMapper usermapper;

    public List<Map<String,Object>> selectUserList(Map<String,Object> paramMap) {
        return usermapper.selectUserList(paramMap);
    }
}
