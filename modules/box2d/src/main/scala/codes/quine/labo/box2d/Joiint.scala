package codes.quine.labo.box2d

import MathUtil._

final class Joint {
  var M: Mat22 = Mat22(0.0f, 0.0f, 0.0f, 0.0f)

  var localAnchor1: Vec2 = Vec2(0.0f, 0.0f)
  var localAnchor2: Vec2 = Vec2(0.0f, 0.0f)

  var r1: Vec2 = Vec2(0.0f, 0.0f)
  var r2: Vec2 = Vec2(0.0f, 0.0f)

  var bias: Vec2 = Vec2(0.0f, 0.0f)
  val P: Vec2 = Vec2(0.0f, 0.0f)

  var body1: Body = null
  var body2: Body = null

  var biasFactor: Float = 0.2f
  var softness: Float = 0.0f

  def set(body1: Body, body2: Body, anchor: Vec2): Unit = {
    this.body1 = body1
    this.body2 = body2

    val rot1 = Mat22.rotation(body1.rotation)
    val rot2 = Mat22.rotation(body2.rotation)
    val rot1T = rot1.transpose
    val rot2T = rot2.transpose

    localAnchor1 = rot1T * (anchor - body1.position)
    localAnchor2 = rot2T * (anchor - body2.position)

    P.set(0.0f, 0.0f)

    biasFactor = 0.2f
    softness = 0.0f
  }

  def preStep(invDt: Float): Unit = {
    require(body1 != null && body2 != null)

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
      body1.invI * r1.y * r1.y,
      -body1.invI * r1.x * r1.y,
      -body1.invI * r1.x * r1.y,
      body1.invI * r1.x * r1.x
    )
    val K3 = Mat22(
      body2.invI * r2.y * r2.y,
      -body2.invI * r2.x * r2.y,
      -body2.invI * r2.x * r2.y,
      body2.invI * r2.x * r2.x
    )
    val K = K1 + K2 + K3
    K.col1.x += softness
    K.col2.y += softness
    M = K.invert

    val p1 = body1.position + r1
    val p2 = body2.position + r2
    val dp = p2 - p1

    if (World.positionCorrection) {
      bias = -biasFactor * invDt * dp
    } else {
      bias.set(0.0f, 0.0f)
    }

    if (World.warmStarting) {
      body1.velocity -= body1.invMass * P
      body1.angularVelocity -= body1.invI * (r1 cross P)

      body2.velocity += body2.invMass * P
      body2.angularVelocity += body2.invI * (r2 cross P)
    } else {
      P.set(0.0f, 0.0f)
    }
  }

  def applyImpluse(): Unit = {
    val dv = body2.velocity + (body2.angularVelocity cross r2) - body1.velocity - (body1.angularVelocity cross r1)
    val impluse = M * (bias - dv - softness * P)

    body1.velocity -= body1.invMass * impluse
    body1.angularVelocity -= body1.invI * (r1 cross impluse)

    body2.velocity += body2.invMass * impluse
    body2.angularVelocity += body2.invI * (r2 cross impluse)

    P += impluse
  }
}
