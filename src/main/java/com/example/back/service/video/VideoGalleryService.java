package com.example.back.service.video;

import com.example.back.config.SystemUrlProvider;
import com.example.back.mapper.video.VideoGalleryMapper;
import com.example.back.model.VideoGallery;
import com.example.back.util.SecurityUtil;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VideoGalleryService {

    private final SystemUrlProvider systemUrlProvider;

    private final VideoGalleryMapper videoGalleryMapper;
    private final String uploadRoot;

    public VideoGalleryService(VideoGalleryMapper videoGalleryMapper,
                               @Value("${gallery.video.upload-dir}") String uploadRoot,
                               SystemUrlProvider systemUrlProvider) {
        this.videoGalleryMapper = videoGalleryMapper;
        this.uploadRoot = uploadRoot;
        this.systemUrlProvider = systemUrlProvider;
    }

    public List<VideoGallery> getVideoList() {
        return videoGalleryMapper.selectVideoList();
    }

    /**
     * 비디오 ID로 단일 비디오 정보를 조회합니다.
     * @param id 비디오 PK
     * @return 조회된 VideoGallery 객체
     */
    public VideoGallery getVideoById(Map<String, Object> params) {
        return videoGalleryMapper.selectVideoList(params);
    }

    @Transactional
    public VideoGallery uploadVideo(MultipartFile file, String title, String description) throws IOException {
        String userId = SecurityUtil.getCurrentUserId();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 선택되지 않았습니다.");
        }

        Path uploadPath = Paths.get(uploadRoot, "gallery").toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path targetLocation = uploadPath.resolve(savedFileName);

        // 파일 저장
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        VideoGallery video = new VideoGallery();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoType("LOCAL");
        video.setCreatedBy(userId);
        video.setFileUrl(targetLocation.toString());

        int nextId = videoGalleryMapper.getNextVideoId();
        video.setId(nextId);

        // javacv를 이용한 메타데이터 추출 및 썸네일 생성
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(targetLocation.toFile())) {
            grabber.start();
            
            // 1. 해상도 및 화질 등급 추출
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();

            String views = getVideoQualityLabel(width, height);
            video.setViews(views);
            
            // 2. 재생 시간 추출 (Microseconds -> HH:mm:ss)
            long durationMicroSec = grabber.getLengthInTime();
            long totalSeconds = durationMicroSec / 1000000;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            String duration  = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            video.setDuration(duration);

            // 3. 10% 지점에서 썸네일 캡처
            grabber.setTimestamp((long) (durationMicroSec * 0.1));
            Frame frame = grabber.grabImage();
            
            if (frame != null) {
                Path thumbDir = uploadPath.resolve("thumbnail");
                if (!Files.exists(thumbDir)) {
                    Files.createDirectories(thumbDir);
                }

                String thumbFileName = savedFileName.substring(0, savedFileName.lastIndexOf(".")) + ".jpg";
                Path thumbPath = thumbDir.resolve(thumbFileName);
                
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(frame);

                if (bi != null) {
                    ImageIO.write(bi, "jpg", thumbPath.toFile());

                    String system_url = systemUrlProvider.getCurrentSystemUrl();    
                    String thumbUrl = system_url + "/gallery/thumbnail?id=" + nextId;
                    video.setThumbnail(thumbUrl);
                }
            }
            grabber.stop();
        } catch (Exception e) {
            System.err.println("메타데이터 추출 중 오류 발생: " + e.getMessage());
        }

        videoGalleryMapper.insertVideo(video);
        return video;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId) {
        Map<String, Object> params = Map.of("id", videoId);
        VideoGallery vg = getVideoById(params);

        if (vg != null) {
            // 1. 데이터베이스에서 비디오 정보 삭제 (트랜잭션 보장)
            videoGalleryMapper.deleteVideo(videoId);

            // 2. 실제 저장된 물리 파일 삭제 (비디오 및 썸네일)
            String fileUrl = vg.getFileUrl();
            deletePhysicalFile(fileUrl);
            
            // 썸네일 물리 파일 경로 유추 및 삭제
            if (fileUrl != null) {
                java.io.File videoFile = new java.io.File(fileUrl);
                String fileName = videoFile.getName();
                if (fileName.contains(".")) {
                    String thumbName = fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
                    java.io.File thumbFile = new java.io.File(videoFile.getParentFile(), "thumbnail/" + thumbName);
                    deletePhysicalFile(thumbFile.getAbsolutePath());
                }
            }
        }
    }

    private void deletePhysicalFile(String filePathStr) {
        if (filePathStr == null || filePathStr.isEmpty()) return;
        
        Path path = Paths.get(filePathStr);
        try {
            boolean deleted = Files.deleteIfExists(path);
            System.out.println("File deleted (" + path + "): " + deleted);
        } catch (IOException e) {
            System.err.println("Failed to delete file (" + path + "): " + e.getMessage());
        }
    }

    // 해상도 기준 화질 판별 메서드
    public static String getVideoQualityLabel(int width, int height) {
        int longSide = Math.max(width, height); // 가로/세로 중 긴 축 기준 (회전 고려)

        if (longSide >= 7680) return "8K UHD";
        if (longSide >= 3840) return "4K UHD";
        if (longSide >= 2560) return "2K (QHD)";
        if (longSide >= 1920) return "1080p (FHD)";
        if (longSide >= 1280) return "720p (HD)";
        return "SD 이하";
    }
}