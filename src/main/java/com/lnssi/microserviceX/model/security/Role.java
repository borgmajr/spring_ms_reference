package com.lnssi.microserviceX.model.security;

import java.io.Serializable;

public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String USER = "USER";
	public static final String ADMIN = "ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	private Integer id;
	private String name;

	public Role() {
	}

	public Role(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + "]";
	}

	

}
