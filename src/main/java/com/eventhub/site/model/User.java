package com.eventhub.site.model;
import java.io.Serializable;

public class User implements Serializable {
	private String id;
	private String orgId;
	private String email;
	private String name;
	private String role;
	private String defaultWorkspace;
	
	
	public String getDefaultWorkspace() {
		return defaultWorkspace;
	}

	public void setDefaultWorkspace(String defaultWorkspace) {
		this.defaultWorkspace = defaultWorkspace;
	}
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
