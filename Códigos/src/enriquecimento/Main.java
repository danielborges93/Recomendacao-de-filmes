package enriquecimento;

import java.util.ArrayList;

import enriquecimento.migration.IMigration;
import enriquecimento.migration.MovieMigrations;
import enriquecimento.migration.RateMigration;
import enriquecimento.migration.UserMigration;

public class Main {
	public static void main(String[] args) throws Exception {
		ArrayList<IMigration> migrationList = new ArrayList<>();
		
		migrationList.add(new MovieMigrations());
		migrationList.add(new UserMigration());
		migrationList.add(new RateMigration());
		
		for (IMigration iMigration : migrationList) {
			iMigration.init();
		}
	}
}
