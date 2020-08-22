package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object CollideSuite extends SimpleTestSuite {
  test("Collide.detect: not contacted") {
    val body1 = Body(Vec2(1, 1), 1, Vec2(-1, 0))
    val body2 = Body(Vec2(1, 1), 1, Vec2(1, 0))
    val contacts = Collide.detect(body1, body2)
    assert(contacts.isEmpty)
  }

  test("Collide.detect: touched") {
    val body1 = Body(Vec2(1, 1), 1, Vec2(-0.5f, 0))
    val body2 = Body(Vec2(1, 1), 1, Vec2(0.5f, 0))
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
    assert(contacts.forall(_.separation == 0))
  }

  test("Collide.detect: contacted (1)") {
    val body1 = Body(Vec2(1, 1), 1, Vec2(-0.25f, 0))
    val body2 = Body(Vec2(1, 1), 1, Vec2(0.25f, 0))
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
    assert(contacts.forall(_.separation == -0.5f))
  }

  test("Collide.detect: contacted (2)") {
    val body1 = Body(Vec2(1, 1), 1, Vec2(0, 0.25f))
    val body2 = Body(Vec2(1, 1), 1, Vec2(0, -0.25f))
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
    assert(contacts.forall(_.separation == -0.5f))
  }

  test("Collide.detect: contacted (3)") {
    val body1 = Body(Vec2(1, 1), 1, Vec2(0, 0))
    val body2 = Body(Vec2(1, 1), 1, Vec2(0.75f, 0.75f), 0.25f)
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
  }

  test("Collide.detect: contacted (4)") {
    val body1 = Body(Vec2(0.2f, 2.0f), 1, Vec2(1.9325352f, 10.469101f), -1.4443482f)
    val body2 = Body(Vec2(0.2f, 2.0f), 1, Vec2(0.8693587f, 10.524727f), -1.3857963f)
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
  }

  test("Collide.detect: contacted (5)") {
    val body1 = Body(Vec2(0.5f, 0.5f), 1, Vec2(4.6065474f, 8.028836f), 0.24121001f)
    val body2 = Body(Vec2(13.0f, 0.25f), 1, Vec2(2.0f, 7.0f), 0.25f)
    val contacts = Collide.detect(body1, body2)
    assert(contacts.nonEmpty)
  }
}
