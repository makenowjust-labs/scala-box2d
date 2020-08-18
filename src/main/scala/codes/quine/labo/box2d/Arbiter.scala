package codes.quine.labo.box2d

import scala.annotation.switch
import scala.collection.mutable

import Arbiter._
import MathUtil.FloatOps

final class Arbiter(val body1: Body, val body2: Body, var contacts: IndexedSeq[Contact]) {
  var friction: Float = MathUtil.sqrt(body1.friction * body2.friction)

  def update(newContacts: IndexedSeq[Contact]): Unit = {
    val mergedContacts = mutable.Buffer.empty[Contact]

    for (cNew <- newContacts) {
      contacts.find(cOld => cOld.feature.value == cNew.feature.value) match {
        case Some(cOld) =>
          val c = cNew.copy()
          if (World.warmStarting) {
            c.Pn = cOld.Pn
            c.Pt = cOld.Pt
            c.Pnb = cOld.Pnb
          } else {
            c.Pn = 0.0f
            c.Pt = 0.0f
            c.Pnb = 0.0f
          }
          mergedContacts.append(c)
        case None =>
          mergedContacts.append(cNew)
      }
    }

    contacts = mergedContacts.toIndexedSeq
  }

  def preStep(invDt: Float): Unit = {
    val ALLOWED_PENETRATION = 0.01f
    val BIAS_FACTOR = if (World.positionCorrection) 0.2f else 0.0f

    for (c <- contacts) {
      val r1 = c.position - body1.position
      val r2 = c.position - body2.position

      // Precompute normal mass, tangent mass, and bias.

      val rn1 = r1 dot c.normal
      val rn2 = r2 dot c.normal
      val kNormal =
        body1.invMass + body2.invMass + body1.invI * ((r1 dot r1) - rn1 * rn1) + body2.invI * ((r2 dot r2) - rn2 * rn2)
      c.massNormal = 1.0f / kNormal

      val tangent = c.normal cross 1.0f
      val rt1 = r1 dot tangent
      val rt2 = r2 dot tangent
      val kTangent =
        body1.invMass + body2.invMass + body1.invI * ((r1 dot r1) - rt1 * rt1) + body2.invI * ((r2 dot r2) - rt2 * rt2)
      c.massTangent = 1.0f / kTangent

      c.bias = -BIAS_FACTOR * invDt * MathUtil.min(0.0f, c.separation + ALLOWED_PENETRATION)

      if (World.accumulateImpulses) {
        // Apply normal + friction impulse
        val P = c.Pn * c.normal + c.Pt * tangent

        body1.velocity -= body1.invMass * P
        body1.angularVelocity -= body1.invMass * (r1 cross P)

        body2.velocity += body2.invMass * P
        body2.angularVelocity += body2.invMass * (r1 cross P)
      }
    }
  }

  def applyImpluse(): Unit = {
    val b1 = body1
    val b2 = body2

    for (c <- contacts) {
      c.r1 = c.position - b1.position
      c.r2 = c.position - b2.position

      // Relative velocity at contact
      var dv = b2.velocity + (b2.angularVelocity cross c.r2) - b1.velocity - (b1.angularVelocity cross c.r1)

      // Compute normal impulse
      val vn = dv cross c.normal

      var dPn = c.massNormal * (-vn + c.bias)

      if (World.accumulateImpulses) {
        // Clamp the accumulated impulse
        val Pn0 = c.Pn
        c.Pn = MathUtil.max(Pn0 + dPn, 0.0f)
        dPn = c.Pn - Pn0
      } else {
        dPn = MathUtil.max(dPn, 0.0f)
      }

      // Apply contact impulse
      val Pn = dPn * c.normal

      b1.velocity -= b1.invMass * Pn
      b1.angularVelocity -= b1.invI * (c.r1 cross Pn)

      b2.velocity += b2.invMass * Pn
      b2.angularVelocity += b2.invI * (c.r2 cross Pn)

      // Relative velocity at contact
      dv = b2.velocity + (b2.angularVelocity cross c.r2) - b1.velocity - (b1.angularVelocity cross c.r1)

      val tangent = c.normal cross 1.0f
      val vt = dv dot tangent
      var dPt = c.massTangent * -vt

      if (World.accumulateImpulses) {
        // Compute friction impulse
        val maxPt = friction * c.Pn

        val oldTangentImpluse = c.Pt
        c.Pt = MathUtil.clamp(oldTangentImpluse + dPt, -maxPt, maxPt)
        dPt = c.Pt - oldTangentImpluse
      } else {
        val maxPt = friction * dPn
        dPt = MathUtil.clamp(dPt, -maxPt, maxPt)
      }

      // Apply contact impulse
      val Pt = dPt * tangent

      b1.velocity -= b1.invMass * Pt
      b1.angularVelocity -= b1.invI * (c.r1 cross Pt)

      b2.velocity += b2.invMass * Pt
      b2.angularVelocity += b2.invI * (c.r2 cross Pt)
    }
  }
}

object Arbiter {
  def apply(b1: Body, b2: Body): Arbiter = {
    val (body1, body2) = if (b1 < b2) (b1, b2) else (b2, b1)
    val contacts = Collide.collide(body1, body2)
    new Arbiter(body1, body2, contacts)
  }

  sealed abstract class EdgeNumber(val num: Byte)

  object EdgeNumber {
    def apply(num: Byte): EdgeNumber =
      (num: @switch) match {
        case 0 => NO_EDGE
        case 1 => EDGE1
        case 2 => EDGE2
        case 3 => EDGE3
        case 4 => EDGE4
        case _ => throw new IllegalArgumentException("unknown edge numbers")
      }
  }

  case object NO_EDGE extends EdgeNumber(0)
  case object EDGE1 extends EdgeNumber(1)
  case object EDGE2 extends EdgeNumber(2)
  case object EDGE3 extends EdgeNumber(3)
  case object EDGE4 extends EdgeNumber(4)

  final class FeaturePair(val value: Int) extends AnyVal {
    def inEdge1: EdgeNumber = EdgeNumber(((value >> 6) & 0xff).toByte)
    def outEdge1: EdgeNumber = EdgeNumber(((value >> 4) & 0xff).toByte)
    def inEdge2: EdgeNumber = EdgeNumber(((value >> 2) & 0xff).toByte)
    def outEdge2: EdgeNumber = EdgeNumber((value & 0xff).toByte)
  }

  object FeaturePair {
    def apply(value: Int): FeaturePair = new FeaturePair(value)

    def apply(i2: EdgeNumber, o2: EdgeNumber): FeaturePair =
      FeaturePair(NO_EDGE, NO_EDGE, i2, o2)

    def apply(i1: EdgeNumber, o1: EdgeNumber, i2: EdgeNumber, o2: EdgeNumber): FeaturePair =
      new FeaturePair(i1.num << 6 | o1.num << 4 | i2.num << 2 | o2.num)

    def flip(fp: FeaturePair): FeaturePair =
      FeaturePair(fp.inEdge2, fp.outEdge2, fp.inEdge1, fp.outEdge1)
  }

  final case class Contact(separation: Float, normal: Vec2, position: Vec2, feature: FeaturePair) {
    var Pn: Float = 0.0f
    var Pt: Float = 0.0f
    var Pnb: Float = 0.0f

    var r1: Vec2 = Vec2(0.0f, 0.0f)
    var r2: Vec2 = Vec2(0.0f, 0.0f)

    var massNormal: Float = 0.0f
    var massTangent: Float = 0.0f
    var bias: Float = 0.0f
  }

  final class ArbiterKey(val body1: Body, val body2: Body) {
    def <(that: ArbiterKey): Boolean =
      if (body1 < that.body1) true
      else if (body1 == that.body1 && body2 < that.body2) true
      else false
  }

  object ArbiterKey {
    def apply(b1: Body, b2: Body): ArbiterKey =
      if (b1 < b2) new ArbiterKey(b1, b2) else new ArbiterKey(b2, b1)
  }
}
