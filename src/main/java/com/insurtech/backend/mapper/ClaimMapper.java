package com.insurtech.backend.mapper;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    List<ClaimResponse> toResponseList(List<Claim> claims);

    ClaimResponse toResponse(Claim claim);

    Claim toEntity(ClaimRequest claimRequest);
}
