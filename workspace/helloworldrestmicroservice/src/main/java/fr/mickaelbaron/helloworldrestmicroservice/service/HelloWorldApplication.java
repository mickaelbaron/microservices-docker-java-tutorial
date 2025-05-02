package fr.mickaelbaron.helloworldrestmicroservice.service;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
@ApplicationPath("/")
public class HelloWorldApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(HelloWorldResource.class);
		s.add(CrossDomainFilter.class);
		return s;
	}
}
