package com.example.back.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.back.service.user.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/select")
    public List<Map<String,Object>> selectUserList(@RequestParam Map<String,Object> paramMap){  
        return userService.selectUserList(paramMap);
    }

    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> saveMap) { 
        return userService.insert(saveMap);
    }
    @PutMapping("/update")
    public int update(@RequestBody Map<String, Object> saveMap) {
        return userService.update(saveMap);
    }
    @PostMapping("/delete")
    public int delete(@RequestBody Map<String, Object> saveMap) {
        return userService.delete(saveMap);
    }
}