package enriquecimento.model;

import java.net.UnknownHostException;

public class Rate extends Model {

	public Rate() throws UnknownHostException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Rate put(String key, Object value){
		super.put(key, value);
		return this;
	}
	
	public Object get(String key){
		return super.get(key);
	}

}
