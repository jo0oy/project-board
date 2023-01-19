package com.example.projectboard.application.uploadfiles;

import com.example.projectboard.infra.storage.ResourceStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleAttachedFileStorageService implements FileStorageService {

    private final ResourceStorage storage;

    // 게시글 첨부 파일 업로드 메서드
    @Override
    public String upload(MultipartFile multipartFile) throws IllegalAccessException {
        log.info("uploading multipartFile.... filename: {}", multipartFile.getOriginalFilename());

        validateFileExists(multipartFile);
        var storeFilename = createStoreFilename(multipartFile.getOriginalFilename());

        return storage.upload(multipartFile, storeFilename);
    }

    // 게시글 첨부 파일 다운로드 메서드
    @Override
    public Resource downloadFile(String storedFilename) throws IOException {
        log.info("downloading file.... filename: {}", storedFilename);
        validateFilename(storedFilename);

        return storage.download(storedFilename);
    }

    // 게시글 첨부 파일 삭제 메서드
    @Override
    public void delete(String filename) {
        log.info("deleting file.... filename: {}", filename);
        validateFilename(filename);

        storage.delete(filename);
    }

    // 파일명, 확장명 검증
    private void validateFilename(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            log.error("파일명이 올바르지 않습니다. filename: {}", storedFilename);
            throw new IllegalArgumentException("파일명이 올바르지 않습니다. filename: " + storedFilename);
        }

        var ext = extractExt(storedFilename);
        var extList = List.of("jpg", "jpeg", "png", "txt", "csv", "gif", "pdf");
        var flag = false;
        for (String s : extList) {
            if (ext.equalsIgnoreCase(s)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            log.error("올바르지 않은 파일 확장명입니다. ext: {}", ext);
            throw new IllegalArgumentException("올바르지 않은 파일 확장명입니다. ext: " + ext);
        }
    }

    // 스토리지에 저장할 파일명 생성
    private String createStoreFilename(String filename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(filename);

        return uuid + "." + ext;
    }

    // 파일 확장명 추출
    private String extractExt(String filename) {
        int pos = filename.lastIndexOf(".");
        if(pos < 0) throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다. filename: " + filename);
        return filename.substring(pos + 1);
    }

    // 파일 존재여부 확인
    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }
    }
}
