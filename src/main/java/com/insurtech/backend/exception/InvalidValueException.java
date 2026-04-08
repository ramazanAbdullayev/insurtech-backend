package com.insurtech.backend.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends RuntimeException {

  private final ErrorCode errorCode;

  public InvalidValueException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
  }

  public InvalidValueException(ErrorCode code, String message) {
    super(message);
    this.errorCode = code;
  }
}
