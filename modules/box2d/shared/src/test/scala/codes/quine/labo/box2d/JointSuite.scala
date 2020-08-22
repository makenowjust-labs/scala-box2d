package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object JointSuite extends SimpleTestSuite {
  test("Joint.apply") {
    val b1 = Body()
    val b2 = Body()
    val joint = Joint(b1, b2, Vec2(0.0f, 1.0f))
    assertEquals(joint.body1, b1)
    assertEquals(joint.body2, b2)
  }

  test("Joint#preStep") {
    val b1 = Body(Vec2(1, 1), 10)
    val b2 = Body(Vec2(1, 1), 10)
    val joint = Joint(b1, b2, Vec2(0.0f, 1.0f))
    b2.position = Vec2(0.5f, 0.5f)
    val invDt = 60.0f
    joint.preStep(invDt)
    assert(joint.bias.length > 0)
    try {
      World.positionCorrection = false
      joint.preStep(invDt)
      assert(joint.bias.length == 0)
    } finally {
      World.positionCorrection = true
    }
  }

  test("Joint#applyImpluse") {
    val b1 = Body(Vec2(1, 1), 10)
    val b2 = Body(Vec2(1, 1), 10)
    val joint = Joint(b1, b2, Vec2(0.0f, 1.0f))
    b2.position = Vec2(0.5f, 0.5f)
    val invDt = 60.0f
    joint.preStep(invDt)
    joint.applyImpluse()
    assert(b1.velocity.length > 0)
    assert(b2.velocity.length > 0)
  }
}
