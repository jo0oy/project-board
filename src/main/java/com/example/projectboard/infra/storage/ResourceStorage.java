package com.example.projectboard.infra.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceStorage {

    String upload(MultipartFile file, String storeFilename) throws IllegalAccessException;

    Resource download(String filename) throws IOException;

    void delete(String filename);
}
