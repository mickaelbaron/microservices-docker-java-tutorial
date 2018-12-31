package fr.mickaelbaron.helloworldrestmicroservice.service;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
public class CrossDomainFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext creq, ContainerResponseContext cres) throws IOException {
		final MultivaluedMap<String, Object> headers = cres.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		headers.add("Access-Control-Max-Age", "1209600");
	}
}