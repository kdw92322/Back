package com.example.back.controller.board;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/board")
public class BoardController {
    
    @GetMapping("select")
    public List<Map<String, Object>> select(@RequestParam String param) {
        return null;
    }

    @PostMapping("insert")
    public void postMethodName(@RequestBody String entity) {
        
    }

    @PutMapping("update")
    public void putMethodName(@PathVariable String id, @RequestBody String entity) {

    }
    
    @DeleteMapping("delete")
    public void deleteMethodName(@RequestBody String id) {

    }
    
}
