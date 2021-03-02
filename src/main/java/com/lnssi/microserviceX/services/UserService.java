package com.lnssi.microserviceX.services;

import java.util.List;
import java.util.Optional;

import com.lnssi.microserviceX.model.security.User;

public interface UserService {

	public Optional<User> findById(Long id);

	public Optional<User> findByUserName(String username);

	public List<User> findAll();
	
	public void deleteById(Long id);
	
	public User save(User user);

}
