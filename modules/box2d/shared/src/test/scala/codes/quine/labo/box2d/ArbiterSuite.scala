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

  test("ArbiterKey.apply") {
    val b1 = Body()
    val b2 = Body()
    val key1 = ArbiterKey(b1, b2)
    val key2 = ArbiterKey(b2, b1)
    assert(key1 == key2)
  }

  test("Arbiter.apply") {
    val b1 = Body()
    val b2 = Body()
    val arb1 = Arbiter(b1, b2)
    val arb2 = Arbiter(b2, b1)
    assert(arb1.body1 == arb2.body1)
    assert(arb1.body2 == arb2.body2)
    assert(arb1.contacts == arb2.contacts)
  }

  test("Arbiter#update") {
    val b1 = Body()
    val b2 = Body()
    val arb = Arbiter(b1, b2)
    val contacts = Collide.detect(arb.body1, arb.body2)
    arb.update(contacts)
    assert(arb.contacts == contacts)
    arb.update(IndexedSeq.empty)
    assert(arb.contacts.isEmpty)
    arb.update(contacts)
    assert(arb.contacts == contacts)
  }

  test("Arbiter#preStep") {
    val b1 = Body()
    val b2 = Body()
    val arb = Arbiter(b1, b2)
    val dt = 60.0f
    val invDt = 1.0f / dt
    arb.preStep(invDt)
    assert(arb.contacts.nonEmpty && arb.contacts.forall(_.bias != 0.0f))
  }


  test("Arbiter#applyImpluse") {
    val b1 = Body(Vec2(1, 1), 10)
    val b2 = Body(Vec2(1, 1), 10)
    val arb = Arbiter(b1, b2)
    val dt = 60.0f
    val invDt = 1.0f / dt
    arb.preStep(invDt)
    arb.applyImpluse()
    assert(b1.velocity.length > 0)
    assert(b2.velocity.length > 0)
  }
}
