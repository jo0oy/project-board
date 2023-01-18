package com.example.projectboard.interfaces.api.v1.files;

import com.example.projectboard.application.uploadfiles.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@RestController
public class ArticleAttachedFileApiController {

    private final FileStorageService fileStorageService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IllegalAccessException {

        log.info("{} {}", HttpMethod.POST, "/api/v1/files");
        var uploadedFilename = fileStorageService.upload(file);

        return ResponseEntity.ok()
                .body("/api/v1/files/" + uploadedFilename);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable("filename") String filename,
                                          HttpServletRequest request) throws IOException {

        log.info("{} {}", HttpMethod.GET, "/api/v1/files/" + filename);

        var resource = fileStorageService.downloadFile(filename);

        // Content-Type 설정
        var contentType = request.getServletContext().getMimeType(filename);

        // Content-Type 알 수 없는 경우, default type(application/octet-stream)으로 설정
        if (contentType == null) {
            log.info("파일의 Content-Type 알 수 없으므로 디폴트값 적용.");
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable("filename") String filename) {

        log.info("{} {}", HttpMethod.DELETE, "/api/v1/files/" + filename);
        fileStorageService.delete(filename);

        return ResponseEntity.ok()
                .body("파일 삭제 성공");
    }
}
