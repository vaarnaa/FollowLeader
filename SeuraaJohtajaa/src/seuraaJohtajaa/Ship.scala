package seuraaJohtajaa

import java.awt.Graphics2D

abstract class Ship(speed: Vector2D , var place: Vector2D) {
  
  def draw(g: Graphics2D) = {
    val oldTransform = g.getTransform()
    
    g.translate(place.x, place.y)
    //g.rotate(angle)
    //g.fill(shape)
    
    g.setTransform(oldTransform)
  }
  
  def move() {
    place = place + speed
  }
}

