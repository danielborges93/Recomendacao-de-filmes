package enriquecimento.migration;

import java.io.File;
import java.util.Scanner;

import enriquecimento.model.User;

public class UserMigration implements IMigration {

	@Override
	public void init() throws Exception {
		Scanner scan = new Scanner(new File("ml-100k/u.user"));
		User user = new User();
		while (scan.hasNext()) {
			String userLine = (String) scan.next();
			String[] movieItemArray = userLine.split("\\|");
			
			user.put("_id",new Integer(movieItemArray[0]))
				.put("age",movieItemArray[1])
				.put("sex",movieItemArray[2])
				.put("ocupation",movieItemArray[3])
				.put("zipCode",movieItemArray[4])
				.save();
		}
		
		scan.close();
		System.out.println("User Migration Complete");
	}

}
