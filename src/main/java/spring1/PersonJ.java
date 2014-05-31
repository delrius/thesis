package spring1;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class PersonJ {

    @GraphId
    Long id;
    public String name;

    public PersonJ() {
    }

    public PersonJ(String name) {
        this.name = name;
    }

    @RelatedTo(type = "TEAMMATE", direction = Direction.BOTH)
    @Fetch
    public Set<PersonJ> teammates;

    public void worksWith(PersonJ person) {
        if (teammates == null) {
            teammates = new HashSet<PersonJ>();
        }
        teammates.add(person);
    }

    public String toString() {
        String results = name + "'s teammates include\n";
        if (teammates != null) {
            for (PersonJ person : teammates) {
                results += "\t- " + person.name + "\n";
            }
        }
        return results;
    }

}