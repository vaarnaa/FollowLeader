package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

//abstrakti luokka aluksille
abstract class Ship(var world: World, var velocity: Vector2D, var place: Vector2D, img: BufferedImage) {
  
 
  //piirretään alukset simulaatioalueelle oikeaan paikkaa ja oikeassa kulmassa
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
    
    at.setToRotation(angle, place.x, place.y);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt - 15, place.y.toInt - 15)
    g.setTransform(oldTransform)
    
  }
  
  
  def wallRepulsion(combVel: Vector2D): Vector2D
  
  def move: Unit
  
}

