package com.eventhub.site.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties("evenhub.rest.client")
public class ApiEndPointUri {

	private String daoApiEndpoint;
	private String schemaApiEndpoint;
	private String publisherApiEndpoint;
	
	public String getDaoApiEndpoint() {
		return daoApiEndpoint;
	}
	public void setDaoApiEndpoint(String daoApiEndpoint) {
		this.daoApiEndpoint = daoApiEndpoint;
	}
	public String getSchemaApiEndpoint() {
		return schemaApiEndpoint;
	}
	public void setSchemaApiEndpoint(String schemaApiEndpoint) {
		this.schemaApiEndpoint = schemaApiEndpoint;
	}
	public String getPublisherApiEndpoint() {
		return publisherApiEndpoint;
	}
	public void setPublisherApiEndpoint(String publisherApiEndpoint) {
		this.publisherApiEndpoint = publisherApiEndpoint;
	}
	
	@Profile("local")
	@Bean
	public String localRestClientProperties() {
		
		System.out.println("daoApiEndpoint == " + daoApiEndpoint);
		System.out.println("schemaApiEndpoint == " + schemaApiEndpoint);
		System.out.println("publisherApiEndpoint == " + publisherApiEndpoint);
		
		return "Rest Client properties - Local"; 
	}
	
	@Profile("gcp")
	@Bean
	public String gcpRestClientProperties() {
		
		System.out.println("daoApiEndpoint == " + daoApiEndpoint);
		System.out.println("schemaApiEndpoint == " + schemaApiEndpoint);
		System.out.println("publisherApiEndpoint == " + publisherApiEndpoint);
		
		return "Rest Client properties - GCP"; 
	}
}
