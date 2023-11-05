package com.onpurple.domain.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "[a-zA-Z\\d]*${4,20}")
    private String password;

    @NotBlank
    private String passwordConfirm;

}
