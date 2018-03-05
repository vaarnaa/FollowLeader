package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.Graphics2D
import java.io._
import javax.imageio.ImageIO

class World() {
  
  val ships = Buffer[Ship]()
  
  val shipImg = ImageIO.read(new File("alus2.png"))
  
  val leader = new Leader(100, Vector2D(0.5,0.5), Vector2D(200,200), shipImg)
  
  ships += leader
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) {
    ships foreach (_.draw(g))
  }
  
  def step() = {
    ships.foreach(_.move())
  }
}