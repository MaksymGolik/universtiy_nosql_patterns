package com.nosqlcourse.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateRequest {
    Long id;
    String roleName;
    String email;
    String password;
    String name;

    public static User toUser(UserUpdateRequest userUpdateRequest){
        User user = new User();
        user.setId(userUpdateRequest.getId());
        user.setEmail(userUpdateRequest.getEmail());
        user.setPassword(userUpdateRequest.getPassword());
        user.setName(userUpdateRequest.getName());
        Role role = new Role();
        role.setName(userUpdateRequest.getRoleName());
        user.setRole(role);
        return user;
    }
}
