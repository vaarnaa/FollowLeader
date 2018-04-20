package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform


abstract class Ship(var velocity: Vector2D, var place: Vector2D, img: BufferedImage) {
  
 
  
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
    g.drawImage(this.img, null, place.x.toInt, (place.y.toInt))
    g.setTransform(oldTransform);
    
  }
  
  def move: Unit
}

