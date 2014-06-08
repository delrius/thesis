package utils

import org.scalatest._
import pdf.parser.{Point, Rectangle}
import readers.PdfBoxReader

abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors {

  def test20 = test(20) _

  def test1 = test(1) _

  def func(n: Int, pos: Int = 0, actual: Rectangle) = {
    val block = PdfBoxReader.getTextBlock(n)
    checkRectangles(block.white(pos), actual)
  }


  def contain(n: Int, actual: Rectangle) = {
    val block = PdfBoxReader.getTextBlock(n)
    var result = false
    for (r <- block.white if !result) {
      if (checkRectanglesSimple(actual, r)) {
        result = true
      }
    }
    withClue("Should contain rectangle " + actual.toString) {
      result should be(right = true)
    }
  }


  def test(n: Int)(f: => Unit): Unit = {
    for (i <- 0 to n) {
      f
    }
  }

  def checkRectangles(expected: Rectangle, actual: Rectangle) = {
    actual match {
      case Rectangle(Point(x1, y1), Point(x2, y2), _, _) =>
        withClue("x top => expected = " + x1 + "; actual = " + expected.topLeft.x) {
          math.abs(x1 - expected.topLeft.x) should be <= precision
        }
        withClue("y bottom => expected = " + x2 + "; actual = " + expected.bottomRight.x) {
          math.abs(x2 - expected.bottomRight.x) should be <= precision
        }
        withClue("y top => expected = " + y1 + "; actual = " + expected.topLeft.y) {
          math.abs(y1 - expected.topLeft.y) should be <= precision
        }
        withClue("y bottom => expected = " + y2 + "; actual = " + expected.bottomRight.y) {
          math.abs(y2 - expected.bottomRight.y) should be <= precision
        }
    }
  }

  def checkRectanglesSimple(expected: Rectangle, actual: Rectangle): Boolean = {
    actual match {
      case Rectangle(Point(x1, y1), Point(x2, y2), _, _) =>
        val r1 = math.abs(x1 - expected.topLeft.x) <= precision
        val r2 = math.abs(x2 - expected.bottomRight.x) <= precision
        val r3 = math.abs(y1 - expected.topLeft.y) <= precision
        val r4 = math.abs(y2 - expected.bottomRight.y) <= precision
        r1 && r2 && r3 && r4
      case _ => false
    }
  }

  val precision = 1.5d
}