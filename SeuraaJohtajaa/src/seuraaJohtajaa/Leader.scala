package seuraaJohtajaa


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Leader(_world: World, _velocity: Vector2D, _place: Vector2D, _img: BufferedImage) extends Ship(_world, _velocity, _place, _img) {
  
  //val world.maxVelocity = 2.0
  //val maxVelChange = 0.04
  //var maxVelChange = world.leaderMaxVelocity / world.mass
  val minDistance = 50
  var wanderingRadius: Double = 0
  var wanderAngle: Double = 0
  var followerTarget = Vector2D(world.width / 2, world.height / 2)
  
  
  def move() {
    
    //update followerTarget
    if (velocity.sizeOf() > 0) {
      val leaderVel = world.leader.velocity
      val leaderVelSize = leaderVel.sizeOf()
      val leaderVelUnit = leaderVel / leaderVelSize
      followerTarget = world.leader.place - leaderVelUnit * 30   
    }
    
    //tutkitaan etäisyys kohteeseen
    val direction = world.target - place
    
    //muutetaan kohteen sijaintia jos ollaan lähellä
    if (direction.sizeOf() < 30) {
      world.targetUpdate()
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
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * world.leaderMaxVelocity, 0.005), world.leaderMaxVelocity)
      else world.leaderMaxVelocity
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
    if (steerDir.sizeOf() > world.maxVelChange) { //nopeuden muutos ei saa ylittää maksimiarvoa
      val k = world.maxVelChange / steerDir.sizeOf()
      steerDir = steerDir * k
    }
    
    //lasketaan uusi nopeusvektori
    var newVel = velocity + steerDir
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * world.leaderMaxVelocity, world.leaderMaxVelocity)
      else world.leaderMaxVelocity
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
    if (place.x < 100) {
      velX =  2 * world.leaderMaxVelocity / ( 1 + {if(place.x - 20 < 0)place.x else place.x - 20})
    }
    
    if (place.x > world.width - 100) {
      velX =  -(2 * world.leaderMaxVelocity / ( 1 + {if(world.width - place.x - 20 < 0)world.width - place.x else world.width - place.x - 20}))
    }
    
    if (place.y < 100) {
      velY =  2 * world.leaderMaxVelocity / ( 1 + {if(place.y - 10 < 0)place.y else place.y - 10})
    }
    
    if (place.y > world.height - 100) {
      velY = -(2 * world.leaderMaxVelocity / ( 1 + {if(world.height - place.y - 20 < 0)world.height - place.y else world.height - place.y - 20}))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }
  
}