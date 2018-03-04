package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.Graphics2D
import java.io._
import javax.imageio.ImageIO

object World {
  
  val ships = Buffer[Ship]()
  
  val shipImg = ImageIO.read(new File("alus1.jpg"))
  
  val leader = new Leader(100, Vector2D(0.5,0.5), Vector2D(100,100), shipImg)
  
  ships += leader
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) {
    ships foreach (_.draw(g))
  }
}