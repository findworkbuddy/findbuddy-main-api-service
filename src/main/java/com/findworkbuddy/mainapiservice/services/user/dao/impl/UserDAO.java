package com.findworkbuddy.mainapiservice.services.user.dao.impl;

import com.findworkbuddy.mainapiservice.exceptions.IncorrectLoginException;
import com.findworkbuddy.mainapiservice.model.LoginUserRequest;
import com.findworkbuddy.mainapiservice.model.User;
import com.findworkbuddy.mainapiservice.services.user.dao.api.IUserDAO;
import com.findworkbuddy.mainapiservice.services.user.dao.util.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import static com.findworkbuddy.mainapiservice.services.user.dao.util.UserSqlUtils.CREATE_USER;
import static com.findworkbuddy.mainapiservice.services.user.dao.util.UserSqlUtils.GET_USERS_COUNT_BY_EMAIL;
import static com.findworkbuddy.mainapiservice.services.user.dao.util.UserSqlUtils.GET_USER_BY_EMAIL;
import static com.findworkbuddy.mainapiservice.services.user.dao.util.UserSqlUtils.GET_USER_PASSWORD;

@Service
public class UserDAO implements IUserDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserDAO(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void createUser(User user) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("firstName", user.getFirstName())
            .addValue("lastName", user.getLastName())
            .addValue("email", user.getEmail())
            .addValue("password", user.getPassword())
            .addValue("headline", user.getHeadLine())
            .addValue("summary", user.getSummary());

        namedParameterJdbcTemplate.update(CREATE_USER, sqlParameterSource);
    }

    @Override
    public boolean isEmailAvailable(String email) {

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("email", email);

        Integer usersWithEmail = namedParameterJdbcTemplate
            .queryForObject(GET_USERS_COUNT_BY_EMAIL, sqlParameterSource, Integer.class);

        return usersWithEmail < 1;
    }

    @Override
    public String getUserPassword(LoginUserRequest loginUserRequest) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("email", loginUserRequest.getEmail());
        String userPassword;
        try {
            userPassword = namedParameterJdbcTemplate
                .queryForObject(GET_USER_PASSWORD, sqlParameterSource, String.class);
        } catch (Exception e) {
            throw new IncorrectLoginException("Incorrect email address");
        }

        return userPassword;
    }

    @Override
    public User getUserByEmail(String email) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("email", email);

        return namedParameterJdbcTemplate.queryForObject(GET_USER_BY_EMAIL,
            sqlParameterSource, new UserRowMapper());

    }

}
