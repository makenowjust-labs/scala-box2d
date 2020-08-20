package codes.quine.labo.box2d
package demo

import org.scalajs.dom.document
import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.HTMLCanvasElement
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.raw.HTMLOptionElement
import org.scalajs.dom.raw.HTMLSelectElement
import org.scalajs.dom.window

import MathUtil.FloatOps
import Demo._

object DemoApp {
  // Renderer:

  val width = 1000
  val height = 720
  val zoom = 40.0f
  val panY = 8.0f

  val canvas: HTMLCanvasElement = document.querySelector("#canvas").asInstanceOf[HTMLCanvasElement]
  val gc: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def drawBody(b: Body): Unit = {
    val rot = Mat22.rotation(b.rotation)
    val pos = b.position
    val h = 0.5f * b.width

    val o = Vec2(width / 2.0f, height / 2.0f + panY * zoom)
    val s = Mat22(zoom, 0.0f, 0.0f, -zoom)

    val v1 = o + s * (pos + rot * Vec2(-h.x, -h.y))
    val v2 = o + s * (pos + rot * Vec2(h.x, -h.y))
    val v3 = o + s * (pos + rot * Vec2(h.x, h.y))
    val v4 = o + s * (pos + rot * Vec2(-h.x, h.y))

    gc.strokeStyle = "rgb(204, 204, 229.5)"
    gc.beginPath()
    gc.moveTo(v1.x, v1.y)
    gc.lineTo(v2.x, v2.y)
    gc.lineTo(v3.x, v3.y)
    gc.lineTo(v4.x, v4.y)
    gc.closePath()
    gc.stroke()
  }

  def drawJoint(j: Joint): Unit = {
    val b1 = j.body1
    val b2 = j.body2

    val rot1 = Mat22.rotation(b1.rotation)
    val rot2 = Mat22.rotation(b2.rotation)

    val o = Vec2(width / 2.0f, height / 2.0f + panY * zoom)
    val s = Mat22(zoom, 0.0f, 0.0f, -zoom)

    val pos1 = o + s * b1.position
    val p1 = pos1 + s * (rot1 * j.localAnchor1)

    val pos2 = o + s * b2.position
    val p2 = pos2 + s * rot2 * j.localAnchor2

    gc.strokeStyle = "rgb(127.5, 127.5, 204)"
    gc.beginPath()
    gc.moveTo(pos1.x, pos1.y)
    gc.lineTo(p1.x, p1.y)
    gc.lineTo(pos2.x, pos2.y)
    gc.lineTo(p2.x, p2.y)
    gc.closePath()
    gc.stroke()
  }

  // UI:

  val accumulateImpulses: HTMLInputElement =
    document.querySelector("#accumulateImpulses").asInstanceOf[HTMLInputElement]
  accumulateImpulses.addEventListener[Event](
    "change",
    { _ =>
      World.accumulateImpulses = accumulateImpulses.checked
    }
  )

  val positionCorrection: HTMLInputElement =
    document.querySelector("#positionCorrection").asInstanceOf[HTMLInputElement]
  positionCorrection.addEventListener[Event](
    "change",
    { _ =>
      World.positionCorrection = positionCorrection.checked
    }
  )

  val warmStarting: HTMLInputElement = document.querySelector("#warmStarting").asInstanceOf[HTMLInputElement]
  warmStarting.addEventListener[Event](
    "change",
    { _ =>
      World.warmStarting = warmStarting.checked
    }
  )

  val combobox: HTMLSelectElement = document.querySelector("#combobox").asInstanceOf[HTMLSelectElement]
  for ((name, i) <- demoNames.zipWithIndex) {
    val option = document.createElement("option").asInstanceOf[HTMLOptionElement]
    option.text = name
    option.value = name
    option.selected = i == 0
    combobox.appendChild(option)
  }
  combobox.addEventListener[Event](
    "change",
    { _ =>
      world.clear()
      demos(combobox.value)()
    }
  )

  def main(args: Array[String]): Unit = {
    demo1()

    window.setInterval(
      { () =>
        world.step(timeStep)

        gc.fillStyle = "rgb(0, 0, 0)"
        gc.fillRect(0, 0, width.toDouble, height.toDouble)
        for (b <- world.bodies) {
          drawBody(b)
        }
        for (j <- world.joints) {
          drawJoint(j)
        }
      },
      1000.0 * timeStep
    )
  }
}
