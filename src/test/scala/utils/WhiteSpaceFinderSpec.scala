package utils

import pdf.parser.{Point, Rectangle}

class WhiteSpaceFinderSpec extends UnitSpec {
  "Major white text box" should " be correct for rectangle 1" in {
    test20(func(n = 1, actual = Rectangle(Point(293, 575), Point(304, 62))))
  }

  "Major white text box" should " be correct for rectangle 2" in {
    test20(func(n = 2, actual = Rectangle(Point(293, 764), Point(304, 68))))
  }

  "Major white text box" should " be correct for rectangle 3" in {
    test20(func(n = 3, actual = Rectangle(Point(293, 764), Point(304, 63))))
  }

  "Major white text box" should " be correct for rectangle 4" in {
    test20(func(n = 4, actual = Rectangle(Point(293, 764), Point(304, 65))))
  }

  "Major white text box" should " be correct for rectangle 5" in {
    test20(func(n = 5, actual = Rectangle(Point(293, 764), Point(304, 66))))
  }

  "Major white text box" should " be correct for rectangle 6" in {
    test20(func(n = 6, actual = Rectangle(Point(293, 289), Point(304, 63))))
    test20(func(n = 6, pos = 1, Rectangle(Point(293, 764), Point(304, 648))))
  }
}
