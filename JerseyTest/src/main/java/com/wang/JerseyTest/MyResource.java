package com.wang.JerseyTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

import com.wang.model.XwwwFormBean;
import com.wang.common.StringUtils;
import com.wang.model.FileInputBean;
import com.wang.model.UserBean;

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
	public Response testPostForm(@BeanParam com.wang.model.UserBean input) {
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

	@POST
	@Path("post/form/file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPostFormFile(@BeanParam FileInputBean input) {
		System.out.println(input);
		InputStream is = input.getFileBody().getValueAs(InputStream.class);
		byte[] data = new byte[512];
		int len;
		try {
			FileOutputStream fos = new FileOutputStream("E:/TestFile/0619.txt");
			while ((len = is.read(data)) != -1) {
				fos.write(data, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(200).entity(StringUtils.beanToJsonString(input)).build();
	}
}
