package com.nosqlcourse.dao.impl;

import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IUserDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLUserDAOImpl implements IUserDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQLUserDAOImpl.class);

    private static final DataSource dataSource = DataSourceConfiguration.getDataSource();

    private static final String GET_USERS = "SELECT * FROM user JOIN user_role ON role_id=user_role.id";
    private static final String GET_USERS_BY_NAME = "SELECT * FROM user JOIN user_role ON role_id=user_role.id WHERE user.name=?";
    private static final String GET_USER_BY_EMAIL = "SELECT * FROM user JOIN user_role ON user.role_id=user_role.id WHERE email = ?";
    private static final String GET_USER_BY_ID = "SELECT * FROM user JOIN user_role ON user.role_id=user_role.id WHERE user.id = ?";
    private static final String INSERT_USER = "INSERT INTO user(role_id,email,password,name) VALUES (?,?,?,?)";
    private static final String UPDATE_USER = "UPDATE user SET email = ?, password = ?, name = ?, role_id = ? WHERE id = ?;";
    private static final String GET_ROLE_ID_BY_ROLE_NAME = "SELECT id FROM user_role WHERE name=?";


    private User extractUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(1));
        user.setEmail(resultSet.getString(2));
        user.setPassword(resultSet.getString(3));
        user.setName(resultSet.getString(4));
        Role role = new Role();
        role.setId(resultSet.getLong(6));
        role.setName(resultSet.getString(7));
        user.setRole(role);
        return user;
    }

    public List<User> getUsers() throws DataNotFoundException {
        List<User> users = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(GET_USERS)){
                while (resultSet.next()) {
                    users.add(extractUser(resultSet));
                }
            }
        } catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return users;
    }

    @Override
    public List<User> getUsersByName(String name) throws DataNotFoundException {
        List<User> users = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USERS_BY_NAME)) {
            preparedStatement.setString(1,name);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    users.add(extractUser(resultSet));
                }
            }
        } catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return users;
    }

    private Long getRoleIdByRoleName(String name, Connection connection) throws SQLException, DataNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_ROLE_ID_BY_ROLE_NAME);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) return resultSet.getLong(1);
        throw new DataNotFoundException("Role with name " + name + "doesn't exists");
    }

    @Override
    public User getUserByEmail(String email) throws DataNotFoundException {
        User user = new User();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = extractUser(resultSet);
                } else {
                    throw new DataNotFoundException();
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws DataNotFoundException {
        User user = new User();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID)) {
            preparedStatement.setLong(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = extractUser(resultSet);
                } else {
                    throw new DataNotFoundException();
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return user;
    }

    @Override
    public Long insertUser(User user) {
        try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            Long roleId = getRoleIdByRoleName(user.getRole().getName(), connection);
            preparedStatement.setLong(1, roleId);
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getName());
            preparedStatement.executeUpdate();
            ResultSet key = preparedStatement.getGeneratedKeys();
            if(key.next()) return key.getLong(1);
            else throw new SQLException("Cannot create new user");
        }
        catch (SQLException | DataNotFoundException ex){
            log.error(ex.getMessage());
        }
        return (long) -1;
    }

    @Override
    public boolean updateUser(User user){
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)){
            preparedStatement.setString(1,user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setLong(4, getRoleIdByRoleName(user.getRole().getName(),connection));
            preparedStatement.setLong(5, user.getId());
            affectedRows = preparedStatement.executeUpdate();
        } catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return affectedRows == 1;
    }
}