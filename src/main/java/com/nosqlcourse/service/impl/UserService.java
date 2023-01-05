package com.nosqlcourse.service.impl;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.IUserDAO;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IUserService;

import java.util.List;

public class UserService implements IUserService {

    private final IUserDAO dao = DAOFactory.getDAOInstance(TypeDAO.MONGO).getUserDao();

    @Override
    public List<User> getUsers(){
        return dao.getUsers();
    }

    @Override
    public List<User> getUsersByName(String name){
        return dao.getUsersByName(name);
    }

    @Override
    public User getUserByEmail(String email){
        return dao.getUserByEmail(email);
    }

    @Override
    public User getUserById(Long id){
        return dao.getUserById(id);
    }

    @Override
    public Long insertUser(User user) {
        return dao.insertUser(user);
    }

    @Override
    public boolean updateUser(User user) {
        return dao.updateUser(user);
    }
}
