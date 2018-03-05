package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform


abstract class Ship(speed: Vector2D , var place: Vector2D, img: BufferedImage) {
  
  def draw(g: Graphics2D) = {
    val angle = Math.atan(1.0 * place.y / place.x)
    val oldTransform = g.getTransform()
    val at = new AffineTransform()
    
    at.setToRotation(angle, place.x, place.y);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt, (place.y.toInt))
    //g.rotate(angle, place.x, place.y);
    
    
    
    
    //g.translate(place.x, place.y)
    //g.rotate(angle)
    //g.rotate(Math.atan(1.0 * place.y / place.x));
    //print(Math.atan(1.0 * place.y / place.x))
    //g.fill(shape)
    //g.drawImage(this.img, null, place.x.toInt+15, -(place.y.toInt+15))
    
    //g.setTransform(oldTransform)
    //g.drawImage(this.img, null, 300, 300)
  }
  
  def move() {
    place = place + Vector2D(speed.x, speed.y)
  }
}

