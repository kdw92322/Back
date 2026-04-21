package com.example.back.config;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

public class CamelCaseMapWrapper extends MapWrapper {
    public CamelCaseMapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping && name != null && name.contains("_")) {
            return toCamelCase(name);
        }
        return name;
    }

    private String toCamelCase(String input) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : input.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }
}