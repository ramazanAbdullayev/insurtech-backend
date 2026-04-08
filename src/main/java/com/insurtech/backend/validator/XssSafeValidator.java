package com.insurtech.backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XssSafeValidator implements ConstraintValidator<XssSafe, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) return true;

    return Jsoup.isValid(value, Safelist.none());
  }
}
