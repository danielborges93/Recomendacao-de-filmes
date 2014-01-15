package enriquecimento.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import enriquecimento.model.Movie;

public class MovieMigrations  implements IMigration{
	
	public void init() throws Exception {
		Scanner scan = new Scanner(new File("ml-100k/u.item"));
		//BasicDBObject movie = new BasicDBObject();
		Movie movie = new Movie();
		while(scan.hasNext()){
			ArrayList<String> categories = new ArrayList<>();
			String line = scan.nextLine();
			String[] movieItemArray = line.split("\\|");
			movie.put("_id", new Integer(movieItemArray[0]))
				.put("name", movieItemArray[1])
				.put("releaseDate", movieItemArray[2])
				.put("videoReleaseDate", movieItemArray[3])
				.put("imdbURL", movieItemArray[4])
				.save();
			
			for (int i = 5; i < movieItemArray.length; i++) {
				if(movieItemArray[i].equals("1")){
					switch (i) {
					case 5:
						categories.add("Unknown");
					break;
					case 6:
						categories.add("Action");
					break;
					case 7:
						categories.add("Adventure");
					break;
					case 8:
						categories.add("Animation");
					break;
					case 9:
						categories.add("Children's");
					break;
					case 10:
						categories.add("Comedy");
					break;
					case 11:
						categories.add("Crime");
					break;
					case 12:
						categories.add("Documentary");
					break;
					case 13:
						categories.add("Drama");
					break;
					case 14:
						categories.add("Fantasy");
					break;
					case 15:
						categories.add("Film-Noir");
					break;
					case 16:
						categories.add("Horror");
					break;
					case 17:
						categories.add("Musical");
					break;
					case 18:
						categories.add("Mystery");
					break;
					case 19:
						categories.add("Romance");
					break;
					case 20:
						categories.add("Sci-Fi");
					break;
					case 21:
						categories.add("Thriller");
					break;
					case 22:
						categories.add("War");
					break;
					case 23:
						categories.add("Western");
					break;
					default:
						throw new Exception("Categoria não definida");
					}
				}
				movie.put("categories", categories);
				movie.save();
			}
		}
		scan.close();
		System.out.println("Movie Migration Complete");
	}

}
