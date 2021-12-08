package com.wang.rest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ScheduledFuture;

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
import javax.ws.rs.core.Response.ResponseBuilder;

import com.wang.model.XwwwFormBean;
import com.wang.schdule.ScheduleUtil;
import com.wang.schdule.ScheduledExecutorServiceManager;
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "get it!";
	}

	@GET
	@Path("get/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMsg(@PathParam("param") String msg) {
		String output = "";
		ResponseBuilder builder;
		if (msg != null && !msg.equals("error")) {
			output = "{\"msg\":\"" + msg + "\"}";
			builder = Response.status(200).entity(output);
		} else {
			builder = Response.status(400);
			builder.header("Error-Code", 51000);
			try {
				builder.header("Error-Message", URLEncoder.encode("入力参数不正确.", "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

//		builder.header("Access-Control-Allow-Origin", "*"); // 允许访问所有域，可以换成具体url，注意仅具体url才能带cookie信息
//		builder.header("Access-Control-Allow-Headers", "Content-Type,AccessToken,X-CSRF-Token, Authorization, Token"); // header的类型
//		builder.header("Access-Control-Allow-Credentials", "true"); // 设置为true，允许ajax异步请求带cookie信息
//		builder.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE"); // 允许请求方法
//		builder.header("content-type", "application/json;charset=UTF-8");
		return builder.build();
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

	@GET
	@Path("schedule/{param}")
	@Produces(MediaType.TEXT_HTML)
	public Response getSchedule(@PathParam("param") String time) {
		ScheduledExecutorServiceManager executor = ScheduledExecutorServiceManager.getExecutorInstance();
		ScheduledFuture<?> future = executor.getReloadFuture();
		future = ScheduleUtil.executeScheduleAtTimePerDayBase(new Runnable() {
			@Override
			public void run() {
				System.out.println("Running!!!!");
			}
		}, time, future);
		return Response.status(200).entity("set").build();
	}
}
