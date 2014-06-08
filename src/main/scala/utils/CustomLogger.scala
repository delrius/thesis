package utils

import com.typesafe.scalalogging.slf4j.LazyLogging

trait CustomLogger extends LazyLogging {
  def info(str: String) = logger.info(str + "\n")
}

object CustomLog extends LazyLogging {
  def info(str: String) = logger.info(str + "\n")
  def error(str: String) = logger.error(str + "\n")
}
