package codes.quine.labo.box2d

import minitest.SimpleTestSuite

import Arbiter._

object ArbiterSuite extends SimpleTestSuite {
  test("EdgeNumber.apply") {
    assertEquals(EdgeNumber(0), NO_EDGE)
    assertEquals(EdgeNumber(1), EDGE1)
    assertEquals(EdgeNumber(2), EDGE2)
    assertEquals(EdgeNumber(3), EDGE3)
    assertEquals(EdgeNumber(4), EDGE4)
  }

  test("FeaturePair.apply") {
    val fp1 = FeaturePair(EDGE1, EDGE2, EDGE3, EDGE4)
    val fp2 = FeaturePair(0x01020304)
    assert(fp1 == fp2)
    assertEquals(fp1.inEdge1, EDGE1)
    assertEquals(fp1.outEdge1, EDGE2)
    assertEquals(fp1.inEdge2, EDGE3)
    assertEquals(fp1.outEdge2, EDGE4)
  }

  test("FeaturePair.flip") {
    val fp = FeaturePair(EDGE1, EDGE2, EDGE3, EDGE4)
    assertEquals(FeaturePair.flip(fp), FeaturePair(EDGE3, EDGE4, EDGE1, EDGE2))
  }

  test("FeaturePair#toString") {
    val fp = FeaturePair(EDGE1, EDGE2, EDGE3, EDGE4)
    assertEquals(fp.toString, "FeaturePair(0x01020304)")
  }
}
