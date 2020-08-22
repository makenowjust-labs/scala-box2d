package codes.quine.labo.box2d

import Arbiter._
import MathUtil.FloatOps

object Collide {
  // Box vertex and edge numbering:
  //
  //        ^ y
  //        |
  //        e1
  //   v2 ------ v1
  //    |        |
  // e2 |        | e4  --> x
  //    |        |
  //   v3 ------ v4
  //        e3

  private sealed abstract class Axis
  private case object FACE_A_X extends Axis
  private case object FACE_A_Y extends Axis
  private case object FACE_B_X extends Axis
  private case object FACE_B_Y extends Axis

  private final case class ClipVertex(v: Vec2, fp: FeaturePair)

  private object ClipVertex {
    def apply(v: Vec2, i2: EdgeNumber, o2: EdgeNumber): ClipVertex =
      ClipVertex(v, FeaturePair(NO_EDGE, NO_EDGE, i2, o2))
  }

  private[this] def clipSegmentToLine(
      vIn: IndexedSeq[ClipVertex],
      normal: Vec2,
      offset: Float,
      clipEdge: EdgeNumber
  ): IndexedSeq[ClipVertex] = {
    val vOut = IndexedSeq.newBuilder[ClipVertex]

    // Calculate the distance of end points to the line
    val distance0 = (normal dot vIn(0).v) - offset
    val distance1 = (normal dot vIn(1).v) - offset

    // If the points are behind the plane
    if (distance0 <= 0.0f) vOut.addOne(vIn(0))
    if (distance1 <= 0.0f) vOut.addOne(vIn(1))

    // If the points are on different sides of the plane
    if (distance0 * distance1 < 0.0f) {
      // Find intersection point of edge and plane
      val interp = distance0 / (distance0 - distance1)
      val v = vIn(0).v + interp * (vIn(1).v - vIn(0).v)
      val fp =
        if (distance0 > 0.0f) FeaturePair(clipEdge, vIn(0).fp.outEdge1, NO_EDGE, vIn(0).fp.outEdge2)
        else FeaturePair(vIn(1).fp.inEdge1, clipEdge, vIn(1).fp.inEdge2, NO_EDGE)
      vOut.addOne(ClipVertex(v, fp))
    }

    vOut.result()
  }

  private[this] def computeIncidentEdge(h: Vec2, pos: Vec2, rot: Mat22, normal: Vec2): IndexedSeq[ClipVertex] = {
    // The normal is from the reference box. Convert it
    // to the incident boxe's frame and flip sign.
    val rotT = rot.transpose
    val n = -(rotT * normal)
    val nAbs = MathUtil.abs(n)

    val (c1, c2) =
      if (nAbs.x > nAbs.y)
        if (MathUtil.sign(n.x) > 0.0f)
          (ClipVertex(Vec2(h.x, -h.y), EDGE3, EDGE4), ClipVertex(Vec2(h.x, h.y), EDGE4, EDGE1))
        else
          (ClipVertex(Vec2(-h.x, h.y), EDGE1, EDGE2), ClipVertex(Vec2(-h.x, -h.y), EDGE2, EDGE3))
      else if (MathUtil.sign(n.y) > 0.0f)
        (ClipVertex(Vec2(h.x, h.y), EDGE4, EDGE1), ClipVertex(Vec2(-h.x, h.y), EDGE1, EDGE2))
      else
        (ClipVertex(Vec2(-h.x, -h.y), EDGE2, EDGE3), ClipVertex(Vec2(h.x, -h.y), EDGE3, EDGE4))

    IndexedSeq(c1.copy(v = pos + rot * c1.v), c2.copy(v = pos + rot * c2.v))
  }

  def detect(bodyA: Body, bodyB: Body): IndexedSeq[Contact] = {
    // Setup
    val hA = 0.5f * bodyA.width
    val hB = 0.5f * bodyB.width

    val posA = bodyA.position
    val posB = bodyB.position

    val rotA = Mat22.rotation(bodyA.rotation)
    val rotB = Mat22.rotation(bodyB.rotation)
    val rotAT = rotA.transpose // == rotA.inverse (because of orthogonal matrix)
    val rotBT = rotB.transpose // == rotB.inverse

    val dp = posB - posA
    val dA = rotAT * dp
    val dB = rotBT * dp

    val C = rotAT * rotB
    val absC = MathUtil.abs(C)
    val absCT = absC.transpose

    // Box A faces
    val faceA = MathUtil.abs(dA) - hA - absC * hB
    if (faceA.x > 0.0f || faceA.y > 0.0f) return IndexedSeq.empty

    // Box B faces
    val faceB = MathUtil.abs(dB) - absCT * hA - hB
    if (faceB.x > 0.0f || faceB.y > 0.0f) return IndexedSeq.empty

    // Find best axis
    val relativeTol = 0.95f
    val absoluteTol = 0.01f

    // Box A faces
    var axis: Axis = FACE_A_X
    var separation = faceA.x
    var normal = if (dA.x > 0.0f) rotA.col1 else -rotA.col1

    if (faceA.y > relativeTol * separation + absoluteTol * hA.y) {
      axis = FACE_A_Y
      separation = faceA.y
      normal = if (dA.y > 0.0f) rotA.col2 else -rotA.col2
    }

    // Box B faces
    if (faceB.x > relativeTol * separation + absoluteTol * hB.x) {
      axis = FACE_B_X
      separation = faceB.x
      normal = if (dB.x > 0.0f) rotB.col1 else -rotB.col1
    }

    if (faceB.y > relativeTol * separation + absoluteTol * hB.y) {
      axis = FACE_B_Y
      separation = faceB.y
      normal = if (dB.y > 0.0f) rotB.col2 else -rotB.col2
    }

    // Compute the clipping lines and the line segment to be clipped.
    val (frontNormal, sideNormal, incidentEdge, front, negSide, posSide, negEdge, posEdge) = axis match {
      case FACE_A_X =>
        val frontNormal = normal
        val front = (posA dot frontNormal) + hA.x
        val sideNormal = rotA.col2
        val side = posA dot sideNormal
        val negSide = -side + hA.y
        val posSide = side + hA.y
        val negEdge = EDGE3
        val posEdge = EDGE1
        val incidentEdge = computeIncidentEdge(hB, posB, rotB, frontNormal)
        (frontNormal, sideNormal, incidentEdge, front, negSide, posSide, negEdge, posEdge)
      case FACE_A_Y =>
        val frontNormal = normal
        val front = (posA dot frontNormal) + hA.y
        val sideNormal = rotA.col1
        val side = posA dot sideNormal
        val negSide = -side + hA.x
        val posSide = side + hA.x
        val negEdge = EDGE2
        val posEdge = EDGE4
        val incidentEdge = computeIncidentEdge(hB, posB, rotB, frontNormal)
        (frontNormal, sideNormal, incidentEdge, front, negSide, posSide, negEdge, posEdge)
      case FACE_B_X =>
        val frontNormal = -normal
        val front = (posB dot frontNormal) + hB.x
        val sideNormal = rotB.col2
        val side = posB dot sideNormal
        val negSide = -side + hB.y
        val posSide = side + hB.y
        val negEdge = EDGE3
        val posEdge = EDGE1
        val incidentEdge = computeIncidentEdge(hA, posA, rotA, frontNormal)
        (frontNormal, sideNormal, incidentEdge, front, negSide, posSide, negEdge, posEdge)
      case FACE_B_Y =>
        val frontNormal = -normal
        val front = (posB dot frontNormal) + hB.y
        val sideNormal = rotB.col1
        val side = posB dot sideNormal
        val negSide = -side + hB.x
        val posSide = side + hB.x
        val negEdge = EDGE2
        val posEdge = EDGE4
        val incidentEdge = computeIncidentEdge(hA, posA, rotA, frontNormal)
        (frontNormal, sideNormal, incidentEdge, front, negSide, posSide, negEdge, posEdge)
    }

    // Clip other face with 5 box planes (1 face plane, 4 edge planes)

    // Clip to box side 1
    val clipPoints1 = clipSegmentToLine(incidentEdge, -sideNormal, negSide, negEdge)
    if (clipPoints1.size < 2) return IndexedSeq.empty

    // Clip to box side 2
    val clipPoints2 = clipSegmentToLine(clipPoints1, sideNormal, posSide, posEdge)
    if (clipPoints2.size < 2) return IndexedSeq.empty

    // Now clipPoints2 contains the clipping points.
    // Due to roundoff, it is possible that clipping removes all points.
    clipPoints2
      .filter(c => (frontNormal dot c.v) - front <= 0)
      .map { c =>
        val separation = (frontNormal dot c.v) - front
        Contact(
          separation,
          normal,
          c.v - separation * frontNormal,
          if (axis == FACE_B_X || axis == FACE_B_Y) FeaturePair.flip(c.fp) else c.fp
        )
      }
  }
}
