package seuraaJohtajaa

//vektoriluokka, joka kuvaa nopeuksia ja paikkoja simulaatioalueessa
case class Vector2D(x: Double, y: Double) {
  
  //summataan kaksi vektoria keskenään
  def + (other: Vector2D) = {
    Vector2D(x + other.x, y + other.y)    
  }
  
  //miinustetaan vektori toisesta vektorista
  def - (other: Vector2D) = {
    Vector2D(x - other.x, y - other.y)    
  }
  
  //kerrotaan vektori vakiolla
  def * (k: Double): Vector2D = {
    return Vector2D(x * k, y * k) 
  }
  
  //jaetaan vektori vakiolla
  def / (k: Double): Vector2D = {
    return Vector2D(x / k, y / k) 
  }
  
  //lasketaan vektorin pituus
  def sizeOf() = {
    Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2))
  }
  
}