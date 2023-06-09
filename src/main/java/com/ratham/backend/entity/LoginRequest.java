package com.ratham.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @JsonProperty("universityId")
    private String universityId;
    @JsonProperty("password")
    private String password;
}

