package seuraaJohtajaa

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform

class Follower(_world: World, _velocity: Vector2D, _place: Vector2D, _img: BufferedImage) extends Ship(_world, _velocity, _place, _img) {
    
  
  //minimietäisyys, jolla alukset hylkivät toisiaan
  private val separationDistance = 60
  
  //seuraajien kohde jonomoodissa
  var nextFollowerTarget = this.place
  
  
  //liikutetaan seuraaja uuteen paikkaan
  def move() {
    
    //päivitetään seuraajien jonomoodin kohde 20 yksikköä seuraajan taakse
    if (velocity.sizeOf() > 0) {
      val followerVel = this.velocity
      val followerVelSize = followerVel.sizeOf()
      val followerVelUnit = followerVel / followerVelSize
      nextFollowerTarget = this.place - followerVelUnit * 20   
    }
    
    //lasketaan uusi nopeus
    velocity = calculateVelocity()
    
    //lasketaan uusi paikka
    place = place + Vector2D(velocity.x, velocity.y)
  }
  
  //lasketaan seuraajan uusi nopeus
  def calculateVelocity() = {
    
    //nopeus jolla pyritään kohteeseen
    var arrVel = arrivalVelocity()
    
    //maksiminopeus riippuu etäisyydestä kohteeseen
    val direction = world.getLeader().followerTarget - place
    val maxVel = {
      if (direction.sizeOf() < 200) Math.min(Math.max(direction.sizeOf() / 100 * world.followerMaxVelocity, 0.005), world.followerMaxVelocity)
      else world.followerMaxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (arrVel.sizeOf() > maxVel) {
      val k = maxVel / arrVel.sizeOf()
      arrVel *=  k
    }
    
    //seinien aiheuttama hylkimisnopeus
    val wallRep = wallRepulsion(arrVel)
    
    var combinedVel = arrVel + wallRep
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel = combinedVel * k
    }
    
    //nopeus, jolla väistetään takana olevaa johtajaa
    val velAvoidance = avoidanceVelocity()
    combinedVel += velAvoidance
    
    //nopeus ei saa ylittää maksimiarvoa
    if (combinedVel.sizeOf() > maxVel) {
      val k = maxVel / combinedVel.sizeOf()
      combinedVel = combinedVel * k
    }
    
    //kokonaisnopeuteen lisätään vielä toisten alusten aiheuttama hylkimisnopeus
    var totalVel = combinedVel + separationVelocity()
    
    //nopeus ei saa ylittää maksimiarvoa
    if (totalVel.sizeOf() > maxVel) {
      val k = maxVel / totalVel.sizeOf()
      totalVel *= k
    }
    
    totalVel
    
  }
  
  //nopeus jolla pyritään kohti kohdetta johtajan takana
  def arrivalVelocity() = {
    
    val followers = world.getFollowers()
    var direction = Vector2D(0,0)
    
    //lasketaan suunta kohteeseen, moodista riippuen joko johtajan tai edellisen seuraajan taakse
    if (world.inFleetMode || (this equals followers.head))
      direction = world.getLeader().followerTarget - place
    else {
      direction = followers(followers.indexOf(this) - 1).nextFollowerTarget - place
    }
      
      
    //lasketaan tarvittava muutosnopeusvektori
    var steerDir = direction + velocity
    if (steerDir.sizeOf() > world.maxVelChange) { //nopeuden muutos ei saa ylittää maksimiarvoa
      val k = world.maxVelChange / steerDir.sizeOf()
      steerDir = steerDir * k
    }
    
    //lasketaan uusi nopeusvektori
    var newVel = velocity + steerDir
    val maxVel = { //maksiminopeus riippuu etäisyydestä kohteeseen
      if (direction.sizeOf() < 200) Math.min(direction.sizeOf() / 50 * world.followerMaxVelocity, world.followerMaxVelocity)
      else world.followerMaxVelocity
    }
    
    //nopeus ei saa ylittää maksimiarvoa
    if (newVel.sizeOf() > maxVel) {
      val k = maxVel / newVel.sizeOf()
      newVel = newVel * k
    }
    
    newVel
    
  }
  
  //seinien aiheuttama hylkimisnopeus
  def wallRepulsion(combVel: Vector2D) = {
    
    //lasketaan seinien repulsiot eri suunnille ja seinille
    var velX = 0.0
    var velY = 0.0
    
    //hylkimistä tapahtuu vain riittävän lähellä seiniä
    if (place.x < 100) {
      velX =  2 * world.followerMaxVelocity / ( 1 + {if(place.x - 15 > 0)place.x - 15 else 0} )
    }
    
    if (place.x > world.width - 100) {
      velX =  -(2 * world.followerMaxVelocity / ( 1 + {if(world.width - place.x - 15 > 0)world.width - place.x - 15 else 0} ))
    }
    
    if (place.y < 100) {
      velY =  2 * world.followerMaxVelocity / ( 1 + {if(place.y - 15 > 0)place.y - 15 else 0} )
    }
    
    if (place.y > world.height - 100) {
      velY = -(2 * world.followerMaxVelocity / ( 1 +  {if(world.height - place.y - 15 > 0)world.height - place.y - 15 else 0} ))
    }
    
    val velRep = Vector2D(velX, velY)
    
    velRep
     
  }
  
  //johtajan edessä olevaan seuraajaan kohdistuu hylkimisvoima johtajan nopeuden normaalin suuntaisesti
  def avoidanceVelocity() = {
    
    //vektoreita nopeuden laskemiseksi
    var avoidanceVector = Vector2D(0,0)
    var avoidanceVelocity = Vector2D(0,0)
    var leaderVector = Vector2D(0,0)
    
    //lasketaan nopeus vain, jos johtajan nopeuskomponentit x ja y eroavat nollasta,
    //koska näillä arvoilla suorien kulmakertoimia ei voi laskea
    if (world.getLeader().velocity.y != 0 && world.getLeader().velocity.x != 0) {
      
      //lasketaan johtajan nopeusvektorin määräämä suoran- ja normaalin kulmakerroin
      val k = world.getLeader().velocity.y / world.getLeader().velocity.x
      val kNormal = -1/k
      
      //lasketaan seuraajan paikan ja nopeuden määräämän suoran leikkauspiste johtajan suoran kanssa
      val x = (1/k * this.place.x + this.place.y + k * world.getLeader().place.x - world.getLeader().place.y) / (k + 1/k)
      val y = k * x - k * world.getLeader().place.x + world.getLeader().place.y
      //print("x: ", x, "y: ", y)
      
      //lasketaan vektori pisteestä seuraajan paikkaan
      avoidanceVector = this.place - Vector2D(x, y)
      
      //lasketaan vektori johtajan paikasta leikkauspisteeseen
      leaderVector = Vector2D(x, y) - world.getLeader().place
      
    }
    
    //lasketaan välttelyvektorin pituus ja määrätään välttelynopeus,
    //mikäli seuraaja on tarpeeksi lähellä seuraajan edellä, jolloin vektorien x ja y komponentit ovat samanmerkkisiä
    val disAvoidanceVector = avoidanceVector.sizeOf()
    if (disAvoidanceVector != 0 && disAvoidanceVector < 100 && leaderVector.sizeOf() < 200 && leaderVector.x * world.getLeader().velocity.x >= 0 && leaderVector.y * world.getLeader().velocity.y >= 0) {
      avoidanceVelocity =  avoidanceVector / disAvoidanceVector * world.followerMaxVelocity / ( 1 + {if(disAvoidanceVector - 50 > 0)disAvoidanceVector - 50 else 0} )
    }
    
    avoidanceVelocity
    
  }
  
  //alusten toisiinsa kohdistaama hylkimisnopeus
  def separationVelocity() = {
    
    var sepVel = Vector2D(0,0)
    
    //lasketaan hylkimisnopeus kaikista muista seuraajista
    for (follower <- world.getFollowers()) {
      
      if (follower equals this) {
      }
      
      else {
        //lasketaan etäisyys seuraajien välillä
        val distanceVector = this.place - follower.place 
        val distance = distanceVector.sizeOf()
        
        //jos seuraajat ovat tarpeeksi lähellä, hylkivät ne toisiaan
        if (distance < separationDistance) {
          
          //jokaisen seuraajan hylkimisnopeus lasketaan yhteen
          val sepVelFollower =  distanceVector / distanceVector.sizeOf() * 1 * Math.max(world.followerMaxVelocity, 1) / ( 1 + {if(distance - 30 > 0)distance - 30 else 0} )
          sepVel += sepVelFollower
        }
      }
    }
    
    //lasketaan vielä hylkimisnopeus johtajasta
    val distanceVector = this.place - world.getLeader().place 
    val distance = distanceVector.sizeOf()
    
    //jos johtaja on tarpeeksi lähellä, hylkii se seuraajaa
    //ilman johtajan huomioimista metodissa, törmäisi johtaja seuraajiin hiljaisilla nopeuksilla
    if (distance < separationDistance) {
      
      //lasketaan hylkimisnopeus ja ynnätään se jo summattuihin
      val sepVelLeader =  distanceVector / distanceVector.sizeOf() * 1 * Math.max(world.followerMaxVelocity, 1) / ( 1 + {if(distance - 30 > 0)distance - 30 else 0})
      sepVel += sepVelLeader
    }
    
    sepVel
    
  }
  
}