package seuraaJohtajaa

import scala.swing._
import scala.swing.event._
import Swing._
import scala.swing.Dialog._
//import swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import scala.collection.mutable.Buffer
import java.awt.event.ActionListener
import java.awt.geom.Ellipse2D





class Canvas(var gameState: Int, var gameWorld: World, height: Int, width: Int) extends MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    
    var gameArea = new Arena(width, height)
    var count = 0
    var countNumber = 4
    var maxVelocity = 2.0
    var mass = 70
    
    
    //minimumSize   = new Dimension(width, height)
    //preferredSize = new Dimension(mainFrameWidth,mainFrameHeight)
    //maximumSize   = new Dimension(mainFrameWidth,mainFrameHeight)
    
    val buttonPause = new Button("Pause") { font = new Font("Arial", 0, 16)}
    val buttonAddFollower = new Button("Add follower") { font = new Font("Arial", 0, 16)}
    val buttonRemoveFollower = new Button("Remove follower") { font = new Font("Arial", 0, 16)}
    
    val buttons = new FlowPanel {
      background = Color.white
      border = EmptyBorder(5, 5, 5, 5)
      contents += buttonPause
      //contents += buttonChangeMaxSpeed
      contents += buttonAddFollower
      contents += buttonRemoveFollower
    }
    
    val changeMaxSpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Max speed: " + maxVelocity.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeDisplaySpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Display speed: " + countNumber.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeMassdText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Mass: " + countNumber.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val buttonChangeMaxSpeed = new Button("Ship max speed") { font = new Font("Arial", 0, 16)} 
    val buttonChangeTimerCounter = new Button("Display speed") { font = new Font("Arial", 0, 16)}
    
    val changeFields = new BoxPanel(Orientation.Vertical) {
      //border = LineBorder(5, 5, 5, 5)
      border = LineBorder(Color.black)
      background = Color.white
      contents += VStrut(10)
      contents += new Label("Change parameters") {
        font = new Font("Arial", 0, 18)
        border = EmptyBorder(0,0,0,50)
        }
      contents += VStrut(20)
      contents += buttonChangeMaxSpeed
      contents += VStrut(10)
      contents += buttonChangeTimerCounter
      contents += VStrut(200)
      contents += changeMaxSpeedText
      contents += VStrut(10)
      contents += changeDisplaySpeedText
    }
    
     
    
    class Arena(val width: Int, height: Int) extends Panel {
      
      preferredSize = new Dimension(width, height)
      
      //border = LineBorder(Color.BLACK)
      border = LineBorder(Color.black)
      
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
    
    val commandText = new TextArea(1, 1) {
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
    
    /*val userInputs = new BorderPanel {
      layout(buttons) = BorderPanel.Position.North
      layout(changeFields) = BorderPanel.Position.South
    }*/
    
    contents = new BorderPanel {
      //layout(userInputs) = BorderPanel.Position.North
      layout(buttons) = BorderPanel.Position.North
      layout(changeFields) = BorderPanel.Position.East
      layout(gameArea) = BorderPanel.Position.West
      layout(commandText) = BorderPanel.Position.South
      //border = LineBorder(Color.BLACK)
    }
    //contents = borderPanel
    
    
    
    menuBar = new MenuBar {
      contents += new Menu("File") {
        { font = new Font("Arial", 0, 16)}
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
          font = new Font("Arial", 0, 16)
        })
      }
      contents += new Menu("New Game") {
        { font = new Font("Arial", 0, 16)}
        contents += new MenuItem(Action("500 x 500") {
          newGame(500)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("600 x 600") {
          newGame(600)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("700 x 700") {
          newGame(700)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("800 x 800") {
          newGame(800)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("900 x 900") {
          newGame(900)
          font = new Font("Arial", 0, 16)
        })
      }
    }
    
    //listenTo(buttonStartGame)
    listenTo(buttonPause)
    listenTo(buttonChangeMaxSpeed)
    listenTo(buttonChangeTimerCounter)
    listenTo(buttonAddFollower)
    listenTo(buttonRemoveFollower)
    listenTo(gameArea.keys)
    listenTo(gameArea.mouse.clicks)
      
    reactions += {
      case ButtonClicked(`buttonPause`) => pause()
      case ButtonClicked(`buttonChangeMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New max speed: (0-4)", "Select new max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble > 0) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            //println(inputLine,3)
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0 and 4.0" + "\n" + "New max speed", "Select new max speed", Message.Error, Swing.EmptyIcon, Nil, "")
            //println(inputLine,1)
            /*if (isDouble(inputLine)) {
              val doubleValue = inputLine.get.toDouble  
              if (isDouble(inputLine) && doubleValue >=0 && doubleValue <= 4) {
                //println(inputLine,2)
                maxVelocity = doubleValue
                changeMaxSpeedText.text = "Maxspeed: " + maxVelocity.toString
                gameWorld.maxVelocity = maxVelocity
                commandText.text = "Maxspeed changed to " + maxVelocity.toString
              } 
            }*/
                
          }
        }
        
        if (isDouble(inputLine)) {
          //println(inputLine,2)
          maxVelocity = inputLine.get.toDouble
          changeMaxSpeedText.text = "Max speed: " + maxVelocity.toString
          gameWorld.maxVelocity = maxVelocity
          commandText.text = "Maxspeed changed to " + maxVelocity.toString
        }
        
      }
      
      case ButtonClicked(`buttonChangeTimerCounter`) => {
        var inputLine = showInput(contents.head, "New display speed: (1-4)", "Select new display update frequency", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isInt(inputLine) || !(inputLine.get.toInt >= 1) || !(inputLine.get.toInt <= 4))) {
          if (inputLine.exists(_ == None)) {
            //println(inputLine,3)
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter an int between 1 and 4" + "\n" + "New display speed", "Select new display update frequency", Message.Error, Swing.EmptyIcon, Nil, "")   
          }
        }
        
        if (isInt(inputLine)) {
          //println(inputLine,2)
          count = 0
          countNumber = inputLine.get.toInt
          changeDisplaySpeedText.text = "Display speed: " + countNumber.toString
          commandText.text = "Display speed changed to " + countNumber.toString
        }
        
      }
      case ButtonClicked(`buttonAddFollower`) => {
        if (gameWorld.addFollower()) {
          commandText.text = "Follower added"
        }
        else {
          commandText.text = ""
        }
      }
      case ButtonClicked(`buttonRemoveFollower`) => {
        if (gameWorld.removeFollower()) {
          commandText.text = "Follower removed"
        }
        else {
          commandText.text = ""
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
    
    private def isDouble(inputLine: Option[String]) = {
      //println(inputLine)
      try {
          val dummyVariable = inputLine.get.toDouble
          true
          /*if (dummyVariable >= 0 && dummyVariable <= 4.0)
            true
          else {
            false
          }*/
          
        } 
      catch {
        case e: Exception => { 
          false  
        }
      }
    }
    
    
    private def isInt(inputLine: Option[String]) = {
      //println(inputLine)
      try {
          val dummyVariable = inputLine.get.toInt
          true
          /*if (dummyVariable >= 0 && dummyVariable <= 4.0)
            true
          else {
            false
          }*/
          
        } 
      catch {
        case e: Exception => { 
          false  
        }
      }
    }
    
    
    private def pause(): Unit = {
      if (gameState == 1) {
    	  gameTimer.stop()
    	  gameState = 2
    	  commandText.text = "Game paused"
      }
      else if (gameState == 2){
        gameTimer.restart()
        gameState = 1
        commandText.text = "Game restarted"
      }
      else {
        commandText.text = ""
      }
    }
    
    def newGame(size: Int) = {
      val arena  = new Arena(size, size)
      gameArea = arena
      contents = new BorderPanel {
        layout(buttons) = BorderPanel.Position.North
        //layout(arena) = BorderPanel.Position.Center
        layout(changeFields) = BorderPanel.Position.East
        layout(gameArea) = BorderPanel.Position.West
        layout(commandText) = BorderPanel.Position.South
      }
      
      
      
      listenTo(gameArea.mouse.clicks)
      gameWorld = new World(size, size, maxVelocity)
      gameWorld.createInitialShips()
      gameState = 1
      commandText.text = "New game started"
      gameTimer.restart()
    }
    
    val gameUpdater = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        count += 1
        if (count == countNumber) {
          count = 0
          gameWorld.step()
          gameArea.repaint() 
        }
      }  
    }
    
    val gameTimer = new javax.swing.Timer(1, gameUpdater)
    gameTimer.start()
    
    //peer.setLocationRelativeTo(null) center frame
  }



object Game extends SimpleSwingApplication {
  
  val width      = 600
  val height     = 600
  var gameWidth = 0
  var gameHeight = 0
  
  var gameWorld = new World(height, width, 2.0)
  
  /*
  GameState = 0, means game is not-started
  GameState = 1, means game is started and running
  GameState = 2, means game is started but stopped
   */
  var gameState = 0
  
  def top = new Canvas(gameState, gameWorld, height, width)
    
  
  
}