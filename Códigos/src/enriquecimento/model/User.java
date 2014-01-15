package enriquecimento.model;

import java.net.UnknownHostException;

public class User extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User() throws UnknownHostException {
		super();
	}
	
	public User put(String key, Object value){
		super.put(key, value);
		return this;
	}
	
	public Object get(String key){
		return super.get(key);
	}
}
