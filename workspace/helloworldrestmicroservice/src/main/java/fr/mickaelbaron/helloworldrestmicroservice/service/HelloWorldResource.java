package fr.mickaelbaron.helloworldrestmicroservice.service;

import java.util.Date;

import fr.mickaelbaron.helloworldrestmicroservice.dao.IHelloWorldDAO;
import fr.mickaelbaron.helloworldrestmicroservice.model.HelloWorld;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
@Path("/helloworld")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

	@Inject
	private IHelloWorldDAO currentDAO;

	@GET
	public Response getHelloWorlds() {
		return Response.ok(currentDAO.getHelloWorlds()).build();
	}

	@POST
	public Response addHelloWorld(HelloWorld newHelloWorld) {
		if (newHelloWorld != null) {
			newHelloWorld.setStartDate(new Date().toString());
		}

		currentDAO.addHelloWorld(newHelloWorld);

		return Response.status(Status.CREATED).build();
	}
}
