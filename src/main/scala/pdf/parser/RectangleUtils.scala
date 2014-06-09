package pdf.parser

import scala.collection.mutable
import scala.List
import scala.util.Random
import scala.collection.immutable.{List => ImmutableList}
import scala.annotation.tailrec

object RectangleUtils {

  type QueueElem = (Rectangle, List[Rectangle])

  val rectWidthMin = 1d
  val rectMinDistFromBorder = 5d

  private def findCandidates(bound: Rectangle, obstacleList: List[Rectangle], checkEmpty: Boolean = true): List[Rectangle] = {

    val queue = new mutable.PriorityQueue[QueueElem]()(queueElemOrdering)
    queue.enqueue((bound, obstacleList))

    var rectangles = List[Rectangle]()

    while (!queue.isEmpty) {
      val tuple: (Rectangle, List[Rectangle]) = queue.dequeue()
      val r = tuple._1
      val obstacles = tuple._2
      if (!obstacles.isEmpty) {

        val pivot = pick(obstacles)
        val list = findByPivot(pivot, r)
        val obstWithoutPivot = obstacles.filterNot(_.equals(pivot))
        val que = for (l <- list) yield (l, findInner(l, obstWithoutPivot))

        que.foreach(queue.enqueue(_))
      } else {
        checkRectangleForNormalWhiteSpace(rect = r, bound = bound, checkEmpty).map(r => rectangles +:= r)
      }
    }

    rectangles
  }

  private def findCandidatesVertical(bound: Rectangle, obstacleList: List[Rectangle]): List[Rectangle] = {
    filterSimilars(findCandidates(bound, obstacleList, checkEmpty = false).sortWith(_.width > _.width))
  }

  def findWhiteSpaces(bound: Rectangle, obstacleList: List[Rectangle]): List[Rectangle] = {
    val result = filterSimilars(findCandidates(bound, obstacleList).sortWith(_.height > _.height))
    if (result.isEmpty) List.empty[Rectangle]
    else {
      val best = result.head
      result.take(5)
        .filter(r => math.abs(r.topLeft.x - best.topLeft.x) < 2 && math.abs(r.bottomRight.x - best.bottomRight.x) < 2)
        .filter((r) => {
        val left = Rectangle(Point(bound.topLeft.x, r.topLeft.y), Point(r.topLeft.x, r.bottomRight.y))
        val right = Rectangle(Point(r.bottomRight.x, r.topLeft.y), Point(bound.bottomRight.x, r.bottomRight.y))
        val lside = findInner(left, obstacleList)
        val rside = findInner(right, obstacleList)

        val some0 = lside.size == 0 || rside.size == 0
        val max = math.max(lside.size, rside.size)
        val min = math.min(lside.size, rside.size)
        !some0 && (max / min <= 2)
      })
    }
  }


  def findWhites(bound: Rectangle, obstacleList: List[Rectangle]): List[Rectangle] = {
    val foundList = findWhiteSpaces(bound, obstacleList)
    if (!foundList.isEmpty) {
      val found = foundList(0)
      val lineSpacing = findMostRecent(obstacleList)
      val whiteBound = Rectangle(Point(bound.topLeft.x, found.topLeft.y), Point(bound.bottomRight.x, found.bottomRight.y))
      val obs = findInner(whiteBound, obstacleList)
      findCandidatesVertical(whiteBound, obs).filter(x => x.height > lineSpacing + 5).sortBy(_.topLeft.y).reverse
    } else {
      List.empty
    }
  }

  private def findMostRecent(obstacleList: List[Rectangle]): Int = {
    var map1 = mutable.Map[Int, Int]()
    for (i <- 0 to obstacleList.size - 2) {
      val curr = obstacleList(i)
      val next = obstacleList(i + 1)
      val dist = (curr.bottomRight.y - next.topLeft.y).toInt
      val thisDist = map1.get(dist)
      thisDist match {
        case Some(v) => map1 += (dist -> (v + 1))
        case None => map1 += (dist -> 1)
      }
    }
    map1.maxBy(_._2)._1
  }

  def filterSimilars(target: List[Rectangle]): List[Rectangle] = {

    @tailrec
    def checkOutBoundHelper(target: List[Rectangle], acc: List[Rectangle]): List[Rectangle] = {
      target match {
        case List() => acc
        case x :: tail =>
          val isUnique = acc.filter(getInnerPart(x, _).nonEmpty).isEmpty
          if (isUnique) checkOutBoundHelper(tail, acc :+ x) else checkOutBoundHelper(tail, acc)
        
      }
    }

    checkOutBoundHelper(target, List.empty)
  }

  def checkRectangleForNormalWhiteSpace(rect: Rectangle, bound: Rectangle, checkEmpty: Boolean): Option[Rectangle] = {
    rect match {
      case r@Rectangle(Point(x1, _), Point(x2, _), _, _) if (x2 - x1 > rectWidthMin)
        && (!checkEmpty || ((bound.bottomRight.x - x2 > rectMinDistFromBorder) && (x1 - bound.topLeft.x > rectMinDistFromBorder))) => Some(r)
      case _ => None
    }
  }

  def findByPivot(pivot: Rectangle, bound: Rectangle): List[Rectangle] = {
    /*
    r0 = (pivot.x1,r.y0,r.x1,r.y1)
    r1 = (r.x0,r.y0,pivot.x0,r.y1)
    r2 = (r.x0,pivot.y1,r.x1,r.y1)
    r3 = (r.x0,r.y0,r.x1,pivot.y0)
     */
    val r1 = checkRectangle(Point(pivot.bottomRight.x, bound.topLeft.y), Point(bound.bottomRight.x, bound.bottomRight.y))
    val r2 = checkRectangle(Point(bound.topLeft.x, bound.topLeft.y), Point(pivot.topLeft.x, bound.bottomRight.y))
    val r3 = checkRectangle(Point(bound.topLeft.x, pivot.bottomRight.y), Point(bound.bottomRight.x, bound.bottomRight.y))
    val r4 = checkRectangle(Point(bound.topLeft.x, bound.topLeft.y), Point(bound.bottomRight.x, pivot.topLeft.y))

    val list = List(r1, r2, r3, r4).foldLeft(ImmutableList[Rectangle]()) {
      (acc, x) => if (x.isEmpty) acc else acc :+ x.get
    }

    list filterNot (x => (x.bottomRight.x - x.topLeft.x < 1) && (x.topLeft.y - x.bottomRight.y < 1))
  }

  def addIfNotNull[T](value: Option[T], acc: List[T]): List[T] = {
    value match {
      case Some(v) => acc :+ v
      case None => acc
    }
  }

  def findInner(value: Rectangle, rectangles: List[Rectangle]): List[Rectangle] = {
    var list = ImmutableList[Rectangle]()

    for (rect <- rectangles) {
      getInnerPart(rect, value) match {
        case Some(v) => list :+= v
        case _ =>
      }
    }

    list
  }

  def getInnerPart(inner: Rectangle, outer: Rectangle): Option[Rectangle] = {
    val topLeftX = {
      if ((inner.topLeft.x >= outer.topLeft.x) && (inner.topLeft.x <= outer.bottomRight.x)) {
        inner.topLeft.x
      } else if (inner.topLeft.x < outer.topLeft.x) {
        outer.topLeft.x
      } else {
        -1d
      }
    }

    val topLeftY = {
      if ((inner.topLeft.y <= outer.topLeft.y) && (inner.topLeft.y >= outer.bottomRight.y)) {
        inner.topLeft.y
      } else if (inner.topLeft.y > outer.topLeft.y) {
        outer.topLeft.y
      } else {
        -1d
      }
    }

    val bottomRightX = {
      if ((inner.bottomRight.x >= outer.topLeft.x) && (inner.bottomRight.x <= outer.bottomRight.x)) {
        inner.bottomRight.x
      } else if (inner.bottomRight.x > outer.bottomRight.x) {
        outer.bottomRight.x
      } else {
        -1d
      }
    }

    val bottomRightY = {
      if ((inner.bottomRight.y <= outer.topLeft.y) && (inner.bottomRight.y >= outer.bottomRight.y)) {
        inner.bottomRight.y
      } else if (inner.bottomRight.y < outer.bottomRight.y) {
        outer.bottomRight.y
      } else {
        -1d
      }
    }

    checkRectangle(topLeft = Point(topLeftX, topLeftY), bottomRight = Point(bottomRightX, bottomRightY))
  }

  def checkRectangle(topLeft: Point, bottomRight: Point): Option[Rectangle] = {
    val sidesCorrect = topLeft.x > 0 && topLeft.y > 0 && bottomRight.x > 0 && bottomRight.y > 0
    val validRect = bottomRight.x - topLeft.x > rectWidthMin && topLeft.y - bottomRight.y > rectWidthMin
    if (sidesCorrect && validRect) Some(Rectangle(topLeft, bottomRight)) else None
  }

  implicit def queueElemOrdering = new Ordering[QueueElem] {
    def compare(t1: QueueElem, t2: QueueElem): Int = t1._1.quality compareTo t2._1.quality
  }

  def pick(rect: List[Rectangle]) = {
    val r = Random
    rect(r.nextInt(rect.size))
  }
}