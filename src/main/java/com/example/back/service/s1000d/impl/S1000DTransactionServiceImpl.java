package com.example.back.service.s1000d.impl;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.example.back.mapper.s1000d.S1000DMapper;
import com.example.back.service.s1000d.S1000DTransactionService;

import net.sf.jcgm.core.CGM;
import net.sf.jcgm.core.CGMDisplay;

@Service
public class S1000DTransactionServiceImpl implements S1000DTransactionService {

    @Autowired
    private S1000DMapper s1000DMapper;

    @Override
    public void insertCsdbMasterInfo(Map<String, Object> csdbInfo){
        s1000DMapper.insertCsdbInfo(csdbInfo);
    }

    @Override
    public void processAndInsertFileInfo(String uuid, String fileName, String filePathStr, byte[] fileBytesArray) throws IOException {
        String fileNameLower = fileName.toLowerCase();
        Path filePath = Paths.get(filePathStr);

        // =========================================================================
        // 1. 기존 XML 파일 처리 로직 (원형 보존)
        // =========================================================================
        if (fileNameLower.endsWith(".xml")) {
            String dmcId = fileName.substring(0, fileName.lastIndexOf("."));
            String xmlContent = new String(fileBytesArray, StandardCharsets.UTF_8);

            if (xmlContent.startsWith("\ufeff")) {
                xmlContent = xmlContent.substring(1);
            }
            xmlContent = xmlContent.trim();
            xmlContent = xmlContent.replaceFirst("^([\\W]+)<", "<");

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("csdb_id", uuid);
            fileInfo.put("dmcId", dmcId);
            fileInfo.put("xmlContent", xmlContent);
            fileInfo.put("filePath", filePathStr);

            //s1000DMapper.insertFileInfo(fileInfo);
        } 
        // =========================================================================
        // 2. ✨ 요구하신 이미지(ICN) 파일 처리 로직 (기존 비즈니스 로직 그대로 복원)
        // =========================================================================
        else if (isIcnFile(fileNameLower)) {
            String icnId = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

            // Case A: 순수 SVG 파일인 경우 (이미 외부에서 썼으므로 메커니즘 유지 혹은 추가 가공 시 활용)
            if (fileNameLower.endsWith(".svg")) {
                // 기존 로직: Files.write(filePath, fileBytes, ...); 
                // 트랜잭션 진입 전 외곽 루프에서 이미 썼으나 트랜잭션 정합성을 위해 덮어쓰기 유지 가능
                Files.write(filePath, fileBytesArray, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } 
            // Case B: CGM 벡터 그래픽 파일인 경우 ➡️ SVG 바이트로 컨버팅 후 저장
            else if (fileNameLower.endsWith(".cgm")) {
                byte[] processedBytes = convertCgmToSvgBytes(fileBytesArray); // 기존 CGM 변환 함수 호출
                String targetFileName = icnId + ".svg";
                Path svgPath = filePath.getParent().resolve(targetFileName);
                Files.write(svgPath, processedBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } 
            // Case C: 일반 래스터 이미지(PNG, JPG 등)인 경우 ➡️ SVG 벡터 바이트로 컨버팅 후 저장
            else if (isSupportedRaster(fileNameLower)) {
                byte[] processedBytes = convertToSvg(fileBytesArray, fileName); // 기존 래스터 변환 함수 호출
                String targetFileName = icnId + ".svg";
                Path svgPath = filePath.getParent().resolve(targetFileName);
                Files.write(svgPath, processedBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

    /**
     * SVG로 변환 가능한 래스터 이미지 포맷인지 확인합니다.
     */
    private boolean isSupportedRaster(String fileNameLower) {
        return fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") ||
                fileNameLower.endsWith(".jpeg") || fileNameLower.endsWith(".gif") || fileNameLower.endsWith(".cgm");
    }

    /**
     * 이미지 파일을 SVG 형식으로 전환합니다.
     * Raster 이미지(PNG, JPG 등)의 경우 브라우저 호환성을 위해 SVG <image> 태그로 래핑합니다.
     */
    private byte[] convertToSvg(byte[] fileBytes, String fileName) {
        String fileNameLower = fileName.toLowerCase();

        // 지원되는 래스터 이미지 확장자인 경우 처리
        if (fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") ||
                fileNameLower.endsWith(".jpeg") || fileNameLower.endsWith(".gif")) {
            try {
                String base64Content = Base64.getEncoder().encodeToString(fileBytes);
                String mimeType = fileNameLower.endsWith(".png") ? "image/png"
                        : (fileNameLower.endsWith(".gif") ? "image/gif" : "image/jpeg");

                String svgWrapper = String.format(
                        "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 800 600\">"
                                +
                                "<image width=\"100%%\" height=\"100%%\" xlink:href=\"data:%s;base64,%s\"/></svg>",
                        mimeType, base64Content);
                return svgWrapper.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.err.println("SVG 변환 중 오류 발생 (" + fileName + "): " + e.getMessage());
            }
        }
        // CGM, PDF 등 전용 변환 라이브러리가 필요한 포맷은 원본 데이터를 유지합니다.
        return fileBytes;
    }

    private byte[] convertCgmToSvgBytes(byte[] cgmBytes) throws IOException {
        if (cgmBytes == null || cgmBytes.length == 0) {
            throw new IllegalArgumentException("입력된 CGM 데이터가 비어 있습니다.");
        }

        // 1. DataInputStream 구조로 CGM 파싱
        CGM cgm = new CGM();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(cgmBytes);
            DataInputStream dis = new DataInputStream(bais)) {
            cgm.read(dis);
        }

        Dimension size = cgm.getSize();
        if (size == null) {
            size = new Dimension(800, 600); 
        }

        // 2. Apache Batik SVG DOM 명시적 구성
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://w3.org";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(size);

        // 3. CGMDisplay를 통해 Graphics2D 캔버스에 그리기
        CGMDisplay cgmDisplay = new CGMDisplay(cgm);
        cgmDisplay.paint(svgGenerator);

        // 4. 💡 [핵심 교정] 스트림 유실 및 인코딩 꼬임 원천 차단 구조
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) { // 💡 UTF_8 명시 보장
            
            // useCSS = false로 세팅하면 인라인 스타일로 빠져 XML 파서 에러 확률이 현격히 낮아집니다.
            boolean useCSS = false; 
            
            // Batik 내부 드로잉 내역을 Writer를 통해 스트림에 기록
            svgGenerator.stream(out, useCSS);
            
            // 💡 매우 중요: Writer 버퍼의 잔여 데이터를 완전히 바이트 배열로 밀어냄
            out.flush(); 
            baos.flush();
            
            return baos.toByteArray();
        } finally {
            svgGenerator.dispose();
        }
    }


    /**
     * 파일명이 S1000D ICN(이미지) 확장자인지 확인합니다.
     */
    private boolean isIcnFile(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".svg") || fileName.endsWith(".gif") || fileName.endsWith(".tif") ||
                fileName.endsWith(".tiff") || fileName.endsWith(".cgm") || fileName.endsWith(".pdf") ||
                fileName.endsWith(".cgm");
    }

}
