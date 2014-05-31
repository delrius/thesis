package utils

import pdf.parser.{Rectangle, RectangleUtils, Point}

class UtilsSpec extends UnitSpec {


  /*
    400 --------------
       |              |
       |              |
    200 --------------
        100        300
   */
  "A bound rectangle" should "be inside" in {
    val o1 = Point(100, 400)
    val o2 = Point(300, 200)

    val i1 = Point(200, 340)
    val i2 = Point(260, 210)


    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(i1, i2), Rectangle(o1, o2))

    part should be(Some(Rectangle(i1, i2)))

  }

  "A special rectangle" should "be inside" in {
    val o1 = Point(100, 400)
    val o2 = Point(300, 200)

    val i1 = Point(100, 400)
    val i2 = Point(260, 210)


    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(i1, i2), Rectangle(o1, o2))

    part should be(Some(Rectangle(i1, i2)))

  }

  "A completely 'out of bound' rectangle" should "be out" in {
    val o1 = Point(100, 400)
    val o2 = Point(300, 200)

    val part0: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(50, 500), Point(75, 450)), Rectangle(o1, o2))
    val part1: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(120, 500), Point(350, 450)), Rectangle(o1, o2))
    val part2: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(350, 500), Point(375, 450)), Rectangle(o1, o2))
    val part3: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(350, 400), Point(375, 200)), Rectangle(o1, o2))
    val part4: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(350, 100), Point(375, 40)), Rectangle(o1, o2))
    val part5: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(100, 100), Point(300, 50)), Rectangle(o1, o2))
    val part6: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(50, 100), Point(75, 40)), Rectangle(o1, o2))
    val part7: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(50, 250), Point(75, 210)), Rectangle(o1, o2))

    part0 should be(None)
    part1 should be(None)
    part2 should be(None)
    part3 should be(None)
    part4 should be(None)
    part5 should be(None)
    part6 should be(None)
    part7 should be(None)
  }

  "A 0 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    val i1x = 50
    val i1y = 500
    val i2x = 150
    val i2y = 250

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(o1x, o1y), Point(i2x, i2y))))

  }

  "A 1 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    val i1x = 120
    val i1y = 500
    val i2x = 150
    val i2y = 250

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(i1x, o1y), Point(i2x, i2y))))

  }

  "A 2 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    val i1x = 120
    val i1y = 500
    val i2x = 550
    val i2y = 250

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(i1x, o1y), Point(o2x, i2y))))

  }

  "A 3 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    val i1x = 120
    val i1y = 350
    val i2x = 550
    val i2y = 250

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(i1x, i1y), Point(o2x, i2y))))

  }

  "A 4 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    // x 100 - 300
    // y 200 - 400
    val i1x = 120
    val i1y = 350
    val i2x = 550
    val i2y = 50

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(i1x, i1y), Point(o2x, o2y))))

  }

  "A 5 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    // x 100 - 300
    // y 200 - 400
    val i1x = 120
    val i1y = 350
    val i2x = 240
    val i2y = 50

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(i1x, i1y), Point(i2x, o2y))))

  }

  "A 6 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    // x 100 - 300
    // y 200 - 400
    val i1x = 40
    val i1y = 250
    val i2x = 240
    val i2y = 50

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(o1x, i1y), Point(i2x, o2y))))

  }

  "A 7 rectangle" should "be calculated correctly" in {
    val o1x = 100
    val o1y = 400

    val o2x = 300
    val o2y = 200

    // x 100 - 300
    // y 200 - 400
    val i1x = 40
    val i1y = 250
    val i2x = 240
    val i2y = 210

    val part: Option[Rectangle] = RectangleUtils.getInnerPart(Rectangle(Point(i1x, i1y), Point(i2x, i2y)), Rectangle(Point(o1x, o1y), Point(o2x, o2y)))

    part should be(Some(Rectangle(Point(o1x, i1y), Point(i2x, i2y))))

  }

  "A side" should "be correctly calculated" in {
    val c0 = Point(1, 18)
    val c1 = Point(11, 2)
    val w: Double = 10
    val h: Double = 16

    val rect = Rectangle(c0, w, h)
    val rect1 = Rectangle(c0, c1)

    rect.bottomRight.x should be(11)
    rect.bottomRight.y should be(2)


    rect1.bottomRight.x should be(11)
    rect1.bottomRight.y should be(2)
    rect1.width should equal(w)
    rect1.height should equal(h)
  }


  "Children" should "be generated correctly" in {
    val o1 = Point(100, 400)
    val o2 = Point(300, 200)

    val i1 = Point(150, 250)
    val i2 = Point(175, 225)

    val r1: Rectangle = Rectangle(Point(100, 400), Point(300, 250))
    val r2: Rectangle = Rectangle(Point(100, 225), Point(300, 200))
    val r3: Rectangle = Rectangle(Point(100, 400), Point(150, 200))
    val r4: Rectangle = Rectangle(Point(175, 400), Point(300, 200))

    val result = RectangleUtils.findByPivot(Rectangle(i1, i2), Rectangle(o1, o2))

    result.size should be (4)
    result should contain (r1)
    result should contain (r2)
    result should contain (r3)
    result should contain (r4)
  }

  "Inner" should "be generated correctly" in {
    val o1 = Point(100, 400)
    val o2 = Point(300, 200)
    val outer = Rectangle(o1, o2)

    val r1: Rectangle = Rectangle(Point(100, 400), Point(300, 250))
    val r2: Rectangle = Rectangle(Point(100, 225), Point(300, 200))
    val r3: Rectangle = Rectangle(Point(100, 400), Point(150, 200))
    val r4: Rectangle = Rectangle(Point(175, 400), Point(300, 200))
    val r5: Rectangle = Rectangle(Point(350, 700), Point(450, 200))

    val result = RectangleUtils.findInner(outer, List(r1, r2, r3, r4, r5))

    result.size should be (4)
    result should contain (r1)
    result should contain (r2)
    result should contain (r3)
    result should contain (r4)
    result should not contain (r5)
  }
}
