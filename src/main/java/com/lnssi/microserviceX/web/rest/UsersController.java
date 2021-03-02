package com.lnssi.microserviceX.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lnssi.microserviceX.model.security.Role;
import com.lnssi.microserviceX.model.security.User;
import com.lnssi.microserviceX.services.RoleService;
import com.lnssi.microserviceX.services.UserService;
import com.lnssi.microserviceX.util.exceptions.ResourceNotFoundException;

@RestController
public class UsersController {

	private static final Logger log = LoggerFactory.getLogger(UsersController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@GetMapping("/users")
	public List<User> getUserList() {
		return userService.findAll();
	}

	@GetMapping("/users/{userId}")
	public User getUser(@PathVariable(value = "userId") Long userId) {
		User user =  userService.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("userId " + userId + " not found"));
		
		user.setRoles(roleService.getRolesForUser(user.getId()));
		
		return user;
	}

	@PostMapping("/users")
	public String createUser(@RequestBody User user) {
		user = userService.save(user);

		if (null != user.getRoles()) {
			roleService.removeAllRolesFromUser(user.getId());
			for (Role r : user.getRoles()) {
				r = roleService.findByName(r.getName()).get();
				roleService.addRoleToUser(user.getId(), r);
			}
		}

		return "User added";
	}

	@PutMapping("/users/{userId}")
	public String updateUser(@PathVariable(value = "userId") Long userId, @RequestBody User user) {
		return userService.findById(userId).map(mappedUser -> {
			mappedUser.setEmail(user.getEmail());
			mappedUser.setRoles(user.getRoles());
			userService.save(mappedUser);

			if (null != mappedUser.getRoles()) {
				roleService.removeAllRolesFromUser(mappedUser.getId());
				for (Role r : mappedUser.getRoles()) {
					r = roleService.findByName(r.getName()).get();
					roleService.addRoleToUser(mappedUser.getId(), r);
				}
			}

			return "User updated";
		}).orElseThrow(() -> new ResourceNotFoundException("userId " + userId + " not found"));
	}

	@DeleteMapping("/users/{userId}")
	public String deleteUser(@PathVariable(value = "userId") Long userId) {
		return userService.findById(userId).map(p -> {
			userService.deleteById(userId);
			return "User deleted";
		}).orElseThrow(() -> new ResourceNotFoundException("userId " + userId + " not found"));
	}
}