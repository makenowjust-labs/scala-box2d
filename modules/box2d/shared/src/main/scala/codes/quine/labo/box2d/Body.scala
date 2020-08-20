package codes.quine.labo.box2d

final class Body extends Ordered[Body] {
  val position: Vec2 = Vec2(0.0f, 0.0f)
  var rotation: Float = 0.0f

  val velocity: Vec2 = Vec2(0.0f, 0.0f)
  var angularVelocity: Float = 0.0f

  val force: Vec2 = Vec2(0.0f, 0.0f)
  var torque: Float = 0.0f
  var friction: Float = 0.2f

  var width: Vec2 = Vec2(1.0f, 1.0f)
  var mass: Float = Float.MaxValue
  var invMass: Float = 0.0f
  var I: Float = Float.MaxValue
  var invI: Float = 0.0f

  def addForce(f: Vec2): Unit = {
    force += f
  }

  def set(w: Vec2, m: Float): Unit = {
    position.set(0.0f, 0.0f)
    rotation = 0.0f
    velocity.set(0.0f, 0.0f)
    angularVelocity = 0.0f
    force.set(0.0f, 0.0f)
    torque = 0.0f
    friction = 0.2f

    width = w
    mass = m

    if (mass < Float.MaxValue) {
      invMass = 1.0f / mass
      I = mass * (width.x * width.x + width.y * width.y) / 12.0f
      invI = 1.0f / I
    } else {
      invMass = 0.0f
      I = Float.MaxValue
      invI = 0.0f
    }
  }

  def compare(that: Body): Int = System.identityHashCode(this) compare System.identityHashCode(that)
}
