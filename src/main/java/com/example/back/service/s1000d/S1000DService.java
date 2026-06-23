package com.example.back.service.s1000d;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface S1000DService {

    public List<Map<String, Object>> selectCsdbList(Map<String, Object> param) throws IOException;

    public int deleteCsdb(Map<String, Object> param) throws IOException;

    public void storeUnzippedFiles(MultipartFile zipFile) throws IOException;

    public List<Map<String, Object>> selectPmc(Map<String, Object> param) throws IOException;

    public Map<String, Object> getXmlContentById(Map<String, Object> param) throws IOException;

    public Map<String, Object> convertStrToJson(Map<String, Object> param) throws IOException;

}
