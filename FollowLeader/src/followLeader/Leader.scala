package followLeader


import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

//johtajaalusta kuvaava luokka
class Leader(_world: World, _velocity: Vector2D, _place: Vector2D, _img: BufferedImage) extends Ship(_world, _velocity, _place, _img) {
  
  //muuttujia wanderingVelocity metodia varten
  private var wanderingRadius: Double = 0
  private var wanderAngle: Double = 0
  
  //seuraaja-alusten kohde
  var followerTarget = this.place//ector2D(world.width / 2, world.height / 2)
  
  
  //liikutetaan johtajaa uuteen paikkaan
  def move() {
    
    //päivitetään seuraajien kohde 100 yksikköä johtajan taakse
    if (velocity.sizeOf() > 0) {
      val leaderVel = this.velocity
      val leaderVelSize = leaderVel.sizeOf()
      val leaderVelUnit = leaderVel / leaderVelSize
      followerTarget = this.place - leaderVelUnit * 100   
    }
    
    //tutkitaan etäisyys kohteeseen
    val direction = world.target.get - place
    
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
  
  //lasketaan johtajan uusi nopeus
  def calculateVelocity() = {
    
    //lasketaan nopeus seeking- ja wanderingVelocity metodien summana
    val seekVel = seekingVelocity()
    val wandVel = wanderingVelocity(seekVel)
    var combinedVel = seekVel + wandVel
    
    //jos ollaan tarpeeksi lähellä kohdetta, lasketaan nopeutta
    val direction = world.target.get - place
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * world.leaderMaxVelocity, 0.005), world.leaderMaxVelocity)
      else world.leaderMaxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    /*if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel *=  k
    }*/
    
    combinedVel = combinedVel.limitToMax(maxVel)
    
    //seinien aiheuttama hylkimisnopeus
    val wallRep = wallRepulsion(combinedVel)
     
    var totalVel = combinedVel + wallRep
    
    //uusi nopeus ei saa ylittää maksimiarvoa
    /*if (totalVel.sizeOf() > maxVel) {
      val k = maxVel / totalVel.sizeOf()
      totalVel = totalVel * k
    }*/
    totalVel = totalVel.limitToMax(maxVel)
    
    totalVel
    
  }
  
  //lasketaan nopeus, jolla pyritään kohti kohdetta
  def seekingVelocity() = {
    
    //lasketaan suunta kohteeseen
    val direction = world.target.get - place
    
    //lasketaan tarvittava muutosnopeusvektori
    var steerDir = direction + velocity
    
    //nopeuden muutos ei saa ylittää maksimiarvoa
    /*if (steerDir.sizeOf() > world.maxVelChange) { 
      val k = world.maxVelChange / steerDir.sizeOf()
      steerDir = steerDir * k
    }*/
    steerDir = steerDir.limitToMax(world.maxVelChange)
    
    
    
    //lasketaan uusi nopeusvektori ja etäisyydestä riippuva maksiminopeus
    var newVel = velocity + steerDir
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * world.leaderMaxVelocity, world.leaderMaxVelocity)
      else world.leaderMaxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    /*if (newVel.sizeOf() > maxVel) {
      val k = maxVel / newVel.sizeOf()
      newVel = newVel * k
    }*/
    newVel = newVel.limitToMax(maxVel)
    
    newVel
    
  }
  
  //lasketaan nopeus, jolla poikkeatetaan suunta seekingVeocityn metodin arvosta ns. pyöristetään mutkia
  def wanderingVelocity(seekVel: Vector2D) = {
    
    //lasketaan wandering nopeusvektorin pituus
    val seekVelSize = seekVel.sizeOf()
    //vähentää äkkikäännöksiä pienillä nopeuksilla
    wanderingRadius = Math.max(seekVelSize / 10, world.leaderMaxVelocity / 20)
      /*{
      if (world.leaderMaxVelocity >= 2) Math.max(seekVelSize / 10, 0.1)
      if (world.leaderMaxVelocity >= 1.6) Math.max(seekVelSize / 10, 0.08)
      if (world.leaderMaxVelocity >= 1) Math.max(seekVelSize / 10, 0.05)
      else Math.max(seekVelSize / 10, 0.1/ world.leaderMaxVelocity)
      }*/ 
    
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
    //se on maksimissaan sadas osa seekingVelocityn aiheuttamasta kulmanmuutoksesta
    val wandAngleChange = util.Random.nextDouble() / 100 * Math.abs(velAngleChange)
    if (util.Random.nextDouble() < 0.5) wanderAngle = wandAngleChange
    else wanderAngle = (-1) * wandAngleChange
    
    //vaeltelu nopeus
    val wanderVel = Vector2D(wanderingRadius * Math.cos(wanderAngle + velNewAngle), wanderingRadius * Math.sin(wanderAngle + velNewAngle))
    
    wanderVel
    
  }
  
  //seinien aiheuttama hylkimisnopeus
  def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    
    //repulsiota on vain tietyllä etäisyydellä seinistä
    
    if (place.x < 100) {
      velX =  2 * world.leaderMaxVelocity / ( 1 + {if(place.x - 15 > 0)place.x - 15 else 0} )
    }
    
    if (place.x > world.width - 100) {
      velX =  -(2 * world.leaderMaxVelocity / ( 1 + {if(world.width - place.x - 15 > 0)world.width - place.x - 15 else 0} ))
    }
    
    if (place.y < 100) {
      velY =  2 * world.leaderMaxVelocity / ( 1 + {if(place.y - 15 > 0)place.y - 15 else 0} )
    }
    
    if (place.y > world.height - 100) {
      velY = -(2 * world.leaderMaxVelocity / ( 1 + {if(world.height - place.y - 15 > 0)world.height - place.y - 15 else 0}))
    }
    
    //hylkimisnopeus
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }
  
}