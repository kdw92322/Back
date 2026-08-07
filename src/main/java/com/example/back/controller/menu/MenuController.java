package com.example.back.controller.menu;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.menu.MenuService;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("select")
    public List<Map<String, Object>> selectMenuList(@RequestParam Map<String, Object> paramsMap) {
        return menuService.selectMenuList(paramsMap);
    }

    @PostMapping("insert")
    public int insert(@RequestBody Map<String, Object> saveMap) {
        return menuService.insert(saveMap);
    }

    @PutMapping("update")
    public int update(@RequestBody Map<String, Object> saveMap) {
        return menuService.update(saveMap);
    }

    @PostMapping("delete")
    public int delete(@RequestBody Map<String, Object> delMap) {
        return menuService.delete(delMap);
    }

    @PostMapping("save")
    public int save(@RequestBody Map<String, Object> saveMap) {
        return menuService.save(saveMap);
    }
}
