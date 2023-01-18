package com.example.projectboard.application.uploadfiles;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    String upload(MultipartFile multipartFile) throws IllegalAccessException;

    Resource downloadFile(String storedFilename) throws IOException;

    void delete(String filename);
}
