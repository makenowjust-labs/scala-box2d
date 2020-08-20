package codes.quine.labo.box2d
package demo

import javafx.scene.canvas.GraphicsContext
import scalafx.animation.KeyFrame
import scalafx.animation.Timeline
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.CheckBox
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.util.Duration

import MathUtil.FloatOps
import Demo._

object DemoApp extends JFXApp {
  // Renderer:

  val width = 1000
  val height = 720
  val zoom = 40.0f
  val panY = 8.0f

  val canvas = new Canvas(width, height)
  val gc: GraphicsContext = canvas.getGraphicsContext2D()

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

    gc.setStroke(Color(0.8, 0.8, 0.9, 1.0))
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

    gc.setStroke(Color(0.5, 0.5, 0.8, 1.0))
    gc.beginPath()
    gc.moveTo(pos1.x, pos1.y)
    gc.lineTo(p1.x, p1.y)
    gc.lineTo(pos2.x, pos2.y)
    gc.lineTo(p2.x, p2.y)
    gc.closePath()
    gc.stroke()
  }

  // UI:

  val combobox = new ComboBox(demoNames)
  combobox.onAction = { _ =>
    world.clear()
    demos(combobox.value.value)()
  }
  combobox.selectionModel.value.select(0)

  val accumulateImpulses = new CheckBox("Accumulation")
  accumulateImpulses.selected = true
  accumulateImpulses.onAction = { _ =>
    World.accumulateImpulses = accumulateImpulses.selected.value
  }

  val positionCorrection = new CheckBox("Position Correction")
  positionCorrection.selected = true
  positionCorrection.onAction = { _ =>
    World.positionCorrection = positionCorrection.selected.value
  }

  val warmStarting = new CheckBox("Warm Starting")
  warmStarting.selected = true
  warmStarting.onAction = { _ =>
    World.warmStarting = warmStarting.selected.value
  }

  val fps = new Text("FPS: 60.00")

  var prevTime: Long = System.currentTimeMillis()
  val timeline: Timeline = new Timeline {
    keyFrames = Seq(
      KeyFrame(
        Duration.apply(1000.0 * timeStep),
        onFinished = { _ =>
          val time = System.currentTimeMillis()
          val diff = time - prevTime
          prevTime = time
          fps.text.value = f"FPS: ${1000.0 / diff}%2.2f"

          world.step(timeStep)

          gc.setFill(Color(0.0, 0.0, 0.0, 1.0))
          gc.fillRect(0, 0, width.toDouble, height.toDouble)
          for (b <- world.bodies) {
            drawBody(b)
          }
          for (j <- world.joints) {
            drawJoint(j)
          }
        }
      )
    )
    cycleCount = Timeline.Indefinite
  }

  timeline.play()
  demo1()

  stage = new PrimaryStage {
    title = "scala-labo-box2d demo"
    scene = new Scene(width.toDouble, height.toDouble) {
      content = new HBox {
        children = Seq(
          new VBox {
            maxWidth = 280
            fillWidth = true
            children = Seq(
              new Text("scala-labo-box2d demo"),
              combobox,
              accumulateImpulses,
              positionCorrection,
              warmStarting,
              fps
            )
          },
          canvas
        )
      }
    }
  }
}
