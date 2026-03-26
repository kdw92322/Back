package com.example.back.service.menu;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.menu.MenuMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;


    public List<Map<String, Object>> selectMenuList(Map<String, Object> paramsMap) {
        return menuMapper.selectMenuList(paramsMap);
    }

    public int insert(Map<String, Object> saveMap){
        String newMenuCode = menuMapper.createNewMenuCode();
        saveMap.put("code", newMenuCode);
        saveMap.put("level", "1");
        return menuMapper.insert(saveMap);
    }

    public int update(Map<String, Object> saveMap){
        return menuMapper.update(saveMap);
    }

    public int delete(Map<String, Object> delMap){
        return menuMapper.delete(delMap);
    }

    public int save(Map<String, Object> saveMap){
        System.out.println(saveMap);
        ObjectMapper mapper = new ObjectMapper();
        //1. insert
        List<Map<String, Object>> inserts = mapper.convertValue(
            saveMap.get("inserts"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for(Map<String, Object> insert : inserts){
            String newMenuCode = menuMapper.createNewMenuCode();
            insert.put("code", newMenuCode);
            saveMap.put("level", "2");
            menuMapper.insert(insert);
        }

        //2. update 
        List<Map<String, Object>> updates = mapper.convertValue(
            saveMap.get("updates"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for(Map<String, Object> update : updates){
            menuMapper.update(update);
        }

        //3. delete
        List<Map<String, Object>> deletes = mapper.convertValue(
            saveMap.get("deletes"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for(Map<String, Object> delete : deletes){
            menuMapper.delete(delete);
        }

        return 1;
    }
}