package com.example.back.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.service.authMng.AuthMngService;
import com.example.back.service.menu.MenuService;
import com.example.back.service.menuAuth.MenuAuthService;

@Component
public class initSetup implements ApplicationRunner{
    
    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthMngService authMngService;

    @Autowired
    private MenuAuthService menuAuthService;

    @Autowired
    private SecurityService securityService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Application started with option names : " + args.getOptionNames());
        
        //1. 메뉴 초기설정
        initMenu();

        //2. 권한 초기설정
        initAuth();
    }

    //1. 메뉴 초기설정
    private void initMenu() {
        // 계층형 메뉴 데이터 구성
        List<Map<String, Object>> menuGroups = new ArrayList<>();
        
        // 1. 시스템 관리
        Map<String, Object> group1 = new HashMap<>();
        group1.put("title", "시스템 관리");
        group1.put("items", List.of(
            Map.of("name", "사용자 관리", "path", "/users", "viewpath", "./sys/UserManagement", "module", "module.UserManagement"),
            Map.of("name", "메뉴 관리", "path", "/menu", "viewpath", "./sys/MenuManagement", "module", "module.MenuManagement"),
            Map.of("name", "권한 관리", "path", "/auth", "viewpath", "./sys/AuthManagement", "module", "module.AuthManagement"),
            Map.of("name", "권한에 대한 접근관리", "path", "/menuauth", "viewpath", "./sys/MenuAuthManagement", "module", "module.MenuAuthManagement"),
            Map.of("name", "코드그룹 관리", "path", "/codeGroup", "viewpath", "./sys/CodeGrpManagement", "module", "module.CodeGrpManagement"),
            Map.of("name", "코드 관리", "path", "/code", "viewpath", "./sys/CodeManagement", "module", "module.CodeManagement")
        ));
        menuGroups.add(group1);
        
        // 2. 게시판 관리
        
        Map<String, Object> group2 = new HashMap<>();
        group2.put("title", "Notice");
        group2.put("items", List.of(
            Map.of("name", "게시판", "path", "/board", "viewpath", "./notice/Board", "module", "module.Board"),
            Map.of("name", "공지사항", "path", "/notice", "viewpath", "./notice/Notice", "module", "module.Notice")
        ));
        menuGroups.add(group2);
        

        int codeSeq = 1;
        for (int i = 0; i < menuGroups.size(); i++) {
            Map<String, Object> group = menuGroups.get(i);
            String groupTitle = (String) group.get("title");
            List<Map<String, String>> items = (List<Map<String, String>>) group.get("items");

            // 1) 상위 메뉴(그룹) Insert
            String parentCode = "M" + String.format("%03d", codeSeq++);
            System.out.println("parentCode: " + parentCode);
            Map<String, Object> parentMap = new HashMap<>();
            parentMap.put("code", parentCode);
            parentMap.put("parentcode", "ROOT");
            parentMap.put("name", groupTitle);
            parentMap.put("path", "/");
            parentMap.put("order", String.valueOf(i + 1));
            parentMap.put("level", "1");
            parentMap.put("useYn", "Y");
            parentMap.put("createBy", "SYSTEM");

            Map<String, Object> parentParam = new HashMap<>();
            parentParam.put("code", parentCode);
            List<Map<String, Object>> parentList = menuService.selectMenuList(parentParam);

            try {
                if (parentList == null || parentList.isEmpty()) {
                    menuService.insert(parentMap);
                }
            } catch (Exception e) {
                System.out.println("상위 메뉴 저장 실패 (" + groupTitle + "): " + e.getMessage());
            }

            // 2) 하위 메뉴(아이템) Insert
            for (int j = 0; j < items.size(); j++) {
                Map<String, String> item = items.get(j);
                String itemName = item.get("name");
                String itemPath = item.get("path");
                String viewpath = item.get("viewpath");
                String module = item.get("module");
                String childCode = "M" + String.format("%03d", codeSeq++);
                
                Map<String, Object> childMap = new HashMap<>();
                childMap.put("code", childCode);
                childMap.put("parentcode", parentCode);
                childMap.put("name", itemName);
                childMap.put("path", itemPath);
                childMap.put("order", String.valueOf(j + 1));
                childMap.put("level", "2");
                childMap.put("useYn", "Y");
                childMap.put("viewPath", viewpath);
                childMap.put("module", module);
                childMap.put("createBy", "SYSTEM");

                Map<String, Object> childParam = new HashMap<>();
                childParam.put("code", childCode);
                List<Map<String, Object>> childList = menuService.selectMenuList(childParam);
                try {
                    if (childList == null || childList.isEmpty()) {
                        menuService.insert(childMap);
                    }
                } catch (Exception e) {
                    System.out.println("하위 메뉴 저장 실패 (" + itemName + "): " + e.getMessage());
                }
            }
        }
    }

    //2. 권한 초기설정
    private void initAuth() {
        // tb_menu_auth 테이블에 데이터가 없는 경우만 초기화 진행
        int totalCount = menuAuthService.countAll();
        if (totalCount > 0) {
            return;
        }

        List<Map<String, Object>> authMngList = authMngService.selectAuthMngList(null);
        List<Map<String, Object>> menuList = menuService.selectMenuList(null);
            
        if (authMngList == null || menuList == null) return;

        for(int i=0; i<authMngList.size(); i++){
            Map<String, Object> authMng = authMngList.get(i);
            String role_id = String.valueOf(authMng.get("role_id"));
            
            for(int j=0; j<menuList.size(); j++){
                Map<String, Object> menuAuth = new HashMap<>();
                Map<String, Object> menuInfo = menuList.get(j);
                
                String menucode = String.valueOf(menuInfo.get("code"));    
                String viewPath = String.valueOf(menuInfo.get("viewPath"));
                String parentcode = String.valueOf(menuInfo.get("parentcode"));

                menuAuth.put("role_id", role_id);
                menuAuth.put("menu_code", menucode);
                menuAuth.put("updateBy", "SYSTEM");
                
                if(role_id.equals("ROLE_ADMIN")){
                    menuAuth.put("cYn", "Y");
                    menuAuth.put("rYn","Y");
                    menuAuth.put("dYn","Y");
                    menuAuth.put("useYn", "Y");
                } else if(role_id.equals("ROLE_USER") || role_id.equals("ROLE_GUEST")){
                    if((viewPath.indexOf("sys") < 0)){
                        menuAuth.put("cYn", "Y");
                        menuAuth.put("rYn","Y");
                        menuAuth.put("dYn","Y");
                        menuAuth.put("useYn", "Y");
                    }
                } 
                menuAuthService.insert(menuAuth); 
            }        
        }
    }

}
