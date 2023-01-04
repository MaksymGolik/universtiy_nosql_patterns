package com.nosqlcourse.observer;

import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;

public class Guest extends User implements EventSubscriber {

    public Guest(){
        super();
    }

    public Guest(Long id, String email, String password, String name, Role role) {
        super(id, email, password, name, role);
    }

    public Guest(User user) {
        super(user.getId(), user.getEmail(), user.getPassword(), user.getName(), user.getRole());
    }

    @Override
    public void update(String notification) {
        System.out.println("Dear "+getName()+ " ["+getEmail()+"] "+notification);
    }
}
