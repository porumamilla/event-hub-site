package com.eventhub.site.form;

import com.eventhub.site.model.Organization;
import com.eventhub.site.model.User;

public class RegistrationForm {

	private String email;
	private String name;
	private String orgName;
	private String address;
	private String address2;
	private String city;
	private String country;
	private String state;
	private String postalCode;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public Organization getOrg() {
		Organization org = new Organization();
		org.setName(getOrgName());
		org.setAddress(getAddress());
		org.setAddress2(getAddress2());
		org.setCity(getCity());
		org.setCountry(getCountry());
		org.setState(getState());
		org.setPostalCode(getPostalCode());
		return org;
	}
	
	public User getUser(String orgId) {
		User user = new User();
		user.setDefaultWorkspace("");
		user.setEmail(getEmail());
		user.setName(getName());
		user.setRole("Admin");
		user.setOrgId(orgId);
		return user;
	}
	
}
