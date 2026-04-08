package com.insurtech.backend.service;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String upload(String claimNumber, MultipartFile file);

  InputStream download(String key);

  void delete(String key);

  String getPresignedUrl(String key);
}
