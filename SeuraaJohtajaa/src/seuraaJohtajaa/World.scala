package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.{Graphics2D, Color}
import java.io._
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D
import java.awt.event.ActionListener

class World(val height: Int, val width: Int) {
  
  val ships = Buffer[Ship]()
  
  val imgLeader = ImageIO.read(new File("alus_musta.png"))
  
  var target = Vector2D(300, 500)
  
  val leader = new Leader(this, 70, Vector2D(0,0), Vector2D(300,300), imgLeader)
  
  val follower = new Leader(this, 50, Vector2D(-1,-1), Vector2D(100,100), imgLeader)
  
  val listenerTarget = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        targetUpdate()
      }  
    }
    
   val timerTarget = new javax.swing.Timer(2000, listenerTarget)
   timerTarget.start()
  
  //ships += leader
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) {
    //ships foreach (_.draw(g))
    leader.draw(g)
  }
  
  def step() = {
    //ships.foreach(_.move())
    leader.move()
  }
  
  def targetUpdate() = {
    target = Vector2D(util.Random.nextInt(600) + 100, util.Random.nextInt(600) + 100)
  }
}