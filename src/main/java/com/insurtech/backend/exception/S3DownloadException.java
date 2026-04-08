package com.insurtech.backend.exception;

import io.awspring.cloud.s3.S3Exception;

public class S3DownloadException extends S3Exception {
  public S3DownloadException(String message, Throwable cause) {
    super(message, cause);
  }
}
