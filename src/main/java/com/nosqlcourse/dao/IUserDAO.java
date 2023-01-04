package com.nosqlcourse.dao;

import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.User;

import java.util.List;

public interface IUserDAO {
    List<User> getUsers() throws DataNotFoundException;
    List<User> getUsersByName(String name) throws DataNotFoundException;
    User getUserByEmail(String email) throws DataNotFoundException;
    User getUserById(Long id) throws DataNotFoundException;
    Long insertUser(User user);
    boolean updateUser(User user);
}