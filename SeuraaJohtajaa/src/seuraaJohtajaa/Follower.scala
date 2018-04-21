package seuraaJohtajaa

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Follower(_world: World, mass: Double, _velocity: Vector2D, _place: Vector2D, _img: BufferedImage) extends Ship(_world, _velocity, _place, _img) {
    
  
  //val maxVelChange = 0.04
  var maxVelChange = world.maxVelocity / mass
  val minDistance = 50
  var wanderingRadius: Double = 0
  var wanderAngle: Double = 0
  val separationDistance = 100
  
  
  
  def move() {
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
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * world.maxVelocity, 0.005), world.maxVelocity)
      else world.maxVelocity
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
    
    val velAvoidance = avoidanceVelocity()
    combinedVel += velAvoidance
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel = combinedVel * k
    }
    
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
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * world.maxVelocity, world.maxVelocity)
      else world.maxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (newVel.sizeOf() > maxVel) {
      val k = maxVel / newVel.sizeOf()
      newVel = newVel * k
    }
    
    newVel
    
  }
  
  
  /*def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    if (place.x < world.width / 8) {
      velX =  2 * world.world.maxVelocity / ( 1 + place.x)
    }
    
    if (place.x > world.width * 7 / 8) {
      velX =  -(2 * world.world.maxVelocity / ( 1 + world.width - place.x))
    }
    
    if (place.y < world.height / 8) {
      velY =  2 * world.world.maxVelocity / ( 1 + place.y)
    }
    
    if (place.y > world.height * 7 / 8) {
      velY = -(2 * world.world.maxVelocity / ( 1 + world.height - place.y))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }*/
  
  def avoidanceVelocity() = {
    
    var avoidanceVector = Vector2D(0,0)
    var avoidanceVelocity = Vector2D(0,0)
    var leaderVector = Vector2D(0,0)
    //var x = 0.0
    //var y = 0.0
    
    if (world.leader.velocity.y != 0 && world.leader.velocity.x != 0) {
      
      //lasketaan johtajan nopeusvektorin määräämä suoran- ja normaalin kulmakerroin
      val k = world.leader.velocity.y / world.leader.velocity.x
      val kNormal = -1/k
      
      //lasketaan seuraajan paikan ja nopeuden määräämän suoran leikkauspiste johtajan suoran kanssa
      val x = (1/k * this.place.x + this.place.y + k * world.leader.place.x - world.leader.place.y) / (k + 1/k)
      val y = k * x - k * world.leader.place.x + world.leader.place.y
      //print("x: ", x, "y: ", y)
      
      //lasketaan vektori pisteestä seuraajan paikkaan
      avoidanceVector = this.place - Vector2D(x, y)
      //print("AvoidanceVector: ", avoidanceVector)
      
      //lasketaan vektori johtajan paikasta leikkauspisteeseen
      leaderVector = Vector2D(x, y) - world.leader.place
      
    }
    
    
    
    //lasketaan välttelyvektorin pituus ja määrätään välttelynopeus,
    //mikäli seuraaja on tarpeeksi lähellä seuraajan edellä, jolloin vektorien x ja y komponentit ovat samanmerkkisiä
    val disAvoidanceVector = avoidanceVector.sizeOf()
    if (disAvoidanceVector != 0 && disAvoidanceVector < 100 && leaderVector.sizeOf() < 200 && leaderVector.x * world.leader.velocity.x >= 0 && leaderVector.y * world.leader.velocity.y >= 0) {
      avoidanceVelocity =  avoidanceVector / disAvoidanceVector * world.maxVelocity / ( 1 + disAvoidanceVector)
      //print("AvoidanceVelocity: ", avoidanceVelocity)
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
          val sepVelFollower =  distanceVector / distanceVector.sizeOf() * 2 * world.maxVelocity / ( 1 + distance)
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
      val sepVelLeader =  distanceVector / distanceVector.sizeOf() * 4 * world.maxVelocity / ( 1 + distance)
      sepVel += sepVelLeader
    }
    
    sepVel
    
    
  }
  
}