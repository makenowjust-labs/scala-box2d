package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object Mat22Suite extends SimpleTestSuite {
  test("Mat22.apply") {
    val m1 = Mat22(Vec2(1, 2), Vec2(3, 4))
    val m2 = Mat22(1, 3, 2, 4)
    assert(m1 == m2)
  }

  test("Mat22.rotation") {
    val m = Mat22.rotation(0)
    assertEquals(m, Mat22(1, 0, 0, 1))
  }

  test("Mat22#transpose") {
    val m = Mat22(1, 2, 3, 4)
    assertEquals(m.transpose, Mat22(1, 3, 2, 4))
  }

  test("Mat22#invert") {
    val m = Mat22(1, 2, 3, 4)
    assertEquals(m.invert * m, Mat22(1, 0, 0, 1))
    assertEquals(m * m.invert, Mat22(1, 0, 0, 1))
  }

  test("Mat22#*") {
    val m1 = Mat22(2, 0, 0, 3)
    val m2 = Mat22(1, 4, 2, 3)
    val v = Vec2(1, 2)
    assertEquals(m1 * v, Vec2(2, 6))
    assertEquals(m1 * m2, Mat22(2, 8, 6, 9))
  }

  test("Mat22#+") {
    val m1 = Mat22(1, 2, 3, 4)
    val m2 = Mat22(4, 3, 2, 1)
    assertEquals(m1 + m2, Mat22(5, 5, 5, 5))
  }

  test("Mat22#equals") {
    val m1 = Mat22(1, 2, 3, 4)
    val m2 = Mat22(1, 2, 3, 4)
    val m3 = Mat22(3, 4, 5, 6)
    assert(m1 == m2)
    assert(m1 != m3)
  }

  test("Mat22#toString") {
    val m = Mat22(0.5f, 1.5f, 2.5f, 3.5f)
    assertEquals(m.toString, "Mat22(0.5, 1.5, 2.5, 3.5)")
  }
}
