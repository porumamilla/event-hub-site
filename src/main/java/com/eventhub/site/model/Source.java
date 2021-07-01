package com.eventhub.site.model;
import java.io.Serializable;
public class Source implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
	private String name;
	private String protocol;


	private String orgId;
	
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
