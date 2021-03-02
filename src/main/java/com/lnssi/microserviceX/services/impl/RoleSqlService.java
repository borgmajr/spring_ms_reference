package com.lnssi.microserviceX.services.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.lnssi.microserviceX.model.security.Role;
import com.lnssi.microserviceX.services.RoleService;

@Component
public class RoleSqlService implements RoleService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Optional<Role> findByName(String roleName) {
		Role role;
		try {
			role = jdbcTemplate.queryForObject("SELECT * FROM roles where name = ?",
					new BeanPropertyRowMapper<Role>(Role.class), roleName);

			return Optional.of(role);
		} catch (DataAccessException e) {
			return Optional.empty();
		}

	}

	@Override
	public Set<Role> getRolesForUser(Long userId) {
		List<Role> roles = null;
		try {
			roles = jdbcTemplate.query(
					"select r.id, r.name from user_roles ur join users u on u.id = ur.userid join roles r on r.id = ur.roleid where u.id = ?",
					new BeanPropertyRowMapper<Role>(Role.class), userId);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new HashSet<Role>(roles);
	}

	@Override
	public Role save(Role role) {

		if (null != role && null != role.getId()) {
			jdbcTemplate.update("UPDATE roles set name = ? where id = ?",
					new Object[] { role.getName(), role.getId() });
		} else {

			KeyHolder keyHolder = new GeneratedKeyHolder();

			PreparedStatementCreator psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement("INSERT INTO roles(name) values (?)",
							new String[] { "id" });

					int i = 1;
					for (Object o : new Object[] { role.getName() }) {
						ps.setObject(i, o);
						i++;
					}

					return ps;
				}

			};

			jdbcTemplate.update(psc, keyHolder);

			role.setId(keyHolder.getKey().intValue());

		}

		return role;
	}

	@Override
	public void addRoleToUser(Long userId, Role role) {
		jdbcTemplate.update("INSERT INTO user_roles(userid, roleid) values (?,?)",
				new Object[] { userId, role.getId() });

	}

	@Override
	public void removeRoleFromUser(Long userId, Role role) {
		jdbcTemplate.update("delete from user_roles where userid = ? and roleid = ?",
				new Object[] { userId, role.getId() });

	}

	@Override
	public void removeAllRolesFromUser(Long userId) {
		jdbcTemplate.update("delete from user_roles where userid = ? ", new Object[] { userId });
	}

}
