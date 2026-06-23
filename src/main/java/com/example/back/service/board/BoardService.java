package com.example.back.service.board;

import java.util.List;
import java.util.Map;

public interface BoardService {
    public List<Map<String, Object>> select(Map<String, Object> param);

    public int insert(Map<String, Object> param);

    public int update(Map<String, Object> param);

    public int delete(Map<String, Object> param);

}
