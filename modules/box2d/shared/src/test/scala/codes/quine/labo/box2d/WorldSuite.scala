package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object WorldSuite extends SimpleTestSuite {
  test("World,apply") {
    val world = World()
    assertEquals(world.gravity, Vec2(0, -10))
    assertEquals(world.iterations, 10)
  }

  test("World#add") {
    val world = World()
    val b1 = Body()
    val b2 = Body()
    val j = Joint(b1, b2, Vec2(0, 0))
    world.add(b1)
    world.add(b2)
    world.add(j)
    assertEquals(world.bodies, Seq(b1, b2))
    assertEquals(world.joints, Seq(j))
  }

  test("World#clear") {
    val world = World()
    val b1 = Body()
    val b2 = Body()
    val j = Joint(b1, b2, Vec2(0, 0))
    world.add(b1)
    world.add(b2)
    world.add(j)
    world.clear()
    assertEquals(world.bodies, Seq.empty)
    assertEquals(world.joints, Seq.empty)
  }

  test("World#step") {
    val world = World()
    val b1 = Body(Vec2(1, 1), 1)
    val b2 = Body(Vec2(1, 1), 1)
    world.add(b1)
    world.add(b2)
    world.step(1000.0f / 60.0f)
    assert(world.arbiters.nonEmpty)
  }
}
