package seuraaJohtajaa

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Follower(world: World, mass: Double, var velocity: Vector2D, var place: Vector2D, img: BufferedImage) extends Ship() {
    
  val maxVelocity = 2.0
  //val maxVelChange = 0.04
  val maxVelChange = maxVelocity / mass
  val minDistance = 50
  var wanderingRadius: Double = 0
  var wanderAngle: Double = 0
  val separationDistance = 100
  
  
  override def draw(g: Graphics2D) = {
    val angle = {
      if (velocity.x == 0 && velocity.y == 0) 0
      else if (velocity.x == 0 && velocity.y > 0) Math.PI / 2
      else if (velocity.x == 0 && velocity.y < 0) -Math.PI / 2
      else if (velocity.x < 0 && velocity.y < 0)  Math.atan(velocity.y / velocity.x) + Math.PI
      else if (velocity.x < 0 && velocity.y > 0)  Math.atan(velocity.y / velocity.x) + Math.PI
      else Math.atan(velocity.y / velocity.x)
    }
    
    val oldTransform = g.getTransform()
    val at = new AffineTransform() 
    
    at.setToRotation(angle, place.x, place.y);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt, (place.y.toInt))
    g.setTransform(oldTransform);
  }
  
  
  override def move() {
    //tutkitaan etäisyys kohteeseen
    val direction = world.leader.followerTarget - place
    
    //muutetaan kohteen sijaintia jos ollaan lähellä
    if (direction.sizeOf() < 20) {
    }
    
    //lasketaan uusi nopeus
    velocity = calculateVelocity()
    
    //lasketaan uusi paikka
    place = place + Vector2D(velocity.x, velocity.y)
  }
  
  
  def calculateVelocity() = {
    
    val seekVel = arrivalVelocity()
    var combinedVel = seekVel
    
    val direction = world.leader.followerTarget - place
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * maxVelocity, 0.005), maxVelocity)
      else maxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel *=  k
    }
    
    val wallRep = wallRepulsion(combinedVel)
    
    combinedVel += wallRep
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel = combinedVel * k
    }
    
    /*val velAvoidance = avoidanceVelocity()
    println(velAvoidance)
    combinedVel += velAvoidance*/
    
    //nopeus ei saa ylittää maksimiarvoa
    /*if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel = combinedVel * k
    }*/
    
    var totalVel = combinedVel + separationVelocity()
    
    //nopeus ei saa ylittää maksimiarvoa
    if (totalVel.sizeOf() > maxVel) {
      val k = maxVel / totalVel.sizeOf()
      totalVel *= k
    }
    
    totalVel
    
  }
  
  
  def arrivalVelocity() = {
    
    //lasketaan suunta kohteeseen
    //val direction = world.leader.place - place
    val direction = world.leader.followerTarget - place
    
    //lasketaan tarvittava muutosnopeusvektori
    var steerDir = direction + velocity
    if (steerDir.sizeOf() > maxVelChange) { //nopeuden muutos ei saa ylittää maksimiarvoa
      val k = maxVelChange / steerDir.sizeOf()
      steerDir = steerDir * k
    }
    
    //lasketaan uusi nopeusvektori
    var newVel = velocity + steerDir
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * maxVelocity, maxVelocity)
      else maxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (newVel.sizeOf() > maxVel) {
      val k = maxVel / newVel.sizeOf()
      newVel = newVel * k
    }
    
    newVel
    
  }
  
  
  def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    if (place.x < world.width / 8) {
      velX =  2 * maxVelocity / ( 1 + place.x)
    }
    
    if (place.x > world.width * 7 / 8) {
      velX =  -(2 * maxVelocity / ( 1 + world.width - place.x))
    }
    
    if (place.y < world.height / 8) {
      velY =  2 * maxVelocity / ( 1 + place.y)
    }
    
    if (place.y > world.height * 7 / 8) {
      velY = -(2 * maxVelocity / ( 1 + world.height - place.y))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }
  
  def avoidanceVelocity() = {
    
    var avoidanceVector = Vector2D(0,0)
    var avoidanceVelocity = Vector2D(0,0)
    
    if (world.leader.velocity.y != 0 && world.leader.velocity.x != 0) {
      
      //lasketaan johtajan nopeusvektorin määräämä suoran- ja normaalin kulmakerroin
      val k = world.leader.velocity.y / world.leader.velocity.x
      val kNormal = -1/k
      
      //lasketaan seuraajan paikan ja nopeuden määräämän suoran leikkauspiste johtajan suoran kanssa
      val x = (1/k * this.place.x + this.place.y + k * world.leader.place.x - world.leader.place.y) / (k + 1/k)
      val y = k * x - k * world.leader.place.x + world.leader.place.y
      
      //lasketaan vektori pisteestä seuraajan paikkaan
      avoidanceVector = this.place - Vector2D(x, y)
      
    }
    
    //lasketaan välttelyvektorin pituus ja määrätään välttelynopeus
    val disAvoidanceVector = avoidanceVector.sizeOf()
    if (disAvoidanceVector < 10) {
      avoidanceVelocity =  avoidanceVector / disAvoidanceVector * maxVelocity / ( 1 + disAvoidanceVector)
    }
    
    avoidanceVelocity
    
    
    
    
     
    
  }
  
  
  def separationVelocity() = {
    var sepVel = Vector2D(0,0)
    
    for (follower <- world.followers) {
      
      if (follower equals this) {
      }
      
      //jos seuraaja on jokin toinen
      else {
        //lasketaan etäisyys seuraajien välillä
        val distanceVector = this.place - follower.place 
        val distance = distanceVector.sizeOf()
        
        //jos seuraajat ovat tarpeeksi lähellä, hylkivät ne toisiaan
        if (distance < separationDistance) {
          //jokaisen seuraajan hylkimisnopeus lasketaan yhteen
          val sepVelFollower =  distanceVector / distanceVector.sizeOf() * 2 * maxVelocity / ( 1 + distance)
          sepVel += sepVelFollower
        }
      }
    }
    
    //lasketaan vielä hylkimisnopeus johtajasta
    val distanceVector = this.place - world.leader.place 
    val distance = distanceVector.sizeOf()
    
    //jos johtaja ovat tarpeeksi lähellä, hylkii se seuraajaa
    if (distance < separationDistance) {
      //lasketaan hylkimisnopeus ja ynnätään se jo summattuihin
      val sepVelLeader =  distanceVector / distanceVector.sizeOf() * 4 * maxVelocity / ( 1 + distance)
      sepVel += sepVelLeader
    }
    
    sepVel
    
    
  }
  
}