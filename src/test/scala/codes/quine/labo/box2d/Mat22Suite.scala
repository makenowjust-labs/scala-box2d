package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object Mat22Suite extends SimpleTestSuite {
  test("Mat22#apply") {
    val m1 = Mat22(Vec2(1, 2), Vec2(3, 4))
    val m2 = Mat22(1, 2, 3, 4)
    assert(m1 == m2)
  }

  test("Mat22#unapply") {
    val m = Mat22(1, 2, 3, 4)
    val Mat22(a, b, c, d) = m
    assertEquals(a, 1)
    assertEquals(b, 2)
    assertEquals(c, 3)
    assertEquals(d, 4)
  }

  test("Mat22#transpose") {
    val m = Mat22(1, 2, 3, 4)
    assertEquals(m.transpose, Mat22(1, 3, 2, 4))
  }

  test("Mat22#equals") {
    val m1 = Mat22(1, 2, 3, 4)
    val m2 = Mat22(1, 2, 3, 4)
    val m3 = Mat22(3, 4, 5, 6)
    assert(m1 == m2)
    assert(m1 != m3)
  }

  test("Mat22#toString") {
    val m = Mat22(1, 2, 3, 4)
    assertEquals(m.toString, "Mat22(1.0, 2.0, 3.0, 4.0)")
  }
}
