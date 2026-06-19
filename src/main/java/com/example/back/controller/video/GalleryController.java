package com.example.back.controller.video;

import com.example.back.model.VideoGallery;
import com.example.back.service.video.VideoGalleryService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gallery")
public class GalleryController {
    
    private final VideoGalleryService videoGalleryService;

    public GalleryController(VideoGalleryService videoGalleryService) {
        this.videoGalleryService = videoGalleryService;
    }

    @GetMapping("/select")
    public ResponseEntity<?> select() {
        List<VideoGallery> list = videoGalleryService.getVideoList();
        return ResponseEntity.ok(list);
    }

    /**
     * 비디오 썸네일 이미지를 반환하는 엔드포인트
     * @param params 동영상 식별자 (id)
     * @return 썸네일 이미지 파일
     */
    @GetMapping("/thumbnail")
    public ResponseEntity<FileSystemResource> getThumbnail(@RequestParam Map<String, Object> params) throws IOException {
        VideoGallery vg = videoGalleryService.getVideoById(params);
        if (vg == null || vg.getFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        // thumbnail 필드에는 웹 URL이 저장되어 있으므로, 물리 파일 경로는 비디오 경로(fileUrl)를 기반으로 유추합니다.
        File videoFile = new File(vg.getFileUrl());
        String fileName = videoFile.getName();
        String thumbName = fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
        File thumbFile = new File(videoFile.getParentFile(), "thumbnail/" + thumbName);
        
        if (!thumbFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(thumbFile);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // VideoGalleryService에서 jpg로 저장함
                .body(resource);
    }

    /**
     * 비디오 전체 파일을 한 번에 응답하는 엔드포인트
     * 브라우저가 전체 파일을 다운로드하여 로컬 캐시에 저장하게 함으로써 
     * 재생 중 버퍼링이 발생하는 것을 방지합니다.
     * @param params 동영상 식별자 (id)
     */
    @GetMapping("/view")
    public ResponseEntity<FileSystemResource> viewVideo(@RequestParam Map<String, Object> params) throws IOException {
        VideoGallery vg = videoGalleryService.getVideoById(params);
        if (vg == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(vg.getFileUrl());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                // 브라우저 캐싱을 유도하여 다음 접속 시 더 빠르게 로드되도록 설정
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") 
                .contentType(MediaTypeFactory.getMediaType(resource)
                        .orElse(MediaType.parseMediaType("video/mp4")))
                .contentLength(file.length())
                .body(resource);
    }

     /**
     * 동영상 스트리밍 엔드포인트
     * @param id 동영상 식별자
     * @param headers HTTP 헤더 (Range 및 Authorization 포함)
     * @return 206 Partial Content와 함께 비디오 데이터의 일부 반환
     */
    @GetMapping("/video")
    public ResponseEntity<ResourceRegion> streamVideo(@RequestParam Map<String, Object> params, @RequestHeader HttpHeaders headers) throws IOException {
        VideoGallery vg = videoGalleryService.getVideoById(params);

        File file = new File(vg.getFileUrl());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 2. Spring의 FileSystemResource 사용 (메모리에 다 올리지 않고 스트림으로 읽음)
        FileSystemResource videoResource = new FileSystemResource(file);
        long contentLength = videoResource.contentLength();

        // 3. 브라우저가 요청한 Range(구간) 확인
        HttpRange range = headers.getRange().stream().findFirst().orElse(null);

        ResourceRegion region;
        // 쪼개서 보낼 조각 크기 설정 (100MB~1GB 영상 기준 1MB~2MB가 적당)
        long chunkSize = (long) (1024 * 1024 * 20); // 20MB

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            // 처음 요청하거나 Range 헤더가 없을 때 기본적으로 처음부터 20MB 전송
            long rangeLength = Long.min(chunkSize, contentLength);
            region = new ResourceRegion(videoResource, 0, rangeLength);
        }

        // 4. HTTP 206 Partial Content 상태코드로 리턴
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaTypeFactory.getMediaType(videoResource)
                    .orElse(MediaType.parseMediaType("video/mp4"))) // 기본 mp4 설정
            .body(region);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        try {
            VideoGallery savedVideo = videoGalleryService.uploadVideo(file, title, description);

            // 프론트엔드에서 활용할 수 있도록 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", savedVideo.getId());
            response.put("title", savedVideo.getTitle());
            response.put("fileUrl", savedVideo.getFileUrl());
            response.put("message", "동영상이 성공적으로 업로드되었습니다.");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> params) {
        String id = String.valueOf(params.get("id"));
        System.out.println("Requested delete ID: " + id);

        videoGalleryService.deleteVideo(id);

        return ResponseEntity.ok().build();
    }
}
