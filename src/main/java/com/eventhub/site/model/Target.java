package com.eventhub.site.model;
import java.io.Serializable;
public class Target implements Serializable {
    private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String config;
	private String parentId;
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}	
	
}
