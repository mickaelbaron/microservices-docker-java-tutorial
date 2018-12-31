package fr.mickaelbaron.helloworldrestmicroservice.dao;

import java.util.List;

import fr.mickaelbaron.helloworldrestmicroservice.model.HelloWorld;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
public interface IHelloWorldDAO {

	/**
	 * @return
	 */
	List<HelloWorld> getHelloWorlds();

	/**
	 * @param newHelloWorld
	 */
	void addHelloWorld(HelloWorld newHelloWorld);
}
