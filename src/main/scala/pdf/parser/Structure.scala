package pdf.parser

import TextBlock.Block
import collection.JavaConversions._
import org.apache.pdfbox.util.TextPosition
import scala.annotation.tailrec

case class Structure(val blocks: List[Block]) {
  lazy val mostUsedFont: Int = 10 //FIXME: findMostUsedFontSize(blocks)
  type FontMap = Map[Int, BigDecimal]

  def findReferences(): List[Block] = {
    blocks.filter(x => {
      val most = findMostUsedFontSize(List(x))
      most != mostUsedFont && most > 0 && most < mostUsedFont
    })
  }

  def findMostUsedFontSize(blocksLocal: List[Block]): Int = {
    val text = blocksLocal.flatten.flatten.map(_.getTextPositions).flatten.toList

    def addToMap(map: FontMap, elem: Int): FontMap = {
      map.get(elem) match {
        case Some(v) => map.updated(elem, v + 1)
        case None => map.updated(elem, 1)
      }
    }

    @tailrec
    def helper(map: FontMap, curr: List[TextPosition]): FontMap = {
      curr match {
        case List() => map
        case head :: tail => helper(addToMap(map, math.round(head.getFontSizeInPt)), tail)
      }
    }

    helper(Map.empty[Int, BigDecimal], text) match {
      case v if v.isEmpty => -1
      case v if !v.isEmpty => v.maxBy (_._2)._1
    }
  }
}
