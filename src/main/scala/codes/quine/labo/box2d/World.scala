package codes.quine.labo.box2d

import scala.collection.mutable

import Arbiter._
import MathUtil.FloatOps

final case class World(val gravity: Vec2, val iterations: Int) {
  val bodies: mutable.Buffer[Body] = mutable.Buffer.empty
  val joints: mutable.Buffer[Joint] = mutable.Buffer.empty
  val arbiters: mutable.Map[ArbiterKey, Arbiter] = mutable.Map.empty

  def add(body: Body): Unit = bodies.append(body)

  def add(joint: Joint): Unit = joints.append(joint)

  def clear(): Unit = {
    bodies.clear()
    joints.clear()
    arbiters.clear()
  }

  def broadPhase(): Unit = {
    // O(n^2) broad-phase
    for ((bi, i) <- bodies.zipWithIndex) {
      for (bj <- bodies.slice(i + 1, bodies.size); if bi.invMass != 0.0f || bj.invMass != 0.0f) {
        val newArb = Arbiter(bi, bj)
        val key = ArbiterKey(bi, bj)

        if (newArb.contacts.nonEmpty) {
          if (arbiters.contains(key)) {
            arbiters(key).update(newArb.contacts)
          } else {
            arbiters(key) = newArb
          }
        } else {
          arbiters.remove(key)
        }
      }
    }
  }

  def step(dt: Float): Unit = {
    val invDt = if (dt > 0.0f) 1.0f / dt else 0.0f

    // Determine overlapping bodies and update contact points.
    broadPhase()

    // Integrate forces.
    for (b <- bodies; if b.invMass != 0.0f) {
      b.velocity += dt * (gravity + b.invMass * b.force)
      b.angularVelocity += dt * b.invI * b.torque
    }

    // Perform pre-steps.
    for (arb <- arbiters.values) arb.preStep(invDt)
    for (joint <- joints) joint.preStep(invDt)

    // Perform iterations
    for (_ <- 1 until iterations) {
      for (arb <- arbiters.values) arb.applyImpluse()
      for (joint <- joints) joint.applyImpluse()
    }

    // Integrate Velocities
    for (b <- bodies) {
      b.position += dt * b.velocity
      b.rotation += dt * b.angularVelocity

      b.force.set(0.0f, 0.0f)
      b.torque = 0.0f
    }
  }
}

object World {
  var positionCorrection: Boolean = true
  var warmStarting: Boolean = true
  var accumulateImpulses: Boolean = true
}
