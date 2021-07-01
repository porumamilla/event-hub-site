package com.eventhub.site.model;
import java.io.Serializable;
public class Workspace implements Serializable {
	private static final long serialVersionUID = 1L;
	private String orgId;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
}
