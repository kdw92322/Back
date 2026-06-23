package com.example.back.service.video;

import com.example.back.model.VideoGallery;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VideoGalleryService {
    public List<VideoGallery> getVideoList();

    /**
     * 비디오 ID로 단일 비디오 정보를 조회합니다.
     * 
     * @param id 비디오 PK
     * @return 조회된 VideoGallery 객체
     */
    public VideoGallery getVideoById(Map<String, Object> params);

    public VideoGallery uploadVideo(MultipartFile file, String title, String description) throws IOException;

    public void deleteVideo(String videoId);
}