package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Leader(world: World, mass: Double, var velocity: Vector2D, var place: Vector2D, img: BufferedImage) extends Ship() {
  
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
    println()
    val oldTransform = g.getTransform()
    //val newTransform = oldTransform.clone().asInstanceOf[AffineTransform]
    val at = new AffineTransform() 
    
    at.setToRotation(angle, place.x, place.y);
    g.setTransform(at)
    g.drawImage(this.img, null, place.x.toInt, (place.y.toInt))
    g.setTransform(oldTransform);
  }
  
  override def move() {
    //tutkitaan etäisyys kohteeseen
    val direction = world.target - place
    
    //muutetaan kohteen sijaintia jos ollaan lähellä
    if (direction.sizeOf() < 20) {
      world.target = Vector2D(util.Random.nextInt(600) + 100, util.Random.nextInt(600) + 100)
      world.timerTarget.restart() 
    }
    
    //lasketaan uusi nopeus
    velocity = calculateVelocity()
    
    //lasketaan uusi paikka
    place = place + Vector2D(velocity.x, velocity.y)
  }
  
  def calculateVelocity() = {
    
    val seekVel = seekingVelocity()
    val wandVel = wanderingVelocity(seekVel)
    var combinedVel = seekVel + wandVel
    
    val direction = world.target - place
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * maxVelocity, 0.005), maxVelocity)
      else maxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel *=  k
    }
    
    
    
    val wallRep = wallRepulsion(combinedVel)
    
    var totalVel = combinedVel + wallRep
    
    //nopeus ei saa ylittää maksimiarvoa
    if (totalVel.sizeOf() > maxVel) {
      val k = maxVel / totalVel.sizeOf()
      totalVel = totalVel * k
    }
    
    totalVel
    
  }
  
  def seekingVelocity() = {
    
    //lasketaan suunta kohteeseen
    val direction = world.target - place
    
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
  
  def wanderingVelocity(seekVel: Vector2D) = {
    
    //lasketaan wanderinging nopeusvektorin pituus
    val seekVelSize = seekVel.sizeOf()
    wanderingRadius = Math.min(seekVelSize / 10, 0.5)
    
    //lasketaan vanhan nopeuden kulma
    val velOldAngle = {
      if (velocity.x == 0 && velocity.y == 0) 0
      else if (velocity.x == 0 && velocity.y > 0) Math.PI / 2
      else if (velocity.x == 0 && velocity.y < 0) -Math.PI / 2
      else if (velocity.x < 0 && velocity.y < 0)  Math.atan(velocity.y / velocity.x) + Math.PI
      else if (velocity.x < 0 && velocity.y > 0)  Math.atan(velocity.y / velocity.x) + Math.PI
      else Math.atan(velocity.y / velocity.x)
    }
    
    //lasketaan seeking nopeuden kulma
    val velNewAngle = {
      if (seekVel.x == 0 && seekVel.y == 0) 0
      else if (seekVel.x == 0 && seekVel.y > 0) Math.PI / 2
      else if (seekVel.x == 0 && seekVel.y < 0) -Math.PI / 2
      else if (seekVel.x < 0 && seekVel.y < 0)  Math.atan(seekVel.y / seekVel.x) + Math.PI
      else if (seekVel.x < 0 && seekVel.y > 0)  Math.atan(seekVel.y / seekVel.x) + Math.PI
      else Math.atan(seekVel.y / seekVel.x)
    }
    
    //lasketaan nopeuden kulman muutos
    val velAngleChange = velNewAngle - velOldAngle
    
    //lasketaan wandering nopeuden kulman muutos
    //val wandAngleChange = Math.min(util.Random.nextDouble() / 100 * Math.abs(velAngleChange), 0.05)
    //if (util.Random.nextDouble() < 0.5) wanderAngle -=wandAnglechange
    //else wanderAngle += wandAnglechange
    val wandAngleChange = util.Random.nextDouble() / 100 * Math.abs(velAngleChange)
    if (util.Random.nextDouble() < 0.5) wanderAngle = wandAngleChange
    else wanderAngle = (-1) * wandAngleChange
    //wanderAngle = wandAngleChange
    
    val wanderVel = Vector2D(wanderingRadius * Math.cos(wanderAngle + velNewAngle), wanderingRadius * Math.sin(wanderAngle + velNewAngle))
    val check = Vector2D(seekVelSize * Math.cos(wanderAngle + velNewAngle), seekVelSize * Math.sin(wanderAngle + velNewAngle))
    
    wanderVel
    
  }
  
  
  def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    if (place.x < world.width / 10) {
      velX =  2 * maxVelocity / ( 1 + place.x)
    }
    
    if (place.x > world.width * 9 / 10) {
      velX =  -(2 * maxVelocity / ( 1 + world.width - place.x))
    }
    
    if (place.y < world.height / 10) {
      velY =  2 * maxVelocity / ( 1 + place.y)
    }
    
    if (place.y > world.height * 9 / 10) {
      velY = -(2 * maxVelocity / ( 1 + world.height - place.y))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }
  
}