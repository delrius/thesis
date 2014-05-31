package spring1;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface PersonRepository extends GraphRepository<PersonJ> {

    PersonJ findByName(String name);

    Iterable<PersonJ> findByTeammatesName(String name);

}
