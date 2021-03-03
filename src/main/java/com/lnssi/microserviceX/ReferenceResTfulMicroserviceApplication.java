package com.lnssi.microserviceX;

import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.lnssi.microserviceX.model.security.Role;
import com.lnssi.microserviceX.model.security.User;
import com.lnssi.microserviceX.services.RoleService;
import com.lnssi.microserviceX.services.UserService;

@SpringBootApplication
public class ReferenceResTfulMicroserviceApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ReferenceResTfulMicroserviceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReferenceResTfulMicroserviceApplication.class, args);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("########################################################################");
		System.out.println("Initting things needed...");

		try {
			{
				Resource resource = new ClassPathResource("sql/001_init.sql");
				ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
				databasePopulator.execute(getAdminDataSource());
			}

			{
				Resource resource = new ClassPathResource("sql/002_init_tables.sql");
				ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
				databasePopulator.execute(jdbcTemplate.getDataSource());
			}
		} catch (Exception e) {
			System.err.println(">>>>>>>>>>>>> INIT SCRIPTS SKIPPED <<<<<<<<<<<<<<<<<<<<");
			e.printStackTrace();
		}

		// insert admin:admin
		try {
			// userService.save(new User("admin",
			// "$2a$04$KNLUwOWHVQZVpXyMBNc7JOzbLiBjb9Tk9bP7KNcPI12ICuvzXQQKG","admin@test.com"));

			// Create user roles
			var adminRole = createRoleIfNotFound(Role.ROLE_ADMIN);
			var userRole = createRoleIfNotFound(Role.ROLE_USER);

			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			// Create users
			createUserIfNotFound("admin", passwordEncoder.encode("admin"), adminRole, "admin@test.com");
			createUserIfNotFound("user", passwordEncoder.encode("user"), userRole, "user@test.com");

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("StartApplication...");
		System.out.println("########################################################################");

	}

	@Transactional
	private final Role createRoleIfNotFound(final String name) {
		Optional<Role> role = roleService.findByName(name);
		if (role.isEmpty()) {
			Role newRole = new Role(name);
			newRole = roleService.save(newRole);

			return newRole;
		} else {
			return role.get();
		}

	}

	@Transactional
	private final User createUserIfNotFound(final String name, final String password, final Role role,
			final String email) {
		Optional<User> user = userService.findByUserName(name);
		if (user.isEmpty()) {
			User newUser = new User(name, password, email);
			newUser = userService.save(newUser);

			roleService.removeAllRolesFromUser(newUser.getId());
			roleService.addRoleToUser(newUser.getId(), role);
			newUser.setRoles(Set.of(role));

			return newUser;
		} else {
			return user.get();
		}
	}

	public DataSource getAdminDataSource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("org.postgresql.Driver");
		dataSourceBuilder.url("jdbc:postgresql://127.0.0.1:5432/");
		dataSourceBuilder.username("postgres");
		dataSourceBuilder.password("");
		return dataSourceBuilder.build();
	}

}
