package fr.rhumain.seeders;

import fr.rhumain.exceptions.DAOException;

import java.util.Comparator;
import java.util.List;

public class SeederRunner {
    private List<DataSeeder> seederList;

    public SeederRunner(List<DataSeeder> seederList) {
        this.seederList = seederList;
    }

    public void runAll() {
        this.seederList.
                stream().
                sorted(Comparator.comparingInt(DataSeeder::getOrder)).
                forEach(this::runOne);
    }

    public void runOne(DataSeeder seeder) {
        try {
            if(seeder.isAlreadySeeded()) {
                System.out.println("[Seeder] " + seeder.getName() + " : already seeded");
                return;
            }
            System.out.println("[Seeder] " + seeder.getName() + " : insertion...");
            seeder.seed();
            System.out.println("[Seeder] " + seeder.getName() + " : insertion complete");
        } catch (DAOException e) {
            System.out.println("[Seeder] " + seeder.getName() + " : error while seeding " + e.getMessage());
        }
    }
}
