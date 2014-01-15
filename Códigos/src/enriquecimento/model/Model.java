package enriquecimento.model;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;

public abstract class Model extends BasicDBObject implements IModel{
	private static final String HOST = "localhost";
	private static final int PORT = 27017;
	private static final String DATABASE = "BigData";

    //protected ObjectId id;
    protected DB db;
	protected MongoClient client;
	protected DBCollection collection;

	public Model() throws UnknownHostException {
        client = new MongoClient(HOST,PORT);
		db = client.getDB(DATABASE);
        collection = db.getCollection(this.getClass().getSimpleName());
	}

    public ObjectId getId() {
        return (ObjectId) get("_id");
    }

    public BasicDBObject setId(ObjectId id) {
        put("_id",id);
        return this;

    }

    @Override
    public void save() {
        collection.save(this);
    }

    public DBCursor find() {
        return collection.find(this);
    }

    @Override
    public boolean isOnline() {
        return collection.findOne() != null;
    }
}
