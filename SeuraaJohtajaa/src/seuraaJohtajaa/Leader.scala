package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Leader(world: World, mass: Double, var velocity: Vector2D, var place: Vector2D, img: BufferedImage) extends Ship() {
  
  val maxVelocity = 2.0
  val maxVelChange = 0.04
  val minDistance = 50
  
  
  override def draw(g: Graphics2D) = {
    val angle = {
      if (place.x == 0 && place.y == 0) 0
      else if (place.x == 0 && place.y > 0) Math.PI / 2
      else if (place.x == 0 && place.y < 0) -Math.PI / 2
      else  Math.atan(place.y / place.x)
    }
    val oldTransform = g.getTransform()
    val at = new AffineTransform()
    
    at.setToRotation(angle, place.x, place.y);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt, (place.y.toInt))
  }
  
  override def move() {
    //tutkitaan etäisyys kohteeseen
    val direction = world.target - place
    
    //muutetaan kohteen sijaintia jos ollaan lähellä
    if (direction.sizeOf() < 20) {
      world.target = Vector2D(util.Random.nextInt(600) + 100, util.Random.nextInt(600) + 100)
    }
    
    calculateVelocity()
    place = place + Vector2D(velocity.x, velocity.y)
  }
  
  def calculateVelocity() = {
    
    //suunta kohteeseen
    val direction = world.target - place
    
    //lasketaan muutosnopeusvektori
    var realDir = direction + velocity
    if (realDir.sizeOf() > maxVelChange) {
      val k = maxVelChange / realDir.sizeOf()
      realDir = realDir * k
    }
    
    //println("Paikat", world.target, place)
    //println("Nopeudet", realDir, velocity)
    
    //lasketaan uusi nopeusvektori
    var newVel = velocity + realDir
    val maxVel = {
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * maxVelocity, maxVelocity)
      else maxVelocity
    }
    if (newVel.sizeOf() > maxVel) {
      val k = maxVel / newVel.sizeOf()
      newVel = newVel * k
      //println(k)
    }
    //print(newVel)
    velocity = newVel
    
    
  }
  
}