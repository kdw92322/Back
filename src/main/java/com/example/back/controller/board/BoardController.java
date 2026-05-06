package com.example.back.controller.board;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.board.BoardService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/board")
public class BoardController {
    
    @Autowired
    private BoardService boardService;

    @GetMapping("/select")
    public List<Map<String, Object>> select(@RequestParam Map<String, Object> param) {
        return boardService.select(param);
    }

    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> param) {
        return boardService.insert(param);
    }

    @PutMapping("/update")
    public int update(@RequestBody Map<String, Object> param) {
        return boardService.update(param);
    }
    
    @DeleteMapping("/delete")
    public int delete(@RequestBody Map<String, Object> param) {
        return boardService.delete(param);
    }
    
}
