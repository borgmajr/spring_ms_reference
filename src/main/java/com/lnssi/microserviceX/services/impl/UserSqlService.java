package com.lnssi.microserviceX.services.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.lnssi.microserviceX.model.security.Role;
import com.lnssi.microserviceX.model.security.User;
import com.lnssi.microserviceX.services.UserService;

@Component
public class UserSqlService implements UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Optional<User> findById(Long id) {

		return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM users where id = ? and deleted = false",
				new BeanPropertyRowMapper<User>(User.class), id));

	}

	@Override
	public Optional<User> findByUserName(String username) {

		User user;
		try {
			user = jdbcTemplate.queryForObject("SELECT * FROM users where username = ? and deleted = false",
					new BeanPropertyRowMapper<User>(User.class), username);

			return Optional.of(user);
		} catch (DataAccessException e) {
			return Optional.empty();
		}

	}

	@Override
	public User save(User user) {
		if (null != user && null != user.getId()) {

			jdbcTemplate.update("UPDATE users set email = ? where id = ?",
					new Object[] { user.getEmail(), user.getId() });

		} else {

			KeyHolder keyHolder = new GeneratedKeyHolder();

			PreparedStatementCreator psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(
							"INSERT INTO users(username,password,email) values (?,?,?)", new String[] { "id" });

					int i = 1;
					for (Object o : new Object[] { user.getUsername(), user.getPassword(), user.getEmail() }) {
						ps.setObject(i, o);
						i++;
					}

					return ps;
				}

			};

			jdbcTemplate.update(psc, keyHolder);

			user.setId(keyHolder.getKey().longValue());

		}

		return user;
	}

	@Override
	public List<User> findAll() {
		List<User> users = jdbcTemplate.query("select * from users", new BeanPropertyRowMapper<User>(User.class));
		return users;
	}

	@Override
	public void deleteById(Long id) {
		jdbcTemplate.update("delete from users where id = ?", new Object[] { id });

	}

}
