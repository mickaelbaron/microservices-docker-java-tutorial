package fr.mickaelbaron.helloworldlogmicroservice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author Mickael BARON
 */
public class HelloWorldLogMicroservice {

	public static final String EXCHANGE_NAME = "helloworld";

	private static final int maxAttempts = 5;

	public HelloWorldLogMicroservice(String rabbitMQHosts) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setUri(rabbitMQHosts);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			System.out.println("❌ Failed to configure RabbitMQ connection: invalid URI.");
			e.printStackTrace(); 
			System.exit(-1);
		}

		final Connection connection = createConnection(factory);
		//final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}

	private Connection createConnection(ConnectionFactory factory) throws InterruptedException {
		// We implement an healthcheck.
		boolean connectionIsReady = false;
		Connection connection = null;
		int attempt = 0;

		while (!connectionIsReady && attempt < maxAttempts) {
			try {
				connection = factory.newConnection();
				connectionIsReady = true;
			} catch (Exception e) {
				attempt++;
				System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
				if (attempt < maxAttempts) {
					System.out.println("Retrying to connect to RabbitMQ in 5s...");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt(); // Restore interrupt status
						break;
					}
				} else {
					System.out.println("Max connection attempts reached. Aborting.");
					System.exit(-1);
				}
			}
		}

		System.out.println("Great !! Connected to RabbitMQ.");

		return connection;
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

		if (args == null || args.length == 0 || args.length != 1) {
			System.err.println("Remote address of RabbitMQ required.");
			System.exit(-1);
		}

		new HelloWorldLogMicroservice(args[0]);
	}
}
