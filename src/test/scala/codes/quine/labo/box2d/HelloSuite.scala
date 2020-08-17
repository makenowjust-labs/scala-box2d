package codes.quine.labo.box2d

import minitest.SimpleTestSuite

object HelloSuite extends SimpleTestSuite {
  test("world") {
    assertEquals(Hello.world, "Hello World")
  }
}
