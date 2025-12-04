package fr.mickaelbaron.helloworldrestmicroservice.event.rabbitmq;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
@ApplicationScoped
public class RabbitMQFactory {

	private static final String RABBITMQ_HOST_ENV = "RABBITMQ_HOST";

	public static final String EXCHANGE_NAME = "helloworld";

	private Channel currentChanel;

	@PostConstruct
	public void init() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri(getRedisURI());
			Connection connection = factory.newConnection();
			currentChanel = connection.createChannel();
			currentChanel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException | IOException
				| TimeoutException e) {
			throw new IllegalStateException("❌ Failed to initialize RabbitMQ connection during application startup.",
					e);
		}
	}

	public Channel getChannel() {
		return currentChanel;
	}

	private URI getRedisURI() {
		String rabbitmqHost = System.getenv(RABBITMQ_HOST_ENV);
		return URI.create(rabbitmqHost != null && !rabbitmqHost.isEmpty() ? rabbitmqHost : "amqp://localhost:5672");
	}

}
