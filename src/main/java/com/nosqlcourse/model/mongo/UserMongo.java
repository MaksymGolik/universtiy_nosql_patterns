package com.nosqlcourse.model.mongo;

import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter @Setter
public class UserMongo {
    @BsonProperty("_id")
    private Long id;
    private String email;
    private String password;
    private String name;
    private String role;

    public static User toUser (UserMongo userMongo){
        User user = new User();
        user.setId(userMongo.getId());
        user.setEmail(userMongo.getEmail());
        user.setPassword(userMongo.getPassword());
        user.setName(userMongo.getName());
        Role role = new Role();
        role.setName(userMongo.getRole());
        user.setRole(role);
        return user;
    }
}
