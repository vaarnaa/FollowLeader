# Follow leader

Scala project implementing boids algorithms. One leader ship follows the target dot on the screen and the other ships follow the leader. The follower ships simulate flocking behavior of birds.

### Simulation
---
The movement of ships is controlled by the following algorithms:
##### Leader:
- Seeking - steers the leader ship towards the position of the target dot
- Wandering - applies randomness to the movement of the leader ship, increases fluidity by eliminating sharp turns

##### Follower:
- Separation - helps followers from bumping into each other or the leader
- Arrival - steers followers toward the leader ship, decelerates once near the target
- Avoidance - steers followers away from the path of the leader

##### All ships
- Wall repulsion - helps ships from bumping into the walls

### Contents
---
Following files are included in the project:
- Ship.scala - implements the abstract ship class
- Follower.scala - implements the follower ship class
- Leader.scala - implements the leader ship class
- Game.scala - provides GUI for the simulation
- Vector2D.scala - a class for representing position and direction vectors
- World.scala - a class to represent simulation world to store simulation objects
