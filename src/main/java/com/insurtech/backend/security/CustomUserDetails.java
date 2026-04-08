package com.insurtech.backend.security;

import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.domain.enums.UserRole;
import com.insurtech.backend.domain.enums.UserStatus;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(
    UUID id, String email, String password, Set<UserRole> roles, UserStatus status)
    implements UserDetails {
  public static CustomUserDetails from(User user) {
    return new CustomUserDetails(
        user.getId(), user.getEmail(), user.getPasswordHash(), user.getRoles(), user.getStatus());
  }

  @Override
  public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).toList();
  }

  @Override
  public @Nullable String getPassword() {
    return this.password;
  }

  @Override
  public @NonNull String getUsername() {
    return this.email;
  }

  @Override
  public boolean isEnabled() {
    return this.status == UserStatus.ACTIVE;
  }

  // Need to be implemented. (Optional)
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
