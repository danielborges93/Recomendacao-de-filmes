package enriquecimento.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public interface IModel {
	public void save();
    public DBCursor find();
    public boolean isOnline();

}
