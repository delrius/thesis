package utils

import scalax.file.Path

object FileUtils {
  def clean(fileName: String) = {

    val out = Path("tmp", fileName.replaceAll("pdf", "txt"))
    out.deleteIfExists(true)

  }
}
