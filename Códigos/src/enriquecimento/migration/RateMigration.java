package enriquecimento.migration;

import java.io.File;
import java.util.Scanner;

import org.bson.types.ObjectId;

import enriquecimento.model.Rate;

public class RateMigration implements IMigration {

	@Override
	public void init() throws Exception {
		Scanner scan = new Scanner(new File("ml-100k/u.data"));
		Rate rate = new Rate();
		while (scan.hasNext()) {
			String userLine = (String) scan.next();
			String[] movieItemArray = userLine.split("\\|");
			
			rate.put("_id",new ObjectId())
				.put("userID", new Integer(movieItemArray[0]))
				.put("movieID",new Integer(movieItemArray[1]))
				.put("rating",new Integer(movieItemArray[2]))
				.put("timestamp",new Integer(movieItemArray[3]))
				.save();
		}
		
		scan.close();
		System.out.println("Rate Migration Complete");
	}

}
