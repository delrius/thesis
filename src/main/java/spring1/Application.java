package spring1;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Configuration
@EnableNeo4jRepositories
//@EnableTransactionManagement
@Transactional
public class Application extends Neo4jConfiguration implements CommandLineRunner {

    public Application() {
        setBasePackage("spring1");
    }

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("target/accessingdataneo4j.db");
    }

    @Autowired
    PersonRepository personRepository;

    public void run(String... args) throws Exception {
        PersonJ greg = new PersonJ("Greg");
        PersonJ roy = new PersonJ("Roy");
        PersonJ craig = new PersonJ("Craig");

        System.out.println("Before linking up with Neo4j...");
        for (PersonJ person : new PersonJ[]{greg, roy, craig}) {
            System.out.println(person);
        }

        try (Transaction tx = graphDatabase().beginTx()) {
            personRepository.save(greg);
            personRepository.save(roy);
            personRepository.save(craig);

            greg = personRepository.findByName(greg.name);
            greg.worksWith(roy);
            greg.worksWith(craig);
            personRepository.save(greg);

            roy = personRepository.findByName(roy.name);
            roy.worksWith(craig);
            // We already know that roy works with greg
            personRepository.save(roy);

            // We already know craig works with roy and greg

            tx.success();
        }

        try (Transaction tx = graphDatabase().beginTx()) {
            System.out.println("Lookup each person by name...");
            for (String name : new String[]{greg.name, roy.name, craig.name}) {
                System.out.println(personRepository.findByName(name));
            }
            tx.success();
        }

        try (Transaction tx = graphDatabase().beginTx()) {
            System.out.println("Looking up who works with Greg...");
            for (PersonJ person : personRepository.findByTeammatesName("Greg")) {
                System.out.println(person.name + " works with Greg.");
            }
            tx.success();
        }

    }

    public static void main(String[] args) throws Exception {
        FileUtils.deleteRecursively(new File("target/accessingdataneo4j.db"));

        SpringApplication.run(Application.class, args);
    }

}