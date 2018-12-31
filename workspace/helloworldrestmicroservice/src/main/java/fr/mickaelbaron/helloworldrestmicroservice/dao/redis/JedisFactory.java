package fr.mickaelbaron.helloworldrestmicroservice.dao.redis;

import java.net.URI;

import javax.inject.Singleton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 */
@Singleton
public class JedisFactory {

	private static final String REDIS_HOST_ENV = "REDIS_HOST";

	private JedisPool jedisPool;

	public JedisFactory() {
		URI redisURI = getRedisURI();
		System.out.println(redisURI);
		jedisPool = new JedisPool(new JedisPoolConfig(), redisURI);
	}

	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	private URI getRedisURI() {
		String redisHost = System.getenv(REDIS_HOST_ENV);
		System.out.println(redisHost);
		return URI.create(redisHost != null && !redisHost.isEmpty() ? redisHost : "tcp://localhost:6379");
	}
}
