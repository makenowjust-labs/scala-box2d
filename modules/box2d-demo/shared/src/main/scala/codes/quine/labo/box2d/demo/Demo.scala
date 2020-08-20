package codes.quine.labo.box2d
package demo

object Demo {
  val timeStep: Float = 1.0f / 60.0f
  val iterations = 10
  val gravity: Vec2 = Vec2(0.0f, -10.0f)

  val world = new World(gravity, iterations)

  // Single box
  def demo1(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.position.set(0.0f, -0.5f * floor.width.y)
    world.add(floor)

    val box = new Body
    box.set(Vec2(1.0f, 1.0f), 200.0f)
    box.position.set(0.0f, 4.0f)
    world.add(box)
  }

  // A simple pendulum
  def demo2(): Unit = {
    val b1 = new Body
    b1.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    b1.friction = 0.2f
    b1.position.set(0.0f, -0.5f * b1.width.y)
    b1.rotation = 0.0f
    world.add(b1)

    val b2 = new Body
    b2.set(Vec2(1.0f, 1.0f), 100.0f)
    b2.friction = 0.2f
    b2.position.set(9.0f, 11.0f)
    b2.rotation = 0.0f
    world.add(b2)

    val j = new Joint
    j.set(b1, b2, Vec2(0.0f, 11.0f))
    world.add(j)
  }

  // Varying friction coefficients
  def demo3(): Unit = {
    val b1 = new Body
    b1.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    b1.position.set(0.0f, -0.5f * b1.width.y)
    world.add(b1)

    val b2 = new Body
    b2.set(Vec2(13.0f, 0.25f), Float.MaxValue)
    b2.position.set(-2.0f, 11.0f)
    b2.rotation = -0.25f
    world.add(b2)

    val b3 = new Body
    b3.set(Vec2(0.25f, 1.0f), Float.MaxValue)
    b3.position.set(5.25f, 9.5f)
    world.add(b3)

    val b4 = new Body
    b4.set(Vec2(13.0f, 0.25f), Float.MaxValue)
    b4.position.set(2.0f, 7.0f)
    b4.rotation = 0.25f
    world.add(b4)

    val b5 = new Body
    b5.set(Vec2(0.25f, 1.0f), Float.MaxValue)
    b5.position.set(-5.25f, 5.5f)
    world.add(b5)

    val b6 = new Body
    b6.set(Vec2(13.0f, 0.25f), Float.MaxValue)
    b6.position.set(-2.0f, 3.0f)
    b6.rotation = -0.25f
    world.add(b6)

    val frictions = Seq(0.75f, 0.5f, 0.35f, 0.1f, 0.0f)
    for ((friction, i) <- frictions.zipWithIndex) {
      val b = new Body
      b.set(Vec2(0.5f, 0.5f), 25.0f)
      b.friction = friction
      b.position.set(-7.5f + 2.0f * i, 14.0f)
      world.add(b)
    }
  }

  // A vertical stack
  def demo4(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.friction = 0.2f
    floor.position.set(0.0f, -0.5f * floor.width.y)
    floor.rotation = 0.0f
    world.add(floor)

    for (i <- 0 until 10) {
      val b = new Body
      b.set(Vec2(1.0f, 1.0f), 1.0f)
      b.friction = 0.2f
      val x = MathUtil.random(-0.1f, 0.1f)
      b.position.set(x, 0.51f + 1.05f * i)
      world.add(b)
    }
  }

  // A pyramid
  def demo5(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.friction = 0.2f
    floor.position.set(0.0f, -0.5f * floor.width.y)
    floor.rotation = 0.0f
    world.add(floor)

    val x = Vec2(-6.0f, 0.75f)

    for (i <- 0 until 12) {
      val y = Vec2(x.x, x.y)
      for (_ <- i until 12) {
        val b = new Body
        b.set(Vec2(1.0f, 1.0f), 1.0f)
        b.friction = 0.2f
        b.position.set(y.x, y.y)
        world.add(b)
        y += Vec2(1.125f, 0.0f)
      }
      x += Vec2(0.5625f, 2.0f)
    }
  }

  // A teeter
  def demo6(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.position.set(0.0f, -0.5f * floor.width.y)
    world.add(floor)

    val b1 = new Body
    b1.set(Vec2(12.0f, 0.25f), 100.0f)
    b1.position.set(0.0f, 1.0f)
    world.add(b1)

    val b2 = new Body
    b2.set(Vec2(0.5f, 0.5f), 25.0f)
    b2.position.set(-5.0f, 2.0f)
    world.add(b2)

    val b3 = new Body
    b3.set(Vec2(0.5f, 0.5f), 25.0f)
    b3.position.set(-5.5f, 2.0f)
    world.add(b3)

    val b4 = new Body
    b4.set(Vec2(1.0f, 1.0f), 100.0f)
    b4.position.set(5.5f, 15.0f)
    world.add(b4)

    val j = new Joint
    j.set(floor, b1, Vec2(0.0f, 1.0f))
    world.add(j)
  }

  // A suspension bridge
  def demo7(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.friction = 0.2f
    floor.position.set(0.0f, -0.5f * floor.width.y)
    floor.rotation = 0.0f
    world.add(floor)

    val mass = 50.0f
    val planks = for {
      i <- 0 until 15
    } yield {
      val b = new Body
      b.set(Vec2(1.0f, 0.25f), mass)
      b.friction = 0.2f
      b.position.set(-8.5f + 1.25f * i, 5.0f)
      b
    }
    for (b <- planks) world.add(b)

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
      val j = new Joint
      j.set(b1, b2, Vec2(-9.125f + 1.25f * i, 5.0f))
      j.softness = softness
      j.biasFactor = biasFactor
      world.add(j)
    }
  }

  // Dominos
  def demo8(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.position.set(0.0f, -0.5f * floor.width.y)
    world.add(floor)

    val bar1 = new Body
    bar1.set(Vec2(12.0f, 0.5f), Float.MaxValue)
    bar1.position.set(-1.5f, 10.0f)
    world.add(bar1)

    for (i <- 0 until 10) {
      val domino = new Body
      domino.set(Vec2(0.2f, 2.0f), 10.0f)
      domino.position.set(-6.0f + 1.0f * i, 11.125f)
      domino.friction = 0.1f
      world.add(domino)
    }

    val bar2 = new Body
    bar2.set(Vec2(14.0f, 0.5f), Float.MaxValue)
    bar2.position.set(1.0f, 6.0f)
    bar2.rotation = 0.3f
    world.add(bar2)

    val bar3 = new Body
    bar3.set(Vec2(0.5f, 3.0f), Float.MaxValue)
    bar3.position.set(-7.0f, 4.0f)
    world.add(bar3)

    val bar4 = new Body
    bar4.set(Vec2(12.0f, 0.25f), 20.0f)
    bar4.position.set(-0.9f, 1.0f)
    world.add(bar4)

    val j1 = new Joint
    j1.set(floor, bar4, Vec2(-2.0f, 1.0f))
    world.add(j1)

    val box1 = new Body
    box1.set(Vec2(0.5f, 0.5f), 10.0f)
    box1.position.set(-10.0f, 15.0f)
    world.add(box1)

    val j2 = new Joint
    j2.set(bar3, box1, Vec2(-7.0f, 15.0f))
    world.add(j2)

    val box2 = new Body
    box2.set(Vec2(2.0f, 2.0f), 20.0f)
    box2.position.set(6.0f, 2.5f)
    box2.friction = 0.1f
    world.add(box2)

    val j3 = new Joint
    j3.set(floor, box2, Vec2(6.0f, 2.6f))
    world.add(j3)

    val domino = new Body
    domino.set(Vec2(2.0f, 0.2f), 10.0f)
    domino.position.set(6.0f, 3.6f)
    world.add(domino)

    val j4 = new Joint
    j4.set(box2, domino, Vec2(7.0f, 3.5f))
    world.add(j4)
  }

  // A multi-pendulum
  def demo9(): Unit = {
    val floor = new Body
    floor.set(Vec2(100.0f, 20.0f), Float.MaxValue)
    floor.friction = 0.2f
    floor.position.set(0.0f, -0.5f * floor.width.y)
    floor.rotation = 0.0f
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

    val y = 12.0f

    val bs = for (i <- 0 until 15) yield {
      val b = new Body
      b.set(Vec2(0.75f, 0.25f), mass)
      b.friction = 0.2f
      b.position.set(0.5f + i, y)
      b.rotation = 0.0f
      b
    }
    for (b <- bs) world.add(b)

    for ((Seq(b1, b2), i) <- (floor +: bs).sliding(2).zipWithIndex) {
      val j = new Joint
      j.set(b1, b2, Vec2(i.toFloat, y))
      j.softness = softness
      j.biasFactor = biasFactor
      world.add(j)
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
