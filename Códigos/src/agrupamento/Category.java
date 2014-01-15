package agrupamento;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Category {
	public String name;
	public List<JSONObject> movies;
	public List<JSONObject> users;
	public List<JSONObject> rates;
	
	public Category(String name, List<JSONObject> listMovies) {
		this.name = name;
		this.movies = listMovies;
		this.users = new ArrayList<JSONObject>();
		this.rates = new ArrayList<JSONObject>();
	}
}
