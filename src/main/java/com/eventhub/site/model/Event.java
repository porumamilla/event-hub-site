package com.eventhub.site.model;

import java.util.Map;

public class Event {
	private String name;
	private String sourceKey;
	private String sourceName;
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	private String timestamp;
	private String userId;
	private Map<String, Object> properties;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSourceKey() {
		return sourceKey;
	}
	
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
