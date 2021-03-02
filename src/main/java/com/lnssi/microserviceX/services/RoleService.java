package com.lnssi.microserviceX.services;

import java.util.Optional;
import java.util.Set;

import com.lnssi.microserviceX.model.security.Role;

public interface RoleService {

	public Optional<Role> findByName(String roleName);

	public Set<Role> getRolesForUser(Long userId);

	public Role save(Role role);
	
	public void addRoleToUser(Long userId, Role role);
	
	public void removeRoleFromUser(Long userId, Role role);
	
	public void removeAllRolesFromUser(Long userId);

}
