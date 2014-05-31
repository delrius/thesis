package spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.config.{Neo4jConfiguration, EnableNeo4jRepositories}
import org.springframework.boot.{SpringApplication, CommandLineRunner}
import org.neo4j.graphdb.{Transaction, GraphDatabaseService}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import org.neo4j.kernel.impl.util.FileUtils
import scala.collection.JavaConverters._

@Configuration
@EnableNeo4jRepositories
class ApplicationScala extends Neo4jConfiguration with CommandLineRunner {
  setBasePackage("spring")

  @Bean(destroyMethod = "shutdown") def graphDatabaseService: GraphDatabaseService = {
    new GraphDatabaseFactory().newEmbeddedDatabase(Runner.dbName)
  }

  @Autowired var personRepository: PersonRepositoryScala = _

  val names: List[String] = List("Greg", "Roy", "Craig")

  override def run(args: String*): Unit = {
    val greg = new Person("Greg")
    val roy = new Person("Roy")
    val craig = new Person("Craig")

    val persons: List[Person] = List(greg, roy, craig)
    persons foreach println
    save(persons)
    lookup
    lookupteammates
  }

  def lookup = {
    println("Lookup each person by name...")
    trans {
      for (name <- names) {
        println(personRepository.findByName(name))
      }
    }
  }

  def lookupteammates = {
    println("Looking up who works with Greg...")
    trans {
      for (person:Person <- personRepository.findByTeammatesName("Greg").asScala) {
        println(person.name + " works with Greg.")
      }
    }
  }

  def save(list: List[Person]) = {
    val greg = list.head
    val roy = list.tail.head
    val craig = list.tail.tail.head

    println
    println(greg)
    println(roy)
    println(craig)

    trans {
      for (l <- list) personRepository.save(l)

      val greg1 = personRepository.findByName(greg.name)
      greg1.workWith(roy)
      greg1.workWith(craig)

      personRepository.save(greg1)

      val roy1 = personRepository.findByName(roy.name)
      roy1.workWith(craig)

      personRepository.save(roy1)
    }
  }

  def trans(f: => Unit) {
    var tx: Transaction = null
    try {
      tx = graphDatabase().beginTx()
      f
      tx.success()
    } catch {
      case x:Throwable => println(x)
    } finally {
      if (tx != null) {
        tx.close()
      }
    }
  }
}


object Runner extends App {
  val dbName = "target/accessingdataneo4j1.db"

  FileUtils.deleteRecursively(new java.io.File(dbName))
  SpringApplication.run(classOf[ApplicationScala])
}
