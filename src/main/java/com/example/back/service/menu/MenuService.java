package com.example.back.service.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.mapper.menu.MenuMapper;
import com.example.back.mapper.menuAuth.MenuAuthMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuAuthMapper menuAuthMapper;

    public List<Map<String, Object>> selectMenuList(Map<String, Object> paramsMap) {
        List<Map<String, Object>> menuList= menuMapper.selectMenuList(paramsMap);
        return menuList;
    }

    public int insert(Map<String, Object> saveMap){
        try {
            String newMenuCode = menuMapper.createNewMenuCode();

            if(saveMap.get("role_id") != null){
                String role_id = String.valueOf(saveMap.get("role_id"));
                Map<String, Object> newAuthMap = new HashMap<>();
                newAuthMap.put("menu_code", newMenuCode);
                newAuthMap.put("role_id", role_id);
                newAuthMap.put("cYn", "Y");
                newAuthMap.put("rYn", "Y");
                newAuthMap.put("dYn", "Y");
                newAuthMap.put("useYn", "Y");

                menuAuthMapper.insert(newAuthMap);
            }

            saveMap.put("code", newMenuCode);
            saveMap.put("level", "1");
            menuMapper.insert(saveMap);

            return 0;
        } catch (Exception e) {
            throw new RuntimeException("메뉴 등록 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public int update(Map<String, Object> saveMap){
        System.out.println(saveMap);
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
            insert.put("level", "2");
            System.out.println("insert : " + insert);
            menuMapper.insert(insert);
        }

        //2. update 
        List<Map<String, Object>> updates = mapper.convertValue(
            saveMap.get("updates"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for(Map<String, Object> update : updates){
            System.out.println("update : " + update);
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