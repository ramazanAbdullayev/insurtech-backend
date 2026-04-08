package com.insurtech.backend.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

  private final ErrorCode errorCode;

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
  }

  public NotFoundException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
