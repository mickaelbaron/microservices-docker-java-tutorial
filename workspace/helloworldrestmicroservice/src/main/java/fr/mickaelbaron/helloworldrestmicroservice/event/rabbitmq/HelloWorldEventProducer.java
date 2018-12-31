package fr.mickaelbaron.helloworldrestmicroservice.event.rabbitmq;

import java.io.IOException;

import javax.inject.Inject;

import com.google.gson.Gson;

import fr.mickaelbaron.helloworldrestmicroservice.event.IHelloWorldEventProducer;
import fr.mickaelbaron.helloworldrestmicroservice.model.HelloWorld;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
public class HelloWorldEventProducer implements IHelloWorldEventProducer {

	@Inject
	RabbitMQFactory factory;

	@Override
	public void sendMessage(HelloWorld newHelloWorld) {
		Gson gson = new Gson();

		System.out.println(gson.toJson(newHelloWorld));
		if (newHelloWorld != null) {
			try {
				factory.getChannel().basicPublish(RabbitMQFactory.EXCHANGE_NAME, "", null,
						gson.toJson(newHelloWorld).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
