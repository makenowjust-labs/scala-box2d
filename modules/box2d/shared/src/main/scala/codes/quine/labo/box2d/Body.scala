package codes.quine.labo.box2d

final class Body private extends Ordered[Body] {
  var position: Vec2 = Vec2(0.0f, 0.0f)
  var rotation: Float = 0.0f

  var velocity: Vec2 = Vec2(0.0f, 0.0f)
  var angularVelocity: Float = 0.0f

  var force: Vec2 = Vec2(0.0f, 0.0f)
  var torque: Float = 0.0f
  var friction: Float = 0.2f

  var _width: Vec2 = Vec2(1.0f, 1.0f)
  var _mass: Float = Float.MaxValue
  var _invMass: Float = 0.0f
  var _I: Float = Float.MaxValue
  var _invI: Float = 0.0f

  def width: Vec2 = _width
  def mass: Float = _mass
  def invMass: Float = _invMass
  def I: Float = _I
  def invI: Float = _invI

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

    _width.set(w.x, w.y)
    _mass = m

    if (mass < Float.MaxValue) {
      _invMass = 1.0f / mass
      _I = mass * (width.x * width.x + width.y * width.y) / 12.0f
      _invI = 1.0f / I
    } else {
      _invMass = 0.0f
      _I = Float.MaxValue
      _invI = 0.0f
    }
  }

  def compare(that: Body): Int = System.identityHashCode(this) compare System.identityHashCode(that)
}

object Body {
  def apply(
      width: Vec2 = Vec2(1.0f, 1.0f),
      mass: Float = Float.MaxValue,
      position: Vec2 = Vec2(0.0f, 0.0f),
      rotation: Float = 0.0f,
      friction: Float = 0.2f
  ): Body = {
    val body = new Body
    body.set(width, mass)
    body.position.set(position.x, position.y)
    body.rotation = rotation
    body.friction = friction
    body
  }
}
