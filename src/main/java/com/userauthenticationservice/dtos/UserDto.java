package com.userauthenticationservice.dtos;

import com.userauthenticationservice.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto {

    private String username;

    private Set<Role> roleSet = new HashSet<>();
}
