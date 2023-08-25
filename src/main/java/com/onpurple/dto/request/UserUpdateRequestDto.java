package com.project.date.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
