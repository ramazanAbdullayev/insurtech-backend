package com.insurtech.backend.exception;

public class S3PresignedUrlException extends RuntimeException {
  public S3PresignedUrlException(String message, Throwable cause) {
    super(message, cause);
  }
}
