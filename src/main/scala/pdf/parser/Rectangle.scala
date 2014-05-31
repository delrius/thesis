package pdf.parser


case class Point(x: Double, y: Double)

case class Rectangle(topLeft: Point, bottomRight: Point, st: String = "", en: String = "") {

  require(Rectangle.isValid(topLeft, bottomRight), toString)

  val width = bottomRight.x - topLeft.x
  val height = topLeft.y - bottomRight.y

  val quality = width * height

  def this(start: Point, width: Double, height: Double) = this(start, Point(start.x + width, start.y - height))

  def this(start: Point, width: Double, height: Double, s: String, e: String) = this(start, Point(start.x + width, start.y - height), s, e)

  override def toString: String = "[" + topLeft.x + ", " + topLeft.y + ", " + st + "]" + "|---|" + "[" + bottomRight.x + ", " + bottomRight.y + ", " + en + "]"
}

object Rectangle {

  def isValid(s: Point, e: Point): Boolean = {
    ((e.x - s.x) > RectangleUtils.rectWidthMin) && ((s.y - e.y) > RectangleUtils.rectWidthMin)
  }

  def orderedRectangle(f: Rectangle): Ordered[Rectangle] = new Ordered[Rectangle] {
    def compare(other: Rectangle) = f.quality.compare(other.quality)
  }

  def apply(start: Point, width: Double, height: Double) = new Rectangle(start, width, height)

  def apply(start: Point, width: Double, height: Double, s: String, e: String) = new Rectangle(start, width, height)

}