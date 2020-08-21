package codes.quine.labo.box2d

import minitest.SimpleTestSuite

import MathUtil.FloatOps

object MathUtilSuite extends SimpleTestSuite {
  test("FloatOps#cross") {
    assertEquals(3 cross Vec2(1, 2), Vec2(-6, 3))
  }

  test("FloatOps#*") {
    assertEquals(3 * Vec2(1, 2), Vec2(3, 6))
  }

  test("MathUtil.abs") {
    assertEquals(MathUtil.abs(1), 1)
    assertEquals(MathUtil.abs(-1), 1)
    assertEquals(MathUtil.abs(Vec2(1, -1)), Vec2(1, 1))
    assertEquals(MathUtil.abs(Mat22(1, 2, -3, -4)), Mat22(1, 2, 3, 4))
  }

  test("MathUtil.sqrt") {
    assertEquals(MathUtil.sqrt(MathUtil.PI), Math.sqrt(MathUtil.PI).toFloat)
  }

  test("MathUtil.sign") {
    assertEquals(MathUtil.sign(2), 1)
    assertEquals(MathUtil.sign(0), 1)
    assertEquals(MathUtil.sign(-2), -1)
  }

  test("MathUtil.min") {
    assertEquals(MathUtil.min(1, 2), 1)
    assertEquals(MathUtil.min(4, 3), 3)
  }

  test("MathUtil.max") {
    assertEquals(MathUtil.max(1, 2), 2)
    assertEquals(MathUtil.max(4, 3), 4)
  }

  test("MathUtil.clamp") {
    assertEquals(MathUtil.clamp(4, 3, 5), 4)
    assertEquals(MathUtil.clamp(1, 2, 3), 2)
    assertEquals(MathUtil.clamp(5, 3, 4), 4)
  }

  test("MathUtil.random") {
    for (_ <- 1 until 100) {
      val r1 = MathUtil.random
      val r2 = MathUtil.random(2, 4)
      assert(-1.0 <= r1 && r1 <= 1.0)
      assert(2 <= r2 && r2 <= 4)
    }
  }
}
