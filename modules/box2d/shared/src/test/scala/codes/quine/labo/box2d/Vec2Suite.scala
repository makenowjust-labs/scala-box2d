package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object Vec2Suite extends SimpleTestSuite {
  test("Vec2.apply") {
    val v = Vec2(1, 2)
    assertEquals(v.x, 1)
    assertEquals(v.y, 2)
  }

  test("Vec2.unapply") {
    val v = Vec2(1, 2)
    val Vec2(x, y) = v
    assertEquals(x, 1)
    assertEquals(y, 2)
  }

  test("Vec2#set") {
    val v = Vec2(1, 2)
    v.set(3, 4)
    assertEquals(v.x, 3)
    assertEquals(v.y, 4)
  }

  test("Vec2#unary_-") {
    val v1 = Vec2(1, 2)
    val v2 = -v1
    assertEquals(v2.x, -1)
    assertEquals(v2.y, -2)
  }

  test("Vec2#+=") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    v1 += v2
    assertEquals(v1.x, 4)
    assertEquals(v1.y, 6)
  }

  test("Vec2#-=") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    v1 -= v2
    assertEquals(v1.x, -2)
    assertEquals(v1.y, -2)
  }

  test("Vec2#*=") {
    val v = Vec2(1, 2)
    v *= 2
    assertEquals(v.x, 2)
    assertEquals(v.y, 4)
  }

  test("Vec2#length") {
    val v = Vec2(1, 2)
    assertEquals(v.length, Math.sqrt(5).toFloat)
  }

  test("Vec2#dot") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    assertEquals(v1 dot v2, 11)
  }

  test("Vec2#cross") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    assertEquals(v1 cross v2, -2)
  }

  test("Vec2#+") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    assertEquals(v1 + v2, Vec2(4, 6))
  }

  test("Vec2#-") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(3, 4)
    assertEquals(v1 - v2, Vec2(-2, -2))
  }

  test("Vec2#equals") {
    val v1 = Vec2(1, 2)
    val v2 = Vec2(1, 2)
    val v3 = Vec2(3, 4)
    assert(v1 == v2)
    assert(v1 != 1)
    assert(v1 != v3)
  }

  test("Vec2#toString") {
    val v = Vec2(1, 2)
    assertEquals(v.toString, "Vec2(1.0, 2.0)")
  }
}
