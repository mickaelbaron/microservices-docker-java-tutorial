package fr.mickaelbaron.helloworldrestmicroservice.dao.redis;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 * 
 * Based on: https://github.com/fjunior87/JedisCrud/blob/master/src/com/xicojunior/jediscrud/model/Keys.java
 */
public enum Keys {

	HELLOWORLD_ALL("helloworld:all"), 
	HELLOWORLD_DATA("helloworld:%s:data"), 
	HELLOWORLD_IDS("helloworld:ids");

	private String key;

	Keys(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}

	public String formated(String... value) {
		return String.format(key, (Object[]) value);
	}
}