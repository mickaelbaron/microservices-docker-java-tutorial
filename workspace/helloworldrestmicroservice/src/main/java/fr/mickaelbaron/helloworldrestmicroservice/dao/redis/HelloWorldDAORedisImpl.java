package fr.mickaelbaron.helloworldrestmicroservice.dao.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import fr.mickaelbaron.helloworldrestmicroservice.dao.IHelloWorldDAO;
import fr.mickaelbaron.helloworldrestmicroservice.model.HelloWorld;
import redis.clients.jedis.Jedis;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 * 
 * Based on: https://github.com/fjunior87/JedisCrud/tree/master/src/com/xicojunior/jediscrud/dao
 * 
 * Improvements: used Redis pipeline.
 */
@Named("redis")
public class HelloWorldDAORedisImpl implements IHelloWorldDAO {

	@Inject
	JedisFactory refSession;

	public List<HelloWorld> getHelloWorlds() {
		Jedis jedis = refSession.getJedis();

		List<HelloWorld> helloWorlds = new ArrayList<HelloWorld>();

		// Get all HelloWorld ids from the redis list using LRANGE.
		List<String> allUserIds = jedis.lrange(Keys.HELLOWORLD_ALL.key(), 0, -1);
		if (allUserIds != null && !allUserIds.isEmpty()) {
			List<Map<String, String>> responseList = new ArrayList<Map<String, String>>();

			for (String userId : allUserIds) {
				// Call HGETALL for each HelloWorld id.
				responseList.add(jedis.hgetAll(Keys.HELLOWORLD_DATA.formated(userId)));
			}

			// Iterate over the results
			for (Map<String, String> properties : responseList) {
				helloWorlds.add(BeanUtil.populate(properties, new HelloWorld()));
			}
		}
		jedis.close();

		return helloWorlds;
	}

	public void addHelloWorld(HelloWorld newHelloWorld) {
		Jedis jedis = refSession.getJedis();

		long helloWorldId = jedis.incr(Keys.HELLOWORLD_IDS.key());
		newHelloWorld.setRid(helloWorldId);

		// Add to HelloWorld list.
		jedis.lpush(Keys.HELLOWORLD_ALL.key(), String.valueOf(helloWorldId));

		// Add to the hash structure.
		jedis.hmset(Keys.HELLOWORLD_DATA.formated(String.valueOf(helloWorldId)), BeanUtil.toMap(newHelloWorld));

		jedis.close();
	}
}
