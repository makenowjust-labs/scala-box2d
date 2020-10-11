package codes.quine.labo.box2d

/** Mat22 is a 2x2 matrix.
  *
  * Note that this matrix value is viewd as the following:
  *
  * {{{
  * [col1.x col2.x
  *  col1.y col2.y]
  * }}}
  */
final case class Mat22(col1: Vec2, col2: Vec2) {

  /** Calculates transpose of this matrix. */
  def transpose: Mat22 = Mat22(Vec2(col1.x, col2.x), Vec2(col1.y, col2.y))

  /** Calculates invert of this matrix. */
  def invert: Mat22 = {
    val a = col1.x
    val b = col2.x
    val c = col1.y
    val d = col2.y
    val det = a * d - b * c
    assert(det != 0.0f)
    val invDet = 1.0f / det
    Mat22(invDet * d, -invDet * b, -invDet * c, invDet * a)
  }

  /** Applies this matrix to `v`. */
  def *(v: Vec2): Vec2 = Vec2(col1.x * v.x + col2.x * v.y, col1.y * v.x + col2.y * v.y)

  /** Returns a new matrix adding `m` from this matrix. */
  def +(m: Mat22): Mat22 = Mat22(col1 + m.col1, col2 + m.col2)

  /** Calculates mulplication between this matrix and `m`. */
  def *(m: Mat22): Mat22 = Mat22(this * m.col1, this * m.col2)

  override def toString: String = s"Mat22(${col1.x}, ${col2.x}, ${col1.y}, ${col2.y})"
}

object Mat22 {

  /** Constructs Mat22 value with human-readable syntax.
    *
    * `Mat22(a, b, c, d)` is viewed as the following:
    *
    * {{{
    * [a b
    *  c d]
    * }}}
    */
  def apply(a: Float, b: Float, c: Float, d: Float): Mat22 = new Mat22(Vec2(a, c), Vec2(b, d))

  /** Constructs rotation matrix. */
  def rotation(angle: Float): Mat22 = {
    val c = Math.cos(angle).toFloat
    val s = Math.sin(angle).toFloat
    Mat22(c, -s, s, c)
  }
}
