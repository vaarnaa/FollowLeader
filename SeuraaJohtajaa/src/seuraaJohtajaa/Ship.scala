package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform


abstract class Ship(var world: World, var velocity: Vector2D, var place: Vector2D, img: BufferedImage) {
  
 
  
  def draw(g: Graphics2D) = {
    
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
    
    at.setToRotation(angle, place.x + 15, place.y + 15);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt, place.y.toInt)
    g.setTransform(oldTransform)
    
  }
  
  
  /*def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    if (place.x < world.width / 8) {
      velX =  2 * world.maxVelocity / ( 1 + place.x)
    }
    
    if (place.x > world.width * 7 / 8) {
      velX =  -(2 * world.maxVelocity / ( 1 + world.width - place.x))
    }
    
    if (place.y < world.height / 8) {
      velY =  2 * world.maxVelocity / ( 1 + place.y)
    }
    
    if (place.y > world.height * 7 / 8) {
      velY = -(2 * world.maxVelocity / ( 1 + world.height - place.y))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }*/
  
  def wallRepulsion(combVel: Vector2D): Vector2D
  
  def move: Unit
  
}

