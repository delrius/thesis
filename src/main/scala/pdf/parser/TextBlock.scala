package pdf.parser

import org.apache.pdfbox.util.PDFTextStripper
import PDFTextStripper.{WordWithTextPositions => Word}
import pdf.parser.TextBlock.{Document, Line}
import java.util.{List => JList}
import collection.JavaConversions._
import org.apache.pdfbox.util.TextPosition


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

  lazy val structure: Structure = Structure(getBlocks)

  lazy val references = structure findReferences

  def getBlocks = {
    var blocksR = List.empty[TextBlock.Block]
    for (whitespace <- findWhite) {
      val bound = Rectangle(Point(topLeft.x, whitespace.topLeft.y), Point(bottomRight.x, whitespace.bottomRight.y))
      val gaps = RectangleUtils.findWhites(bound, RectangleUtils.findInner(bound, blocks))
      if (gaps.isEmpty) {
        val col1 = Rectangle(Point(topLeft.x, whitespace.topLeft.y), Point(whitespace.topLeft.x, whitespace.bottomRight.y))
        val col2 = Rectangle(Point(whitespace.bottomRight.x, whitespace.topLeft.y), Point(bottomRight.x, whitespace.bottomRight.y))
        blocksR :+= getLines(col1)
        blocksR :+= getLines(col2)
      } else {
//        require(gaps.size < 2, gaps.size)
        val gap = gaps.get(0)
        val col1 = RectangleUtils.checkRectangle(Point(topLeft.x, whitespace.topLeft.y), Point(whitespace.topLeft.x, gap.topLeft.y))
        val col2 = RectangleUtils.checkRectangle(Point(whitespace.bottomRight.x, whitespace.topLeft.y), Point(bottomRight.x, gap.topLeft.y))
        val col3 = RectangleUtils.checkRectangle(Point(topLeft.x, gap.bottomRight.y), Point(whitespace.topLeft.x, whitespace.bottomRight.y))
        val col4 = RectangleUtils.checkRectangle(Point(whitespace.bottomRight.x, gap.bottomRight.y), Point(bottomRight.x, whitespace.bottomRight.y))

        List[Option[Rectangle]](col1, col2, col3, col4).filter(!_.isEmpty).map(x => getLines(x.get)).toList.foreach(x => blocksR +:= x)
      }
    }
    blocksR.reverse
  }

  def printReferences = references foreach blockPrinter

  def printBlocks = {
    val bl = getBlocks
    println(Structure(bl).findMostUsedFontSize(bl) + "<--------------------------------------")
    bl foreach blockPrinter
  }

  def blockPrinter(block: TextBlock.Block): Unit = {
    println("!!!!!!!!!!!!!!!!!!start!!!!!!!!!!!!!!!!")
    block.map(l => l.map(_.getText).toList.mkString(" ")).foreach(println)
    println("!!!!!!!!!!!!!!!!!!end!!!!!!!!!!!!!!!!!!")
    println
  }

  def getLines(r: Rectangle): TextBlock.Block = {
    list.filter(isInRect(_, r))
  }

  def isInRect(line: TextBlock.Line, r: Rectangle) = {
    val startPositions: JList[TextPosition] = line.get(0).getTextPositions
    val endPositions: JList[TextPosition] = line.get(line.size - 1).getTextPositions
    val startPosition: TextPosition = startPositions.get(0)
    val endPosition: TextPosition = endPositions.get(endPositions.size() - 1)

    val isX0 = startPosition.getXDirAdj >= r.topLeft.x || areClose(startPosition.getXDirAdj, r.topLeft.x)
    val isY0 = startPosition.getYDirAdj >= tY(r.topLeft.y) || areClose(startPosition.getYDirAdj, tY(r.topLeft.y))
    val isX1 = endPosition.getXDirAdj <= r.bottomRight.x || areClose(endPosition.getXDirAdj, r.bottomRight.x)
    val isY1 = endPosition.getYDirAdj <= tY(r.bottomRight.y) || areClose(endPosition.getYDirAdj, tY(r.bottomRight.y))

    isX0 && isX1 && isY0 && isY1
  }

  def areClose(x: Double, y: Double) = math.abs(x - y) < 1

  def tY(y: Double) = oheight - y

  override def toString: String = blocks.mkString("\n")

  def print: String = "[" + topLeft.toString + ", " + bottomRight.toString + ", " + width + ", " + height + "]"

  def printWhite: String = findWhite mkString "\n"

  def printColumns: String = RectangleUtils.findWhites(Rectangle(topLeft, bottomRight), blocks) mkString "\n"

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