package spring

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.neo4j.config.{Neo4jConfiguration, EnableNeo4jRepositories}
import org.springframework.boot.{SpringApplication, CommandLineRunner}
import org.neo4j.graphdb.{Transaction, GraphDatabaseService}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import org.neo4j.kernel.impl.util.FileUtils
import reference.RefList
import scala.collection.mutable
import utils.CustomLogger

@Configuration
@EnableNeo4jRepositories
class ApplicationScala extends Neo4jConfiguration with CommandLineRunner with CustomLogger {
  setBasePackage("spring")

  @Bean(destroyMethod = "shutdown") def graphDatabaseService: GraphDatabaseService = {
    new GraphDatabaseFactory().newEmbeddedDatabase(Runner.dbNameReal)
  }

  @Autowired var personRepository: AuthorRepository = _

  override def run(args: String*): Unit = {}

  def test() = {
    trans {
      val author = personRepository.findByName("Глибовець М. М. ")
      info(author.toString)
    }
  }

  def runPerson(refer: Set[RefList]) = {

    for (reflist <- refer) {
      info("Persisting to DB article:  " + reflist.getTitle + " by " + reflist.getAuth_old)
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
      info("Persisted to DB in " + ti + " seconds")
      //        }
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
      case x: Throwable => info(x.getMessage)
    } finally {
      if (tx != null) {
        tx.close()
      }
    }
  }
}


object Runner extends CustomLogger {

  var cleaned: Boolean = false
  val dbNameReal = "target/real.db"
  var ref: mutable.Set[RefList] = _
  lazy val ctx = SpringApplication.run(classOf[ApplicationScala])

  def application = ctx.getBean(classOf[ApplicationScala])

  def runPerson(refer: java.util.Set[RefList]) = {
    application.runPerson(refer.toArray(new Array[RefList](refer.size())).toSet)
  }

  def run() {
    if (!cleaned) {
      FileUtils.deleteRecursively(new java.io.File(dbNameReal))
      cleaned = true
      info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Cleaning DB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }
  }
}

object Run extends App with CustomLogger {
  val ctx = SpringApplication.run(classOf[ApplicationScala])
  info("Let's inspect the beans provided by Spring Boot:")
  ctx.getBeanDefinitionNames.foreach(info)
}
