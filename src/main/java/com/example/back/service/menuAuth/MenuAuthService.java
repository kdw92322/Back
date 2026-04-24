package com.example.back.service.menuAuth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.menuAuth.MenuAuthMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MenuAuthService {

    @Autowired
    private MenuAuthMapper menuAuthMapper;

    public List<Map<String, Object>> select(Map<String,Object> param) {
        return menuAuthMapper.select(param);
    }

    public int countAll() {
        return menuAuthMapper.countAll();
    }

    public int insert(Map<String,Object> saveMap) {
        return menuAuthMapper.insert(saveMap);
    }

    public int update(Map<String,Object> saveMap) {
        ObjectMapper mapper = new ObjectMapper();
        int result = 0;
        
        List<Map<String, Object>> auths = mapper.convertValue(
            saveMap.get("auths"),
            new TypeReference<List<Map<String, Object>>>() {}
        );

        for (Map<String, Object> auth : auths) {
            auth.put("updateBy", saveMap.get("updateBy"));
            System.out.println("auth : " + auth);

            if (auth.containsKey("use_yn")) {
                auth.put("useYn", auth.get("use_yn"));
            }

            if (auth.containsKey("c_yn")) {
                auth.put("cYn", auth.get("c_yn"));
            }

            if (auth.containsKey("r_yn")) {
                auth.put("rYn", auth.get("r_yn"));
            }

            if (auth.containsKey("d_yn")) {
                auth.put("dYn", auth.get("d_yn"));
            }

            List<Map<String, Object>> list = select(auth);    
            int size = list.size();
            if (size == 0) {
                insert(auth);
            } else {
                result += menuAuthMapper.update(auth);
            }

        }

        return result;
    }

}