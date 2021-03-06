package followLeader

import scala.collection.mutable.Buffer
import java.awt.{Graphics2D, Color}
import java.io._
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D
import java.awt.event.ActionListener

class World(val height: Int, val width: Int, var maxVelocity: Double, var mass: Double) {
  
  //puskuri seuraajia varten
  private val followers = Buffer[Follower]()
  
  //yleisiä arvoja aluksille
  var leaderMaxVelocity = maxVelocity
  var followerMaxVelocity = maxVelocity
  var maxVelChange = maxVelocity / mass
  
  //kertoo seuraavatko alukset johtajaa vai toisiaan
  var inFleetMode = true
  
  val leaderImageFileName: String = "alus_musta.png"
  val followerImageFileName: String = "alus2.png"
  
  //johtaja-aluksen kuva
  val imgLeader = {
    try {
           ImageIO.read(new File(leaderImageFileName))
          
        } 
      catch {
        case e: IOException => {
          println(s"Couldn't load image: $leaderImageFileName")
          println("Closing program")
          sys.exit(0)  
        }
      }
  }
  
  //seuraaja-aluksen kuva
  val imgFollower = {
    try {
           ImageIO.read(new File(followerImageFileName))
          
        } 
      catch {
        case e: IOException => {
          println(s"Couldn't load image: $followerImageFileName")
          println("Closing program")
          sys.exit(0)  
        }
      }
  }
  
  //alustetaan johtajan kohde ja johtaja tyhjiksi
  var target: Option[Vector2D] = None
  private var leader: Option[Leader] = None
  
  
   //laskee uuden maksimimuutosnopeuden
   def newMaxVelChange() = {
     maxVelChange = maxVelocity / mass
   }
  
  //tyhjentää seuraajat puskurista ja luo johtajan ja kolme seuraajaa simulaation alussa
  def createInitialShips() = {
     createLeader()
     followers.clear
     for(_ <- 1 to 3) {
       addFollower()
     }
   }
   
   //luo uuden johtajan satunnaiseen paikkaan
  def createLeader() = {
     val x = util.Random.nextDouble
     val y = util.Random.nextDouble
     leader = Some(new Leader(
      this,
      Vector2D(if (x < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble, if (y < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble),
      Vector2D(util.Random.nextInt(width * 7 / 10) + 100, util.Random.nextInt(height * 7 / 10) + 100),
      imgLeader))
   }
  
  //getteri seuraajien hakemiseen
  def getFollowers() = {
    followers
  }
  
  //getteri johtajan hakemiseen
  def getLeader() = {
    leader.get
  }
  
   
  //luo uuden seuraajan satunnaiseen paikkaan ja lisää sen puskuriin
  def addFollower() = {
     if (followers.size < 30 && !leader.isEmpty) followers.synchronized {
       val x = util.Random.nextDouble
       val y = util.Random.nextDouble
       followers += new Follower(
        this,
        Vector2D(if (x < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble, if (y < 0.5) util.Random.nextDouble else (-1) * util.Random.nextDouble),
        Vector2D(util.Random.nextInt(width - 100) + 50, util.Random.nextInt(height - 100) + 50),
        imgFollower)
       true
     }
     
     else
       false
   }
  
  //poistaa puskurista viimeksi lisätyn seuraajan
   def removeFollower() = followers.synchronized {
     if (followers.size > 0) {
       followers.remove(followers.size - 1)
       true
     }
     else
       false
   }
  
  //piirretään alukset ja johtajan kohde
  def draw(g: Graphics2D) = followers.synchronized {
    if (!leader.isEmpty && !target.isEmpty) {
      g.setColor(Color.red);
      val targetCircle = new Ellipse2D.Double(this.target.get.x - 8, this.target.get.y - 8, 2.0 * 8, 2.0 * 8)
      g.fill(targetCircle);
      g.draw(targetCircle);
      leader.get.draw(g)
      if (followers.size > 0) {
        followers foreach (_.draw(g))
      }
      
    }
  }
  
  //lasketaan alusten ja johtajan kohteen uudet paikat
  def step() = followers.synchronized {
    
    //jos targettia ei vielä luotu, luodaan se
    if (target.isEmpty) {
      targetUpdate()
    }
    
    //jos johtaja olemassa, liikutetaan aluksia
    if (!leader.isEmpty) {
      leader.get.move()
      followers.foreach(_.move())
    }
    
  }
  
  //johtajan kohde päivitetään manuaalisesti hiirellä osoitettuun kohtaan
  def manualTarget(x: Int, y: Int) = {
    target = Some(Vector2D(x, y))
    timerTarget.restart()
  }
  
  //johtajan kohde päivitetään satunnaiseen paikkaan
  def targetUpdate() = {
    target = Some(Vector2D(util.Random.nextInt(width - 200) + 100, util.Random.nextInt(height - 200) + 100))
  }
  
  //päivitetään johtajan kohde tapahtumankäsittelijällä
  val listenerTarget = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        if (!leader.isEmpty) {
          targetUpdate()
        }
      }  
    }
   
  //ajastin johtajan kohteen päivitystä varten
   val timerTarget = new javax.swing.Timer(3000, listenerTarget)
   timerTarget.start()
   
   
}