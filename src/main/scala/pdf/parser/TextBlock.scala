package pdf.parser

import org.apache.pdfbox.util.{PDFTextStripper, TextPosition}
import PDFTextStripper.{WordWithTextPositions => Word}
import pdf.parser.TextBlock.{Document, Line}
import java.util.{List => JList}
import collection.JavaConversions._
import collection.JavaConverters._
import org.apache.pdfbox.util.TextPosition
import java.util
import scala.util


class TextBlock(val list: Document, val oheight: Double) {

  implicit def con(v: TextPosition): Position = Position(v)

  val blocks = list.map(x => getItem(x)).filter(!_.isEmpty).map(_.get)

  val topLeft = Point(blocks.map(_.topLeft.x).min, blocks.map(_.topLeft.y).max)
  val bottomRight = Point(blocks.map(_.bottomRight.x).max, blocks.map(_.bottomRight.y).min)
  val height = topLeft.y - bottomRight.y
  val width = bottomRight.x - topLeft.x

  def findWhite = RectangleUtils.findWhiteSpaces(Rectangle(topLeft, bottomRight), blocks)

  def findWhiteJava = seqAsJavaList(findWhite)

  def getItem(line: Line): Option[Rectangle] = {
    val startPositions: JList[TextPosition] = line.get(0).getTextPositions
    val endPositions: JList[TextPosition] = line.get(line.size - 1).getTextPositions
    val startPosition: TextPosition = startPositions.get(0)
    val endPosition: TextPosition = endPositions.get(endPositions.size() - 1)

    val x0 = startPosition.getXDirAdj
    val y0 = oheight - startPosition.getYDirAdj
    val x1 = endPosition.getXDirAdj + endPosition.getWidthDirAdj
    val y1 = oheight - (math.max((startPosition.getYDirAdj + startPosition.getHeightDir), (endPosition.getYDirAdj + endPosition.getHeightDir)))

    if (Rectangle.isValid(Point(x0, y0), Point(x1, y1))) {
      Some(Rectangle(Point(x0, y0), Point(x1, y1), startPosition.getCharacter, endPosition.getCharacter))
    } else {
      None
    }
  }

  override def toString: String = blocks.mkString("\n")

  def print: String = "[" + topLeft.toString + ", " + bottomRight.toString + ", " + width + ", " + height + "]"

  def printWhite: String = findWhite mkString "\n"

  def printColumns : String = RectangleUtils.findWhites(Rectangle(topLeft, bottomRight), blocks) mkString "\n"

  def getBlockContent : List[TextBlock.Block] = {

    List()
  }

  def white: List[Rectangle] = findWhite

  //  override def toString: String = list.map((x: Line) => x.map((x: Word) => x.getText).mkString(" ")).mkString("\n")
}


object TextBlock {
  type Block = List[Line]
  type Line = List[Word]
  type Document = List[Line]

  def apply(doc: JList[JList[Word]], oheight: Double) = {
    var tmp: List[Line] = List[Line]()

    for (i <- 0 to doc.size() - 1) {
      tmp :+= doc.get(i).toList
    }

    new TextBlock(tmp, oheight)
  }
}


case class Position(position: TextPosition) {
  def x = position.getXDirAdj

  def y = position.getYDirAdj

  def width = position.getWidthDirAdj

  def height = position.getHeightDir

  def character = position.getCharacter
}