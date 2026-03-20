package com.insurtech.backend.dto.api.request;

import com.insurtech.backend.constants.enums.api.UserRole;
import com.insurtech.backend.constants.enums.api.UserStatus;
import jakarta.persistence.EnumeratedValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank
    @Size(max = 255, message = "Max 255 character(s)")
    private String firstName;

    @NotBlank
    @Size(max = 255, message = "Max 255 character(s)")
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 255, message = "Max 255 character(s)")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Min 8 character(s)")
    @Size(max = 64, message = "Max 64 character(s)")
    private String password;

    @NotNull
    @EnumeratedValue
    private UserStatus status;

    @NotNull
    @EnumeratedValue
    private UserRole role;
}
