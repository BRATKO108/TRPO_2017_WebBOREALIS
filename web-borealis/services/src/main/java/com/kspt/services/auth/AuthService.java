package com.kspt.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@Primary
public class AuthService {

    public static final String INSERT_USER = "insert into users (username, password) values (?, ?)";
    public static final String DELETE_USER = "delete from users where id = ?";
    public static final String UPDATE_USER = "update users set (username, password) = (?, ?) where id = ?";
    public static final String SELECT_USER = "select * from users where id = ?";
    public static final String SELECT_USER_BY_USERNAME = "select * from users where username = ?";

    public static final String INSERT_ROLE = "insert into roles (user_id, role) values (?, ?)";
    public static final String DELETE_ALL_ROLES = "delete from roles where username = ?";
    public static final String DELETE_ROLE = "delete from roles where username = ? and role = ?";
    public static final String UPDATE_ROLE = "update users set (username, role) = (?, ?) where username = ? and role = ?";
    public static final String SELECT_ROLE = "select * from roles where user_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int insertUser(User user){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            return preparedStatement;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return rows;
    }

    public int deleteUser(long id){
        return jdbcTemplate.update(DELETE_USER, preparedStatement -> preparedStatement.setLong(1, id));
    }

    public int updateUser(long id, User user){
        return jdbcTemplate.update(UPDATE_USER, user.getUsername(), user.getPassword(), id);
    }

    public User selectUser(long id){
        return jdbcTemplate.queryForObject(SELECT_USER, new Object[]{id}, (resultSet, i) -> {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            return user;
        });
    }

    public User selectUserByUsername(String username){
        return jdbcTemplate.queryForObject(SELECT_USER_BY_USERNAME, new Object[]{username}, (resultSet, i) -> {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            return user;
        });
    }

    public int insertRole(long id, String role){
        int rows = jdbcTemplate.update(INSERT_ROLE, id, role);
        User user = selectUser(id);
        user.addAuthority(role);
        return rows;
    }

    public List<String> selectAuthorities(long id){
        return jdbcTemplate.query(SELECT_ROLE, new Object[]{id}, (resultSet, i) ->
                resultSet.getString("role"));
    }

}
