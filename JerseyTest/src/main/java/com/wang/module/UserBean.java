package com.wang.module;

import javax.ws.rs.DefaultValue;

import org.glassfish.jersey.media.multipart.FormDataParam;

public class UserBean {

	@FormDataParam("name")
	private String name;

	@DefaultValue("7")
	@FormDataParam("age")
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "UserBean [name=" + name + ", age=" + age + "]";
	}

}
