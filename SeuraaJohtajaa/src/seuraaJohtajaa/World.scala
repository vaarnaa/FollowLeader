package seuraaJohtajaa

import scala.collection.mutable.Buffer
import java.awt.{Graphics2D, Color}
import java.io._
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D
import java.awt.event.ActionListener

class World(val height: Int, val width: Int, var maxVelocity: Double) {
  
  val followers = Buffer[Follower]()
  
  var leaderMaxVelocity = maxVelocity
  var followerMaxVelocity = maxVelocity
  
  val imgLeader = ImageIO.read(new File("alus_musta.png"))
  val imgFollower = ImageIO.read(new File("alus2.png"))
  
  var target: Vector2D = null
  var leader: Leader = null
  
  val listenerTarget = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        if (leader != null) {
          targetUpdate()
        }
      }  
    }
    
   val timerTarget = new javax.swing.Timer(2000, listenerTarget)
   timerTarget.start()
  
  
  def createInitialShips() = {
     createLeader()
     followers.clear
     addFollower()
   }
   
   
  def createLeader() = {
     val x = util.Random.nextDouble
     val y = util.Random.nextDouble
     leader = new Leader(
      this,
      70,
      Vector2D(if (x < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble, if (y < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble),
      Vector2D(util.Random.nextInt(width * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100),
      imgLeader) 
   }
   
   
  def addFollower() = {
     if (followers.size < 20 && leader != null) followers.synchronized {
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
   
   def removeFollower() = followers.synchronized {
     if (followers.size > 0) {
       followers.remove(followers.size - 1)
       true
     }
     else
       false
   }
  
  // Avaruuden piirtäminen on asteroidien piirtämistä
  def draw(g: Graphics2D) = followers.synchronized {
    if (leader != null && target != null) {
      g.setColor(Color.red);
      val targetCircle = new Ellipse2D.Double(this.target.x - 8, this.target.y - 8, 2.0 * 8, 2.0 * 8)
      g.fill(targetCircle);
      g.draw(targetCircle);
      leader.draw(g)
      if (followers.size > 0) {
        followers foreach (_.draw(g))
      }
      
    }
  }
  
  def step() = followers.synchronized {
    
    //jos targettia ei luotu, luodaan se
    if (target == null) {
      targetUpdate()
    }
    
    //jos johtaja olemassa, liikutetaan sitä
    if (leader != null) {
      leader.move()
      //if (followers.size > 0) {
        followers.foreach(_.move())
    //}
    }
    
  }
  
  def manualTarget(x: Int, y: Int) = {
    target = Vector2D(x, y)
    timerTarget.restart()
  }
  
  
  def targetUpdate() = {
    target = Vector2D(util.Random.nextInt(height * 7 / 10) + 100, util.Random.nextInt(width * 7 / 10) + 100)
  }
}