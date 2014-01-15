package enriquecimento.model;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class Movie extends Model {
	private static final long serialVersionUID = 1L;
	
	public Movie() throws UnknownHostException {
		super();
	}
	
	public Movie put(String key, Object value){
		super.put(key, value);
		return this;
	}
	
	public Object get(String key){
		return super.get(key);
	}
}
