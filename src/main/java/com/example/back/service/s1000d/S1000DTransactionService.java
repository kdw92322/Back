package com.example.back.service.s1000d;

import java.io.IOException;
import java.util.Map;

public interface S1000DTransactionService {
    public void insertCsdbMasterInfo(Map<String, Object> csdbInfo) throws IOException;

    public void processAndInsertFileInfo(String uuid, String fileName, String filePathStr, byte[] fileBytesArray) throws IOException;    
}
