package com.nosqlcourse.service.impl.proxy;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IUserService;
import com.nosqlcourse.service.impl.UserService;

import java.util.List;
import java.util.Objects;

public class UserProxyService implements IUserService {
    private final UserService userService;
    private final User initiator;

    public UserProxyService(UserService userService, User initiator) {
        this.userService = userService;
        this.initiator = initiator;
    }

    @Override
    public List<User> getUsers() throws DataNotFoundException {
        if(initiator!=null){
            return userService.getUsers();
        } else throw new AccessDeniedException("Access denied in getUsers() for unauthorized initiator");
    }

    @Override
    public List<User> getUsersByName(String name){
        if(initiator!=null){
            return userService.getUsersByName(name);
        } else throw new AccessDeniedException("Access denied in getUsersByName() for unauthorized initiator");
    }

    @Override
    public User getUserByEmail(String email){
        return userService.getUserByEmail(email);
    }

    @Override
    public User getUserById(Long id){
        if(initiator!=null){
            return userService.getUserById(id);
        } else throw new AccessDeniedException("Access denied in getUserById() for unauthorized initiator");
    }

    @Override
    public Long insertUser(User user) {
        return userService.insertUser(user);
    }

    @Override
    public boolean updateUser(User user){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)
                || (initiator.getRole().getName().equals(ProxyAccess.USER_ROLE_NAME)
                && Objects.equals(user.getId(), initiator.getId())) ){
            return userService.updateUser(user);
        } else throw new AccessDeniedException("Access denied in updateUser() for initiator " + initiator.getEmail());
    }
}
