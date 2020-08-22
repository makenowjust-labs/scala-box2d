package codes.quine.labo.box2d

/**
  * Vec2 represents a 2D vector.
  *
  * @param x x-axis value
  * @param y y-axis value
  */
final case class Vec2(x: Float, y: Float) {
  def unary_- : Vec2 = new Vec2(-x, -y)

  def length: Float = Math.hypot(x, y).toFloat

  def dot(v: Vec2): Float = x * v.x + y * v.y

  def cross(v: Vec2): Float = x * v.y - y * v.x

  def cross(a: Float): Vec2 = Vec2(a * y, -a * x)

  def +(v: Vec2): Vec2 = Vec2(x + v.x, y + v.y)

  def -(v: Vec2): Vec2 = Vec2(x - v.x, y - v.y)

  override def toString: String = s"Vec2($x, $y)"
}
