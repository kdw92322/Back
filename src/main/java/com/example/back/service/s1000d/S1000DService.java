package com.example.back.service.s1000d;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface S1000DService {

    public SseEmitter createEmitter(String clientId);

    public List<Map<String, Object>> selectCsdbList(Map<String, Object> param) throws IOException;

    public Map<String, Object> xmlContent(Map<String, Object> param) throws IOException;

    public int deleteCsdb(Map<String, Object> param) throws IOException;

    public void storeUnzippedFiles(InputStream zipInputStream, String zipFileName, Long zipFileSize, SseEmitter emitter, byte[] fileBytes) throws IOException;

    public List<Map<String, Object>> selectPmc(Map<String, Object> param) throws IOException;

    public Map<String, Object> getXmlContentById(Map<String, Object> param) throws IOException;

    public Map<String, Object> convertStrToJson(Map<String, Object> param) throws IOException;

    public void storeUnzippedFilesAsync(byte[] fileBytes, String originalFilename, long zipFileSize, String clientId);

}
