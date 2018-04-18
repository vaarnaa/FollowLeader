package seuraaJohtajaa

import scala.swing._
import scala.swing.event._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import scala.collection.mutable.Buffer
import java.awt.event.ActionListener
import java.awt.geom.Ellipse2D

object Game extends SimpleSwingApplication {
  
  val width      = 800
  val height     = 800
  
  val gameWorld = new World(height, width)
  
  /*
  GameState = 1, means game is running
  GameState = 0, means game is stopped
   */
  var gameState = 1
  
  def top = new MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    //resizable = true
    
    minimumSize   = new Dimension(width,height)
    preferredSize = new Dimension(width,height)
    maximumSize   = new Dimension(width,height)
    
    val buttonPause = new Button("Pause")
    val buttonAddFollower = new Button("Add follower")
    val buttonRemoveFollower = new Button("Remove follower")
    
    
    val buttons = new FlowPanel {
      contents += buttonPause
      contents += buttonAddFollower
      contents += buttonRemoveFollower
    }
    
    val arena = new Panel {
      
      override def paintComponent(g: Graphics2D) = {

        // Piirretään valkoisella vanhan kuvan päälle suorakaide
        g.setColor(new Color(255, 255, 255))
        g.fillRect(0, 0, width, height)

        // Pyydetään graphics:ilta siloiteltua grafiikkaa ns. antialiasointia
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)          

        // Piirretään pelimaailma
        g.setColor(Color.red);
        val targetCircle = new Ellipse2D.Double(gameWorld.target.x - 8, gameWorld.target.y - 8, 2.0 * 8, 2.0 * 8)
        g.fill(targetCircle);
        g.draw(targetCircle);
        gameWorld.draw(g)
        
        
      }
   
    }
    
    val textField = new TextField()
    textField.editable = false
    
    
    //contents += arena
    contents = new BorderPanel {
      add(buttons, BorderPanel.Position.North)
      add(arena, BorderPanel.Position.Center)
      add(textField, BorderPanel.Position.South)
    }
    
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
    }
    
    listenTo(buttonPause)
    listenTo(buttonAddFollower)
    listenTo(buttonRemoveFollower)
    listenTo(arena.keys)
    listenTo(arena.mouse.clicks)
      
    reactions += {
      case ButtonClicked(`buttonPause`) => pause()
      case ButtonClicked(`buttonAddFollower`) => if (gameWorld.addFollower()) textField.text = "Follower added"
      case ButtonClicked(`buttonRemoveFollower`) => if (gameWorld.removeFollower()) textField.text = "Follower removed"
      case MouseClicked(_, p, _, _, _) => pause()
      case KeyPressed(_, Key.Enter, _, _) => pause()
        /*if (key == 'p') {
          println("moro")
          pause()
        }
        else {
          println("moro")
        }*/
    }
    
    private def pause(): Unit = {
      if (gameState == 1) {
    	  gameTimer.stop()
    	  gameState = 0
    	  textField.text = "Game paused"
      }
      else {
        gameTimer.restart()
        gameState = 1
        textField.text = "Game restarted"
      }
    }
    
    def newGame() = {
      
    }
    
    val listener = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        gameWorld.step()
        arena.repaint() 
      }  
    }
    
    val gameTimer = new javax.swing.Timer(6, listener)
    gameTimer.start()
    
    
    
    
    
    
    
  }
  
}