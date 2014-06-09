import AssemblyKeys._
import sbtassembly.Plugin

// put this at the top of the file

assemblySettings

version := "1.0"

organization := "naukma"

scalaVersion := "2.10.4"

jarName in assembly := "referenceParser.jar"

mainClass in assembly := Some("runner.Runner")

test in assembly := {}

mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case PathList("META-INF", "CHANGES.txt") => MergeStrategy.discard
    case PathList("META-INF", "LICENSES.txt") => MergeStrategy.discard
    case PathList("META-INF", "spring.factories") => MergeStrategy.discard
    case PathList("META-INF", "spring.provides") => MergeStrategy.discard
    case PathList("META-INF", "spring.tooling") => MergeStrategy.discard
    case PathList("overview.html") => MergeStrategy.discard
    case PathList("org", "apache", "commons", "logging", "Log.class") => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", "LogConfigurationException.class") => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", "LogFactory.class") => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", "impl", "NoOpLog.class") => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", "impl", "SimpleLog$1.class") => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", "impl", "SimpleLog.class") => MergeStrategy.first
    case PathList("org", "apache", "pdfbox", "util", "PDFTextStripper$WordSeparator.class") => MergeStrategy.rename
    case PathList("org", "apache", "pdfbox", "util", "PDFTextStripper$WordWithTextPositions.class") => MergeStrategy.rename
    case PathList("org", "apache", "pdfbox", "util", "PDFTextStripper.class") => MergeStrategy.rename
    case PathList("org", "apache", "pdfbox", "util", "TextPosition.class") => MergeStrategy.rename
    case PathList("org", "aspectj", xs @ _*) => MergeStrategy.first
    case x => old(x)
  }
}