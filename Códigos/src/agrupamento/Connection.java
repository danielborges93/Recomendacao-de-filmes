package agrupamento;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Connection {
	public static void main(String[] args) throws UnknownHostException, JSONException {
		//connecting to database
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB db = mongoClient.getDB("bigdata");		
		
		//get movies collection and iterate it
		DBCollection movies = db.getCollection("movies");
		DBCursor cursor = movies.find();
		long tempoInicial = System.currentTimeMillis();
		
		//list of categories w/ users, movies and rates
		List<Category> categories = new ArrayList<Category>();
		try {
			System.out.println("Starting iterating each movie...");
			while(cursor.hasNext()) {
				JSONObject obj = new JSONObject(cursor.next().toString());
				JSONArray catArray = (JSONArray) obj.get("categories");
				
				//iterate each category of each movie
				for (int i = 0; i < catArray.length(); i++) {
					String catName = catArray.get(i).toString();
					if(!Connection.hasCategory(categories, catName)){
						List<JSONObject> m = new ArrayList<JSONObject>();
						m.add(obj);
						Category cat = new Category(catName, m);
						categories.add(cat);
					}else {
						Category cat = Connection.getCategoryByName(categories, catName);
						cat.movies.add(obj);
					}
				}
			}
			} finally {
				cursor.close();
			}
			System.out.println("End of movie iteration.");
			
			//get rates collection and iterate it
			DBCollection rates = db.getCollection("rates");
			DBCursor cursorRates = rates.find();
			DBCursor cursorMovie;
			
			//get users collection
			DBCollection users = db.getCollection("users");
			
			int j = 0;
			try{				
				System.out.println("Starting iterating each rate...");
				while(cursorRates.hasNext()) {
					j++;
					System.out.println("rate: " + j);
					JSONObject rate = new JSONObject(cursorRates.next().toString());
					
					//get user of each rate
					Integer userId = (Integer) rate.get("userID");
					BasicDBObject query1 = new BasicDBObject("_id", userId);
					cursor = users.find(query1);
					JSONObject user = null;
					try {
						while (cursor.hasNext()) {
							user = new JSONObject(cursor.next().toString());
						}
					}finally {
						cursor.close();
					}
					
					//get movie of each rate
					Integer movieId = (Integer) rate.get("movieID");
					BasicDBObject query = new BasicDBObject("_id", movieId);
					cursorMovie = movies.find(query);
					try {
						while(cursorMovie.hasNext()) {
							JSONObject obj = new JSONObject(cursorMovie.next().toString());
							JSONArray cats = (JSONArray) obj.get("categories");
							Connection.addUserAndRateToSetOfCategories(categories, cats, user, rate);
						}
					} finally {
						cursorMovie.close();
					}
				}
			} finally {
				cursorRates.close();
				System.out.println("End of rates iteration.");
			}
			
			//create "categories" collection in the database
			System.out.println("Creating the 'categories' collection in the database");
			DBCollection catCollection = db.getCollection("categories");
			
			//create category objects in the database
			for (Category catg : categories) {
				System.out.println("Creating document for category "+catg.name);
				BasicDBObject docCateg = new BasicDBObject("name", catg.name);
				
				//add each movie to the collection categories
				List<Integer> listOfMovies = new ArrayList<Integer>();
				//BasicDBObject docMovie = null;
				for (JSONObject m : catg.movies) {
					//docMovie = new BasicDBObject();
					Integer movieID = (Integer) m.get("_id");
//					docMovie.append("name", m.get("name"));
//					docMovie.append("releaseDate", m.get("releaseDate"));
//					docMovie.append("videoReleaseDate", m.get("videoReleaseDate"));
//					docMovie.append("imdbURL", m.get("imdbURL"));
//					JSONArray catArray = (JSONArray) m.get("categories");
//					List<String> array = new ArrayList<>();
//					for (int i = 0; i < catArray.length(); i++) {
//						array.add(catArray.get(i).toString());
//					}
//					docMovie.append("categories", array);
					listOfMovies.add(movieID);
				}
				docCateg.put("movies", listOfMovies);
				
				//add each user to the collection categories
				List<Integer> listOfUsers = new ArrayList<Integer>();
				//BasicDBObject docUser = null;
				for (JSONObject u : catg.users) {
					//docUser = new BasicDBObject();
					Integer userID = (Integer) u.get("_id");
//					docUser.append("age", u.get("age"));
//					docUser.append("sex", u.get("sex"));
//					docUser.append("ocupation", u.get("ocupation"));
//					docUser.append("zipCode", u.get("zipCode"));					
					listOfUsers.add(userID);
				}
				docCateg.put("users", listOfUsers);
				
				//add each rate to the collection categories
				List<String> listOfRates = new ArrayList<String>();
				//BasicDBObject docRate = null;
				for (JSONObject r : catg.rates) {
					//docRate = new BasicDBObject();
					JSONObject rateId = (JSONObject)r.get("_id");
					String rateID = (String) rateId.get("$oid");
//					docRate.append("userID", r.get("userID"));
//					docRate.append("movieID", r.get("movieID"));
//					docRate.append("rating", r.get("rating"));
//					docRate.append("timestamp", r.get("timestamp"));					
					listOfRates.add(rateID);
				}
				docCateg.put("rates", listOfRates);
				
				catCollection.insert(docCateg);
			}
			long tempoFinal = System.currentTimeMillis();
			long result = tempoFinal-tempoInicial;
			System.out.println("Tempo total: " + result);
	}
	
	public static boolean hasCategory(List<Category> list, String catName) {
		for (Category category : list) {
			if(category.name.equals(catName))
				return true;
		}
		return false;
	}
	
	public static Category getCategoryByName(List<Category> list, String name){
		for (Category category : list) {
			if(category.name.equals(name))
				return category;
		}
		return null;
	}
	
	public static void addUserAndRateToSetOfCategories (List<Category> list, JSONArray userCatgs, JSONObject user, JSONObject rate) throws JSONException {
		for (int i = 0; i < userCatgs.length(); i++) {
			Category c = Connection.getCategoryByName(list, userCatgs.get(i).toString());
			if(!Connection.categoryHasUser(c, user)) {
				c.users.add(user);
			}
			if(!Connection.categoryHasRate(c, rate)) {
				c.rates.add(rate);
			}
		}
	}
	
	public static boolean categoryHasUser (Category cat, JSONObject newUser) throws JSONException {
		for (JSONObject user : cat.users) {
			if(user.get("_id").equals(newUser.get("_id")))
					return true;
		}
		return false;
	}
	
	public static boolean categoryHasRate (Category cat, JSONObject newRate) throws JSONException {
		for (JSONObject rate : cat.rates) {
			if(rate.get("_id").equals(newRate.get("_id")))
					return true;
		}
		return false;
	}
}
