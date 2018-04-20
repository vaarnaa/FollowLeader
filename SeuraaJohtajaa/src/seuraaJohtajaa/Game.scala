package seuraaJohtajaa

import scala.swing._
import scala.swing.event._
import Swing._
//import swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import scala.collection.mutable.Buffer
import java.awt.event.ActionListener
import java.awt.geom.Ellipse2D





class Canvas(var gameState: Int, var gameWorld: World, height: Int, width: Int) extends MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    //resizable = true
    
    var gameArea = new Arena(width, height)
    
    
    
    //minimumSize   = new Dimension(width, height)
    //preferredSize = new Dimension(mainFrameWidth,mainFrameHeight)
    //maximumSize   = new Dimension(mainFrameWidth,mainFrameHeight)
    
    val buttonPause = new Button("Pause") { font = new Font("Arial", 0, 16)}
    val buttonAddFollower = new Button("Add follower") { font = new Font("Arial", 0, 16)}
    val buttonRemoveFollower = new Button("Remove follower") { font = new Font("Arial", 0, 16)}
    
    val buttons = new FlowPanel {
      contents += buttonPause
      contents += buttonAddFollower
      contents += buttonRemoveFollower
    }
    
     
    
    class Arena(val width: Int, height: Int) extends Panel {
      
      preferredSize = new Dimension(width, height)
      
      //border = LineBorder(Color.BLACK)
      
      override def paintComponent(g: Graphics2D) = {
        
        if (gameState == 0) {
          g.setColor(new Color(255, 255, 255))
          g.fillRect(0, 0, width, height)
        }

        // Piirretään valkoisella vanhan kuvan päälle suorakaide
        g.setColor(new Color(255, 255, 255))
        g.fillRect(0, 0, width, height)

        // Pyydetään graphics:ilta siloiteltua grafiikkaa ns. antialiasointia
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)          

        // Piirretään pelimaailma
        gameWorld.draw(g)
        
      }
   
    } 
    
    val textArea = new TextArea(1, 1) {
      editable = false
      //border = LineBorder(Color.BLACK)
      border = EmptyBorder(10)
      font = new Font("Arial", 0, 18)
      
    }
    
    //textField.
    //text
    
    /*val centerPanel = new BorderPanel { 
     contents += arena.center
    }*/
    
    contents = new BorderPanel {
      layout(buttons) = BorderPanel.Position.North
      layout(gameArea) = BorderPanel.Position.Center
      layout(textArea) = BorderPanel.Position.South
      border = LineBorder(Color.BLACK)
    }
    //contents = borderPanel
    
    
    
    menuBar = new MenuBar {
      contents += new Menu("File") {
        { font = new Font("Arial", 0, 16)}
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
      contents += new Menu("New Game") {
        { font = new Font("Arial", 0, 16)}
        contents += new MenuItem(Action("500 x 500") {
          newGame(500)
        })
        contents += new MenuItem(Action("600 x 600") {
          newGame(600)
        })
        contents += new MenuItem(Action("700 x 700") {
          newGame(700)
        })
        contents += new MenuItem(Action("800 x 800") {
          newGame(800)
        })
        contents += new MenuItem(Action("900 x 900") {
          newGame(900)
        })
      }
    }
    
    //listenTo(buttonStartGame)
    listenTo(buttonPause)
    listenTo(buttonAddFollower)
    listenTo(buttonRemoveFollower)
    listenTo(gameArea.keys)
    listenTo(gameArea.mouse.clicks)
      
    reactions += {
      case ButtonClicked(`buttonPause`) => pause()
      case ButtonClicked(`buttonAddFollower`) => {
        if (gameWorld.addFollower()) {
          textArea.text = "Follower added"
        }
        else {
          textArea.text = ""
        }
      }
      case ButtonClicked(`buttonRemoveFollower`) => {
        if (gameWorld.removeFollower()) {
          textArea.text = "Follower removed"
        }
        else {
          textArea.text = ""
        }
      }
      case MouseClicked(_, p, _, _, _) => {
        if (gameState == 1)
        gameWorld.manualTarget(p.x, p.y)
      }
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
    	  gameState = 2
    	  textArea.text = "Game paused"
      }
      else if (gameState == 2){
        gameTimer.restart()
        gameState = 1
        textArea.text = "Game restarted"
      }
      else {
        textArea.text = ""
      }
    }
    
    def newGame(size: Int) = {
      val arena  = new Arena(size, size)
      gameArea = arena
      contents = new BorderPanel {
        layout(buttons) = BorderPanel.Position.North
        layout(arena) = BorderPanel.Position.Center
        layout(textArea) = BorderPanel.Position.South
      }
      listenTo(gameArea.mouse.clicks)
      gameWorld = new World(size, size)
      gameWorld.createInitialShips()
      gameState = 1
      textArea.text = "New game started"
      gameTimer.restart()
    }
    
    val gameUpdater = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        gameWorld.step()
        gameArea.repaint() 
      }  
    }
    
    val gameTimer = new javax.swing.Timer(6, gameUpdater)
    gameTimer.start()
    
    //peer.setLocationRelativeTo(null) center frame
  }



object Game extends SimpleSwingApplication {
  
  val width      = 600
  val height     = 600
  var gameWidth = 0
  var gameHeight = 0
  
  var gameWorld = new World(height, width)
  
  /*
  GameState = 0, means game is not-started
  GameState = 1, means game is started and running
  GameState = 2, means game is started but stopped
   */
  var gameState = 0
  
  def top = new Canvas(gameState, gameWorld, height, width)
    
  
  
}