package com.example.back.service.board;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.board.BoardMapper;

@Service
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;

    public List<Map<String, Object>> select(Map<String, Object> param) {
        return boardMapper.select(param);
    }

    public int insert(Map<String, Object> param) {
        int result = boardMapper.insert(param);
        return result;
    }

    public int update(Map<String, Object> param) {
        int result = boardMapper.update(param);
        return result;
    }

    public int delete(Map<String, Object> param) {
        int result = boardMapper.delete(param);
        return result;
    }

}
