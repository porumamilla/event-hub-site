package com.eventhub.site.model;

import java.io.Serializable;
public class SourceType implements Serializable {
    private static final long serialVersionUID = 1L;
	private String id;
	private String name;

	private String type;
	private String key;
    private String workspace;
	
	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
    public boolean equals(Object o) {
		SourceType passedInObj = (SourceType) o;
		if (this.id.equals(passedInObj.getId())) {
			return true;
		}
		return false;
	}
}
