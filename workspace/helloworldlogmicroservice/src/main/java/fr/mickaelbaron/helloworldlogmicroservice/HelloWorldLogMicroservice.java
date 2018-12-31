package fr.mickaelbaron.helloworldlogmicroservice;

import java.io.IOException;
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

	public HelloWorldLogMicroservice(String rabbitMQHosts) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitMQHosts);

//		final Connection connection = createConnection(factory);
		Connection connection = factory.newConnection();
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
		while (!connectionIsReady) {
			try {
				connection = factory.newConnection();
				connectionIsReady = true;
			} catch (Exception e) {
				System.out.println("Problem:" + e.getMessage());
				System.out.println("We will try to connect to RabbitMQ in 5s.");
				Thread.sleep(5000);
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
