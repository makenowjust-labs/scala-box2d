package codes.quine.labo.box2d

/** Vec2 is a 2D vector. */
final case class Vec2(x: Float, y: Float) {

  /** Calculates negation of this vector. */
  def unary_- : Vec2 = new Vec2(-x, -y)

  /** Returns a length of this vector. */
  def length: Float = Math.hypot(x, y).toFloat

  /** Calculates dot product between this vector and `v`. */
  def dot(v: Vec2): Float = x * v.x + y * v.y

  /** Calculates cross product between this vector and `v`. */
  def cross(v: Vec2): Float = x * v.y - y * v.x

  /** Calculates cross product between this vector and `a`. */
  def cross(a: Float): Vec2 = Vec2(a * y, -a * x)

  /** Returns a new vector adding `v` from this vector. */
  def +(v: Vec2): Vec2 = Vec2(x + v.x, y + v.y)

  /** Returns a new vector subtracting `v` from this vector. */
  def -(v: Vec2): Vec2 = Vec2(x - v.x, y - v.y)

  override def toString: String = s"Vec2($x, $y)"
}
