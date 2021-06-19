package com.wang.model;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class XwwwFormBean {
	@FormParam("name")
	private String name;

	@DefaultValue("7")
	@FormParam("age")
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
		return "XwwwFormBean [name=" + name + ", age=" + age + "]";
	}

}
