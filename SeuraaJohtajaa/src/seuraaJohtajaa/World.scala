package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.{Graphics2D, Color}
import java.io._
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D

class World() {
  
  val ships = Buffer[Ship]()
  
  val shipImg = ImageIO.read(new File("alus_musta.png"))
  
  var target = Vector2D(300, 500)
  
  var leader = new Leader(this, 100, Vector2D(-1,-1), Vector2D(100,100), shipImg)
  
  ships += leader
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) {
    //ships foreach (_.draw(g))
    leader.draw(g)
  }
  
  def step() = {
    //ships.foreach(_.move())
    leader.move()
  }
}