package com.example.projectboard.infra.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.projectboard.infra.storage.ResourceStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Storage implements ResourceStorage {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * @description S3에 파일 업로드 로직 메서드
     * @param file : 업로드할 파일
     * @param storeFilename : fileService 에서 생성한 저장될 파일명
     * @return storeFilename
     */
    @Override
    public String upload(MultipartFile file, String storeFilename) throws IllegalAccessException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, storeFilename, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new IllegalAccessException(String.format("파일 변환 중에러가 발생하였습니다.(%s)", file.getOriginalFilename()));
        }

        return storeFilename;
    }

    /**
     * @description S3에 저장되어 있는 파일 다운로드 로직 메서드
     * @param filename : 다운로드할 파일명
     * @return ByteArrayResource
     */
    @Override
    public Resource download(String filename) throws IOException {
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucketName, filename));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        return new ByteArrayResource(bytes);
    }

    /**
     * @description S3에 저장되어 있는 파일 삭제 메서드
     * @param filename : 삭제할 파일명
     */
    @Override
    public void delete(String filename) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, filename));
    }
}
