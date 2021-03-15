
package com.wang.JerseyTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.test.model.TestBean;

// The Java class will be hosted at the URI path "/myresource"
@Path("/test")
public class MyResource {

	// TODO: update the class to suit your needs

	// The Java method will process HTTP GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Path("get")
	@Produces("text/plain")
	public String getIt() {
		return "Got it!";
	}

	@GET
	@Path("get/{param}")
	public Response getMsg(@PathParam("param") String msg) {
		String output = "Jersy say : " + msg;
		return Response.status(200).entity(output).build();
	}

	// TODO : archive post request
	@Path("post")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPost(TestBean input) {
		System.out.println("get input info");
		return Response.ok(input, MediaType.APPLICATION_JSON).build();
	}
}
