package com.example.back.service.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.back.mapper.user.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<Map<String,Object>> selectUserList(Map<String,Object> paramMap){
        return userMapper.selectUserList(paramMap);        
    }

    public int insert(Map<String, Object> saveMap) {
        String pwd = String.valueOf(saveMap.get("password"));
        saveMap.put("password", passwordEncoder.encode(pwd));

        return userMapper.insert(saveMap);
    }
    public int update(Map<String, Object> saveMap) {
        String pwd = String.valueOf(saveMap.get("password"));
        saveMap.put("password", passwordEncoder.encode(pwd));

        return userMapper.update(saveMap);
    }
    public int delete(Map<String, Object> saveMap) {
        return userMapper.delete(saveMap);
    }
}