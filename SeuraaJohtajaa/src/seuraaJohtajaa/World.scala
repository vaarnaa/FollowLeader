package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.{Graphics2D, Color}
import java.io._
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D
import java.awt.event.ActionListener

class World(val height: Int, val width: Int) {
  
  val followers = Buffer[Follower]()
  
  val imgLeader = ImageIO.read(new File("alus_musta.png"))
  val imgFollower = ImageIO.read(new File("alus2.png"))
  
  var target = Vector2D(300, 500)
  
  val leader = new Leader(this, 70, Vector2D(0,0), Vector2D(300,300), imgLeader)
  
  //lisätään followereita satunnaisilla aloituspaikoilla ja -nopeuksilla
  for( x <- 0 until 1 ){
   val x = util.Random.nextDouble
   val y = util.Random.nextDouble
   followers += new Follower(
      this,
      70,
      Vector2D(if (x < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble, if (y < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble),
      Vector2D(util.Random.nextInt(width * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100),
      imgFollower)
}
  
  /*val follower = new Follower(
      this,
      70,
      Vector2D(util.Random.nextDouble, util.Random.nextInt),
      Vector2D(util.Random.nextInt(width * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100),
      imgFollower)
      * 
      */
  
  //followers += follower
  
  val listenerTarget = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        targetUpdate()
      }  
    }
    
   val timerTarget = new javax.swing.Timer(2000, listenerTarget)
   timerTarget.start()
  
  //ships += leader
   
  def addFollower() = {
     if (followers.size < 20) {
       val x = util.Random.nextDouble
       val y = util.Random.nextDouble
       followers += new Follower(
        this,
        70,
        Vector2D(if (x < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble, if (y < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble),
        Vector2D(util.Random.nextInt(width * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100),
        imgFollower)
       true
     }
     
     else
       false
   }
   
   def removeFollower() = {
     if (followers.size > 0) {
       followers.remove(followers.size - 1)
       true
     }
     else
       false
   }
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) = {
    followers foreach (_.draw(g))
    leader.draw(g)
  }
  
  def step() = {
    followers.foreach(_.move())
    leader.move()
  }
  
  def targetUpdate() = {
    target = Vector2D(util.Random.nextInt(height * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100)
  }
}