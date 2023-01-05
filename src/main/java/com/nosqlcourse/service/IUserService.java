package com.nosqlcourse.service;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.User;

import java.util.List;

public interface IUserService {
    List<User> getUsers();
    List<User> getUsersByName(String name);
    User getUserByEmail(String email);
    User getUserById(Long id);
    Long insertUser(User user);
    boolean updateUser(User user);
}
