package seuraaJohtajaa


case class Vector2D(x: Double, y: Double) {
  
  //summataan kaksi vektoria keskenään
  def + (other: Vector2D) = {
    Vector2D(x + other.x, y + other.y)    
  }
  
  //miinustetaan vektori toisesta vektorista
  def - (other: Vector2D) = {
    Vector2D(x + other.x, y + other.y)    
  }
  
}