package seuraaJohtajaa

import seuraaJohtajaa._
import java.awt.image.BufferedImage
import org.scalatest._
import org.scalatest.Assertions._
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import collection.mutable.Stack

class GameTest extends FlatSpec {
  
  val maxVelocity = 2.0
  val mass = 60.0
  val width = 600
  val height = 600
  
  val world = new World(height, width, maxVelocity, mass)
  val followers = world.getFollowers
  
  "A new world" should "be empty" in {
    assert(world.getFollowers().size == 0)
    assertThrows[NoSuchElementException] {
      world.getLeader()
    }
    assert(world.target == None)
  }
  
  it should "have pictures for both ships" in {
    assert(world.imgLeader.isInstanceOf[BufferedImage])
    assert(world.imgFollower.isInstanceOf[BufferedImage])
  }
  
  "Method createInitialShips" should "create one follower and leader" in {
    world.createInitialShips()
    assert(world.getFollowers().size == 1)
    assert(world.getLeader().isInstanceOf[Leader])
  }
  
  "Method addFollower" should "work if there are less than 30 followers" in {
    
    while(followers.size < 30) {
      world.addFollower()
    }
    
    assert(world.addFollower() == false)
    assert(followers.size == 30)
  }
  
  "Method removeFollower" should "work if there are more than 0 followers" in {
    
    while(followers.size > 0) {
      world.removeFollower()
    }
    
    assert(world.removeFollower() == false)
    assert(followers.size == 0)
  }
  
  
  "Follower target" should "update correctly to 100 units behind leader" in {
    val leader = world.getLeader()
    world.target = Some(Vector2D(300,300))
    
    leader.place = Vector2D(200,300)
    leader.velocity = Vector2D(1,0)
    leader.move()
    assert(leader.followerTarget == Vector2D(100,300))
    
    leader.place = Vector2D(400,300)
    leader.velocity = Vector2D(-1,0)
    leader.move()
    assert(leader.followerTarget == Vector2D(500,300))
    
    leader.place = Vector2D(300,200)
    leader.velocity = Vector2D(0,1)
    leader.move()
    assert(leader.followerTarget == Vector2D(300,100))
    
    leader.place = Vector2D(300,400)
    leader.velocity = Vector2D(0,-1)
    leader.move()
    assert(leader.followerTarget == Vector2D(300,500))
  }
  
  "Leader velocity" should "always be less than leaderMaxVelocity" in {
    val leader = world.getLeader()
    world.target = Some(Vector2D(500,500))
    
    leader.place = Vector2D(200,200)
    leader.velocity = Vector2D(2, 0)
    
    for(a <- 1 to 100){
      leader.move()
      assert(leader.place != Vector2D(200,200))
      assert(leader.velocity.sizeOf() <= world.leaderMaxVelocity)
    }
  }
  
  "Follower velocity" should "always be less than followerMaxVelocity" in {
    world.addFollower()
    val follower = followers.head
    val leader = world.getLeader()
    leader.followerTarget = Vector2D(500,500)
    
    follower.place = Vector2D(200,200)
    follower.velocity = Vector2D(2, 0)
    
    for(a <- 1 to 100){
      follower.move()
      assert(follower.place != Vector2D(200,200))
      assert(follower.velocity.sizeOf() <= world.followerMaxVelocity)
    }
    
  }
  
}
