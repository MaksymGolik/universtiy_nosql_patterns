package com.nosqlcourse.controller;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.dto.UserCreateRequest;
import com.nosqlcourse.dto.UserUpdateRequest;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    private final DAOFactory dao = DAOFactory.getDAOInstance(TypeDAO.MONGO);

    @GetMapping("/users")
    public List<User> getUsers() throws DataNotFoundException {
       return dao.getUserDao().getUsers();
    }

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser (@RequestBody UserCreateRequest userCreateRequest) throws DataNotFoundException {
        User user = new User();
        Role role = new Role();
        role.setName(userCreateRequest.getRoleName());
        user.setRole(role);
        user.setEmail(userCreateRequest.getEmail());
        user.setPassword(userCreateRequest.getPassword());
        user.setName(userCreateRequest.getName());

        Long userId = dao.getUserDao().insertUser(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/users").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(dao.getUserDao().getUserById(userId));
    }

    @PatchMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) throws DataNotFoundException {
        User user = UserUpdateRequest.toUser(userUpdateRequest);
        dao.getUserDao().updateUser(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/users").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(dao.getUserDao().getUserById(user.getId()));
    }
}
