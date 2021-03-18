package com.wang.JerseyTest;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wang.module.UserBean;
import com.wang.module.XwwwFormBean;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("test")
public class MyResource {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Path("get")
	@Produces(MediaType.TEXT_HTML)
	public String getIt() {
		return "Got it!";
	}

	@GET
	@Path("get/{param}")
	@Produces(MediaType.TEXT_HTML)
	public Response getMsg(@PathParam("param") String msg) {
		String output = "Jersy say : " + msg;
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("get/query")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuery(@QueryParam("name") String name, @DefaultValue("8") @QueryParam("age") int age) {
		UserBean result = new UserBean();
		result.setAge(age);
		result.setName(name);
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("post/bean")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPost(UserBean input) {
//		return Response.status(200).entity(input).build();
//		return input;
		return Response.ok(input, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("post/form")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPostForm(@BeanParam UserBean input) {
//		return Response.status(200).entity(input).build();
//		return input;
		System.out.println(input);
		return Response.ok(input, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("post/xform")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testxPostForm(@BeanParam XwwwFormBean input) {
		System.out.println(input);
		return Response.status(200).entity(input).build();
	}
}
