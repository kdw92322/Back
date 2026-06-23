package com.example.back.mapper.video;

import com.example.back.model.VideoGallery;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoGalleryMapper {
    int getNextVideoId();

    int insertVideo(VideoGallery video);

    List<VideoGallery> selectVideoList();

    VideoGallery selectVideoList(Map<String, Object> params);

    int deleteVideo(int id);
}