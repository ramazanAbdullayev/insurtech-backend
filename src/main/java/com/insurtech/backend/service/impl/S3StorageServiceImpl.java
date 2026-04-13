package com.insurtech.backend.service.impl;

import com.insurtech.backend.exception.StorageServiceException;
import com.insurtech.backend.exception.handler.ErrorCode;
import com.insurtech.backend.service.StorageService;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements StorageService {
  private final S3Template s3Template;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${spring.cloud.aws.s3.presigned-url.ttl-minutes:30}")
  private int presignedUrlTtlMinutes;

  public String upload(String claimNumber, MultipartFile file) {
    String s3Key = generateFileKey(claimNumber, file);
    ObjectMetadata metadata =
        ObjectMetadata.builder()
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();
    try {
      s3Template.upload(bucketName, s3Key, file.getInputStream(), metadata);
      log.info("File uploaded to storage (S3) successfully. claimNumber: {}", claimNumber);
    } catch (IOException e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when reading file: " + e);
    } catch (S3Exception e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when uploading file: " + e);
    }

    return s3Key;
  }

  public InputStream download(String fileKey) {
    try {
      log.info("Downloading file from S3. fileKey: {}", fileKey);
      return s3Template.download(bucketName, fileKey).getInputStream();
    } catch (IOException e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when reading file");
    } catch (S3Exception e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when downloading file");
    }
  }

  public void delete(String fileKey) {
    try {
      s3Template.deleteObject(bucketName, fileKey);
      log.info("File deleted from storage service (S3) successfully. fileKey: {}", fileKey);
    } catch (S3Exception e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when deleting file: " + e);
    }
  }

  public String getPresignedUrl(String fileKey) {
    try {
      return s3Template
          .createSignedGetURL(bucketName, fileKey, Duration.ofMinutes(presignedUrlTtlMinutes))
          .toString();
    } catch (S3Exception e) {
      throw new StorageServiceException(
          ErrorCode.STORAGE_SERVICE_ERROR, "Something went wrong when getting presign url: " + e);
    }
  }

  private String generateFileKey(String claimNumber, MultipartFile file) {
    String extension = getExtension(file.getOriginalFilename());
    String uniqueId = UUID.randomUUID().toString();

    // claims/{claimNumber}/{uniqueId}.{ext}
    return String.format("claims/%s/%s.%s", claimNumber, uniqueId, extension);
  }

  private String getExtension(String originalFileName) {
    if (Objects.isNull(originalFileName) || !originalFileName.contains(".")) return "bin";
    return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
  }
}
