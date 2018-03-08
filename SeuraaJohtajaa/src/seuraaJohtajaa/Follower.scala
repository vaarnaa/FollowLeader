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
    
    
    
    //val wallRep = wallRepulsion(combinedVel)
    
    var totalVel = combinedVel //+ wallRep
    
    //nopeus ei saa ylittää maksimiarvoa
    if (totalVel.sizeOf() > maxVel) {
      val k = maxVel / totalVel.sizeOf()
      totalVel = totalVel * k
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
  
}