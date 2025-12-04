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
    @ConfigProperty(name = REDIS_HOST_ENV, defaultValue = "tcp://localhost:6379")
    private String redisHost;

	@PostConstruct
	public void init() {
		URI redisURI = getRedisURI();
		jedisPool = new JedisPool(new JedisPoolConfig(), redisURI);
	}

	public Jedis getJedis() {
		return jedisPool.getResource();
	}

    private URI getRedisURI() {
        return URI.create(redisHost);
    }
}
