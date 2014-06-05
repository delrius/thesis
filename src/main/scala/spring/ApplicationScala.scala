package spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.config.{Neo4jConfiguration, EnableNeo4jRepositories}
import org.springframework.boot.{SpringApplication, CommandLineRunner}
import org.neo4j.graphdb.{Transaction, GraphDatabaseService}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import org.neo4j.kernel.impl.util.FileUtils
import scala.collection.JavaConverters._
import reference.{RefList, Reference}
import scala.collection.mutable
import org.springframework.data.neo4j.conversion.Handler

@Configuration
@EnableNeo4jRepositories
class ApplicationScala extends Neo4jConfiguration with CommandLineRunner {
  setBasePackage("spring")

  @Bean(destroyMethod = "shutdown") def graphDatabaseService: GraphDatabaseService = {
    new GraphDatabaseFactory().newEmbeddedDatabase(Runner.dbNameReal)
  }

  @Autowired var personRepository: AuthorRepository = _

  val names: List[String] = List("Greg", "Roy", "Craig")

  override def run(args: String*): Unit = {
    runPerson()
    // test
  }

  def test = {
    trans {
      //      personRepository.findAll().handle(new Handler[Author] {
      //        override def handle(value: Author): Unit = {
      //          println(value.name)
      //        }
      //      })
      val author = personRepository.findByName("Глибовець М. М. ")
      println(author.toString)
    }
  }

  def runPerson() = {
    val c: mutable.Set[RefList] = Runner.ref

    for (reflist <- c) {
      println("Persisting to DB article:  " + reflist.getTitle + " by " + reflist.getAuth_old)
      val st = System.currentTimeMillis()
      val authors = reflist.getAuthors
      //        for (i <-0 to authors.size() - 1) {
      val name = authors.get(0)
      val isin: Boolean = isInRepo(name)
      val author = if (!isin) Author(name) else get(name)
      if (!isin) {
        save(List(author))
      }

      val references = reflist.getReferenceList
      for (k <- 0 to references.size() - 1) {
        val ref = references.get(k)
        val auth = ref.getAuthors
        val artname = ref.getTitle

        for (j <- 0 to auth.size() - 1) {
          val isRf: Boolean = isInRepo(auth.get(j))
          val referee = if (!isRf) Author(auth.get(j)) else get(auth.get(j))
          if (!isRf) {
            save(List(referee))
          }
          trans {
            author.workWith(referee, artname, reflist.getTitle)
            personRepository.save(author)
          }
        }
      }

      val end = System.currentTimeMillis()
      val ti = (end - st) / 1000d
      println("Persisted to DB in " + ti + " seconds")
      //        }
    }
  }

  def tmp = {

    val greg = new Author("Greg")
    val roy = new Author("Roy")
    val craig = new Author("Craig")

    val persons: List[Author] = List(greg, roy, craig)
    // persons foreach println
    save(persons)
    lookup
  }

  def lookup = {
    println("Lookup each person by name...")
    trans {
      for (name <- names) {
        println(personRepository.findByName(name))
      }
    }
  }

  def get(name: String): Author = {
    var author: Author = null
    trans {
      author = personRepository.findByName(name)
    }
    author
  }

  def isInRepo(name: String): Boolean = {
    var exists: Boolean = false
    trans {
      exists = personRepository.findByName(name) != null
    }
    exists
  }

  def save(list: List[Author]) = {
    trans {
      for (l <- list) {
        if (personRepository.findByName(l.name) == null) personRepository.save(l)
      }
    }
  }

  def trans[A](f: => A) {
    var tx: Transaction = null
    try {
      tx = graphDatabase().beginTx()
      f
      tx.success()
    } catch {
      case x: Throwable => println(x)
    } finally {
      if (tx != null) {
        tx.close()
      }
    }
  }
}


object Runner {

  val dbName = "target/accessingdataneo4j1.db"
  val dbNameReal = "target/real.db"
  var ref: mutable.Set[RefList] = _

  def runPerson() = new ApplicationScala().runPerson()

  def run(refer: java.util.Set[RefList]) {
    ref = asScalaSetConverter(refer).asScala
    FileUtils.deleteRecursively(new java.io.File(dbNameReal))
    SpringApplication.run(classOf[ApplicationScala])
  }
}

object Run extends App {
  SpringApplication.run(classOf[ApplicationScala])
}
