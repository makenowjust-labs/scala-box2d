package codes.quine.labo.box2d
package demo

object Demo {
  val timeStep: Float = 1.0f / 60.0f
  val iterations = 10
  val gravity: Vec2 = Vec2(0.0f, -10.0f)

  val world = new World(gravity, iterations)

  // Single box
  def demo1(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val box = Body(Vec2(1.0f, 1.0f), 200.0f, Vec2(0.0f, 4.0f))
    world.add(box)
  }

  // A simple pendulum
  def demo2(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val weight = Body(Vec2(1.0f, 1.0f), 100.0f, Vec2(9.0f, 11.0f))
    world.add(weight)

    val joint = Joint(floor, weight, Vec2(0.0f, 11.0f))
    world.add(joint)
  }

  // Varying friction coefficients
  def demo3(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val bar1 = Body(Vec2(13.0f, 0.25f), Float.MaxValue, Vec2(-2.0f, 11.0f), -0.25f)
    world.add(bar1)

    val wall1 = Body(Vec2(0.25f, 1.0f), Float.MaxValue, Vec2(5.25f, 9.5f))
    world.add(wall1)

    val bar2 = Body(Vec2(13.0f, 0.25f), Float.MaxValue, Vec2(2.0f, 7.0f), 0.25f)
    world.add(bar2)

    val wall2 = Body(Vec2(0.25f, 1.0f), Float.MaxValue, Vec2(-5.25f, 5.5f))
    world.add(wall2)

    val bar3 = Body(Vec2(13.0f, 0.25f), Float.MaxValue, Vec2(-2.0f, 3.0f), -0.25f)
    world.add(bar3)

    val frictions = Seq(0.75f, 0.5f, 0.35f, 0.1f, 0.0f)
    for ((friction, i) <- frictions.zipWithIndex) {
      val box = Body(Vec2(0.5f, 0.5f), 25.0f, Vec2(-7.5f + 2.0f * i, 14.0f), 0.0f, friction)
      world.add(box)
    }
  }

  // A vertical stack
  def demo4(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    for (i <- 0 until 10) {
      val x = MathUtil.random(-0.1f, 0.1f)
      val b = Body(Vec2(1.0f, 1.0f), 1.0f, Vec2(x, 0.51f + 1.05f * i))
      world.add(b)
    }
  }

  // A pyramid
  def demo5(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val x = Vec2(-6.0f, 0.75f)

    for (i <- 0 until 12) {
      val y = Vec2(x.x, x.y)
      for (_ <- i until 12) {
        val box = Body(Vec2(1.0f, 1.0f), 1.0f, y)
        world.add(box)
        y += Vec2(1.125f, 0.0f)
      }
      x += Vec2(0.5625f, 2.0f)
    }
  }

  // A teeter
  def demo6(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val bar = Body(Vec2(12.0f, 0.25f), 100.0f, Vec2(0.0f, 1.0f))
    world.add(bar)

    val box1 = Body(Vec2(0.5f, 0.5f), 25.0f, Vec2(-5.0f, 2.0f))
    world.add(box1)

    val box2 = Body(Vec2(0.5f, 0.5f), 25.0f, Vec2(-5.5f, 2.0f))
    world.add(box2)

    val box3 = Body(Vec2(1.0f, 1.0f), 100.0f, Vec2(5.5f, 15.0f))
    world.add(box3)

    val joint = Joint(floor, bar, Vec2(0.0f, 1.0f))
    world.add(joint)
  }

  // A suspension bridge
  def demo7(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val mass = 50.0f
    val planks = (0 until 15).map(i => Body(Vec2(1.0f, 0.25f), mass, Vec2(-8.5f + 1.25f * i, 5.0f)))
    for (plank <- planks) world.add(plank)

    // Tuning
    val frequencyHz = 2.0f
    val dampingRatio = 0.7f

    // frequency in radians
    val omega = 2.0f * MathUtil.PI * frequencyHz

    // damping coefficient
    val d = 2.0f * mass * dampingRatio * omega

    // spring stifness
    val k = mass * omega * omega

    // magic formulas
    val softness = 1.0f / (d + timeStep * k)
    val biasFactor = timeStep * k / (d + timeStep * k)

    for ((Seq(b1, b2), i) <- (Seq(floor) ++ planks ++ Seq(floor)).sliding(2).zipWithIndex) {
      val joint = Joint(b1, b2, Vec2(-9.125f + 1.25f * i, 5.0f), softness, biasFactor)
      world.add(joint)
    }
  }

  // Dominos
  def demo8(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val bar1 = Body(Vec2(12.0f, 0.5f), Float.MaxValue, Vec2(-1.5f, 10.0f))
    world.add(bar1)

    for (i <- 0 until 10) {
      val domino = Body(Vec2(0.2f, 2.0f), 10.0f, Vec2(-6.0f + 1.0f * i, 11.125f), 0.0f, 0.1f)
      world.add(domino)
    }

    val bar2 = Body(Vec2(14.0f, 0.5f), Float.MaxValue, Vec2(1.0f, 6.0f), 0.3f)
    world.add(bar2)

    val bar3 = Body(Vec2(0.5f, 3.0f), Float.MaxValue, Vec2(-7.0f, 4.0f))
    world.add(bar3)

    val bar4 = Body(Vec2(12.0f, 0.25f), 20.0f, Vec2(-0.9f, 1.0f))
    world.add(bar4)

    val joint1 = Joint(floor, bar4, Vec2(-2.0f, 1.0f))
    world.add(joint1)

    val box1 = Body(Vec2(0.5f, 0.5f), 10.0f, Vec2(-10.0f, 15.0f))
    world.add(box1)

    val joint2 = Joint(bar3, box1, Vec2(-7.0f, 15.0f))
    world.add(joint2)

    val box2 = Body(Vec2(2.0f, 2.0f), 20.0f, Vec2(6.0f, 2.5f), 0.0f, 0.1f)
    world.add(box2)

    val joint3 = Joint(floor, box2, Vec2(6.0f, 2.6f))
    world.add(joint3)

    val domino = Body(Vec2(2.0f, 0.2f), 10.0f, Vec2(6.0f, 3.6f))
    world.add(domino)

    val joint4 = Joint(box2, domino, Vec2(7.0f, 3.5f))
    world.add(joint4)
  }

  // A multi-pendulum
  def demo9(): Unit = {
    val floor = Body(Vec2(100.0f, 20.0f), Float.MaxValue, Vec2(0.0f, -10.0f))
    world.add(floor)

    val mass = 10.0f

    // Tuning
    val frequencyHz = 4.0f
    val dampingRatio = 0.7f

    // frequency in radians
    val omega = 2.0f * MathUtil.PI * frequencyHz

    // damping coefficient
    val d = 2.0f * mass * dampingRatio * omega

    // spring stifness
    val k = mass * omega * omega

    // magic formulas
    val softness = 1.0f / (d + timeStep * k)
    val biasFactor = timeStep * k / (d + timeStep * k)

    val weights = (0 until 15).map(i => Body(Vec2(0.75f, 0.25f), mass, Vec2(0.5f + i, 12.0f)))
    for (weight <- weights) world.add(weight)

    for ((Seq(b1, b2), i) <- (floor +: weights).sliding(2).zipWithIndex) {
      val joint = Joint(b1, b2, Vec2(i.toFloat, 12.0f), softness, biasFactor)
      world.add(joint)
    }
  }

  val demos: Map[String, () => Unit] = Map(
    "1. Single box" -> demo1 _,
    "2. A simple pendulum" -> demo2 _,
    "3. Varying friction coefficients" -> demo3 _,
    "4. A vertical stack" -> demo4 _,
    "5. A pyramid" -> demo5 _,
    "6. A teeter" -> demo6 _,
    "7. A suspension bridge" -> demo7 _,
    "8. Dominos" -> demo8 _,
    "9. A multi-pendulum" -> demo9 _
  )

  val demoNames: Seq[String] = demos.keys.toSeq.sorted
}
