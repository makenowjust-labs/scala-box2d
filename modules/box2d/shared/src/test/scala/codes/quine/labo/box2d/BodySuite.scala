package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object BodySuite extends SimpleTestSuite {
  def assertInitialBody(body: Body): Unit = {
    assertEquals(body.position, Vec2(0, 0))
    assertEquals(body.rotation, 0)
    assertEquals(body.velocity, Vec2(0, 0))
    assertEquals(body.angularVelocity, 0)
    assertEquals(body.force, Vec2(0, 0))
    assertEquals(body.torque, 0)
    assertEquals(body.friction, 0.2f)
  }

  test("Body.apply") {
    val body = Body()
    assertInitialBody(body)
    assertEquals(body.width, Vec2(1, 1))
    assertEquals(body.mass, Float.MaxValue)
    assertEquals(body.invMass, 0)
    assertEquals(body.inertia, Float.MaxValue)
    assertEquals(body.invInertia, 0)
  }

  test("Body#addForce") {
    val body = Body()
    body.addForce(Vec2(1, 2))
    assertEquals(body.force, Vec2(1, 2))
    body.addForce(Vec2(3, 4))
    assertEquals(body.force, Vec2(4, 6))
  }

  test("Body#set") {
    val body = Body()
    body.position = Vec2(0, 1)
    body.rotation = 3
    body.velocity = Vec2(4, 5)
    body.angularVelocity = 6
    body.force = Vec2(7, 8)
    body.torque = 9
    body.friction = 10

    body.set(Vec2(1, 1), Float.MaxValue)
    assertInitialBody(body)
    assertEquals(body.width, Vec2(1, 1))
    assertEquals(body.mass, Float.MaxValue)
    assertEquals(body.invMass, 0)
    assertEquals(body.inertia, Float.MaxValue)
    assertEquals(body.invInertia, 0)

    body.set(Vec2(1, 2), 3)
    assertEquals(body.width, Vec2(1, 2))
    assertEquals(body.mass, 3)
    assertEquals(body.invMass, 1 / 3.0f)
    assertEquals(body.inertia, 3 * 5 / 12.0f)
    assertEquals(body.invInertia, 12.0f / (3 * 5))
  }

  test("Body#compareTo") {
    val body1 = Body()
    val body2 = Body()
    val hash1 = System.identityHashCode(body1)
    val hash2 = System.identityHashCode(body2)
    assertEquals(body1.compareTo(body2), hash1.compareTo(hash2))
    assertEquals(body1 < body2, hash1 < hash2)
    assertEquals(body1 > body2, hash1 > hash2)
    assertEquals(body1 <= body2, hash1 <= hash2)
    assertEquals(body1 >= body2, hash1 >= hash2)
  }
}
