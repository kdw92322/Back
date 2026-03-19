package com.example.back.controller.user;

import com.example.back.service.user.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/select")
    public List<Map<String,Object>> select(@RequestParam Map<String, Object> request) {
        System.out.println("Received request: " + request);
        List<Map<String,Object>> result = userService.selectUserList(request);
        return result;
    }
}
