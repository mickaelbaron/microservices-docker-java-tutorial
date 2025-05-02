package fr.mickaelbaron.helloworldrestmicroservice.dao.redis;

import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
@ApplicationScoped
public class JedisFactory {

	private static final String REDIS_HOST_ENV = "REDIS_HOST";

	private JedisPool jedisPool;

	@Inject
	@ConfigProperty(name = REDIS_HOST_ENV)
	private String redisHost;

	// public JedisFactory() {
	// URI redisURI = getRedisURI();
	// jedisPool = new JedisPool(new JedisPoolConfig(), redisURI);
	// }

	@PostConstruct
	public void init() {
		URI redisURI = getRedisURI();
		jedisPool = new JedisPool(new JedisPoolConfig(), redisURI);
	}

	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	private URI getRedisURI() {
		// String redisHost = System.getenv(REDIS_HOST_ENV);
		return URI.create(redisHost != null && !redisHost.isEmpty() ? redisHost : "tcp://localhost:6379");
	}
}
