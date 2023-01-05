package com.nosqlcourse.controller;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.dto.UserCreateRequest;
import com.nosqlcourse.dto.UserUpdateRequest;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IUserService;
import com.nosqlcourse.service.impl.UserService;
import com.nosqlcourse.service.impl.proxy.UserProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {
    private final IUserService userService = new UserService();

    @GetMapping("/users")
    public List<User> getUsers(@RequestHeader("email") String email,
                               @RequestHeader("password") String password){
       return new UserProxyService((UserService)userService,getUserByHeaderCredentials(email, password))
               .getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@RequestHeader("email") String email,
                        @RequestHeader("password") String password,
                        @PathVariable long id){
        return new UserProxyService((UserService)userService,getUserByHeaderCredentials(email, password))
                .getUserById(id);
    }

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser (@RequestHeader("email") String email,
                                         @RequestHeader("password") String password,
                                         @RequestBody UserCreateRequest userCreateRequest){
        User user = new User();
        Role role = new Role();
        role.setName(userCreateRequest.getRoleName());
        user.setRole(role);
        user.setEmail(userCreateRequest.getEmail());
        user.setPassword(userCreateRequest.getPassword());
        user.setName(userCreateRequest.getName());

        Long userId = userService.insertUser(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/users").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(userService.getUserById(userId));
    }

    @PatchMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestHeader("email") String email,
                                        @RequestHeader("password") String password,
                                        @RequestBody UserUpdateRequest userUpdateRequest){
        User user = UserUpdateRequest.toUser(userUpdateRequest);
        UserProxyService proxyService = new UserProxyService((UserService)userService,getUserByHeaderCredentials(email, password));
        proxyService.updateUser(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/users").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(proxyService.getUserById(user.getId()));
    }

    private User getUserByHeaderCredentials(String email, String password){
        User user = userService.getUserByEmail(email);
        if(user.getPassword().equals(password)) return user;
        return null;
    }
}
