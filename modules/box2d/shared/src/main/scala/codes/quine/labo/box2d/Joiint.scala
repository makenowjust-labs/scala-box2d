package codes.quine.labo.box2d

import MathUtil._

/** Joint is a constraint between a pair of bodies. */
final class Joint private {
  private[this] var _localAnchor1: Vec2 = Vec2(0.0f, 0.0f)
  private[this] var _localAnchor2: Vec2 = Vec2(0.0f, 0.0f)

  def localAnchor1: Vec2 = _localAnchor1
  def localAnchor2: Vec2 = _localAnchor2

  private[box2d] var M: Mat22 = Mat22(0.0f, 0.0f, 0.0f, 0.0f)

  private[box2d] var r1: Vec2 = Vec2(0.0f, 0.0f)
  private[box2d] var r2: Vec2 = Vec2(0.0f, 0.0f)

  private[box2d] var bias: Vec2 = Vec2(0.0f, 0.0f)
  private[box2d] var P: Vec2 = Vec2(0.0f, 0.0f)

  private[this] var _body1: Body = null
  private[this] var _body2: Body = null

  def body1: Body = _body1
  def body2: Body = _body2

  var biasFactor: Float = 0.2f
  var softness: Float = 0.0f

  def set(body1: Body, body2: Body, anchor: Vec2): Unit = {
    _body1 = body1
    _body2 = body2

    val rot1 = Mat22.rotation(body1.rotation)
    val rot2 = Mat22.rotation(body2.rotation)
    val rot1T = rot1.transpose
    val rot2T = rot2.transpose

    _localAnchor1 = rot1T * (anchor - body1.position)
    _localAnchor2 = rot2T * (anchor - body2.position)

    P = Vec2(0.0f, 0.0f)

    biasFactor = 0.2f
    softness = 0.0f
  }

  def preStep(invDt: Float): Unit = {
    val rot1 = Mat22.rotation(body1.rotation)
    val rot2 = Mat22.rotation(body2.rotation)

    r1 = rot1 * localAnchor1
    r2 = rot2 * localAnchor2

    val K1 = Mat22(
      body1.invMass + body2.invMass,
      0.0f,
      0.0f,
      body1.invMass + body2.invMass
    )
    val K2 = Mat22(
      body1.invInertia * r1.y * r1.y,
      -body1.invInertia * r1.x * r1.y,
      -body1.invInertia * r1.x * r1.y,
      body1.invInertia * r1.x * r1.x
    )
    val K3 = Mat22(
      body2.invInertia * r2.y * r2.y,
      -body2.invInertia * r2.x * r2.y,
      -body2.invInertia * r2.x * r2.y,
      body2.invInertia * r2.x * r2.x
    )
    val K = K1 + K2 + K3 + Mat22(softness, 0, 0, softness)
    M = K.invert

    val p1 = body1.position + r1
    val p2 = body2.position + r2
    val dp = p2 - p1

    if (World.positionCorrection) {
      bias = -biasFactor * invDt * dp
    } else {
      bias = Vec2(0.0f, 0.0f)
    }

    if (World.warmStarting) {
      body1.velocity -= body1.invMass * P
      body1.angularVelocity -= body1.invInertia * (r1 cross P)

      body2.velocity += body2.invMass * P
      body2.angularVelocity += body2.invInertia * (r2 cross P)
    } else {
      P = Vec2(0.0f, 0.0f)
    }
  }

  def applyImpluse(): Unit = {
    val dv = body2.velocity + (body2.angularVelocity cross r2) - body1.velocity - (body1.angularVelocity cross r1)
    val impluse = M * (bias - dv - softness * P)

    body1.velocity -= body1.invMass * impluse
    body1.angularVelocity -= body1.invInertia * (r1 cross impluse)

    body2.velocity += body2.invMass * impluse
    body2.angularVelocity += body2.invInertia * (r2 cross impluse)

    P += impluse
  }
}

object Joint {
  /** Constructs a joint with the given parameters. */
  def apply(body1: Body, body2: Body, anchor: Vec2, softness: Float = 0.0f, biasFactor: Float = 0.2f): Joint = {
    val joint = new Joint
    joint.set(body1, body2, anchor)
    joint.softness = softness
    joint.biasFactor = biasFactor
    joint
  }
}
