package codes.quine.labo.box2d

import scala.collection.mutable

import Arbiter._
import MathUtil.FloatOps

/** World is a world for physics simulation. */
final class World private (var gravity: Vec2, var iterations: Int) {
  private val _bodies: mutable.Buffer[Body] = mutable.Buffer.empty
  private val _joints: mutable.Buffer[Joint] = mutable.Buffer.empty
  private val _arbiters: mutable.Map[ArbiterKey, Arbiter] = mutable.Map.empty

  /** A list of rigid bodies on this world. */
  def bodies: Seq[Body] = _bodies.toSeq
  /** A list of joints on this world. */
  def joints: Seq[Joint] = _joints.toSeq
  /** A map from bodies pair to arbiters working on this world and the bodies. */
  def arbiters: Map[ArbiterKey, Arbiter] = _arbiters.toMap

  /** Adds the given rigid body to this world. */
  def add(body: Body): Unit = _bodies.append(body)
  /** Adds the given joint to this world. */
  def add(joint: Joint): Unit = _joints.append(joint)

  /** Clear rigid bodies and joints from this world. */
  def clear(): Unit = {
    _bodies.clear()
    _joints.clear()
    _arbiters.clear()
  }

  private[this] def broadPhase(): Unit = {
    // O(n^2) broad-phase
    for ((bi, i) <- bodies.zipWithIndex) {
      for (bj <- bodies.slice(i + 1, bodies.size); if bi.invMass != 0.0f || bj.invMass != 0.0f) {
        val newArb = Arbiter(bi, bj)
        val key = ArbiterKey(bi, bj)

        if (newArb.contacts.nonEmpty) {
          if (arbiters.contains(key)) {
            _arbiters(key).update(newArb.contacts)
          } else {
            _arbiters(key) = newArb
          }
        } else {
          _arbiters.remove(key)
        }
      }
    }
  }

  /** Run simulation step. */
  def step(dt: Float): Unit = {
    val invDt = if (dt > 0.0f) 1.0f / dt else 0.0f

    // Determine overlapping bodies and update contact points.
    broadPhase()

    // Integrate forces.
    for (b <- bodies; if b.invMass != 0.0f) {
      b.velocity += dt * (gravity + b.invMass * b.force)
      b.angularVelocity += dt * b.invInertia * b.torque
    }

    // Perform pre-steps.
    for (arb <- arbiters.values) arb.preStep(invDt)
    for (joint <- joints) joint.preStep(invDt)

    // Perform iterations
    for (_ <- 0 until iterations) {
      for (arb <- arbiters.values) arb.applyImpluse()
      for (joint <- joints) joint.applyImpluse()
    }

    // Integrate Velocities
    for (b <- bodies) {
      b.position += dt * b.velocity
      b.rotation += dt * b.angularVelocity

      b.force = Vec2(0.0f, 0.0f)
      b.torque = 0.0f
    }
  }
}

object World {
  def apply(gravity: Vec2 = Vec2(0.0f, -10.0f), iterations: Int = 10): World =
    new World(gravity, iterations)

  var accumulateImpulses: Boolean = true
  var positionCorrection: Boolean = true
  var warmStarting: Boolean = true
}
