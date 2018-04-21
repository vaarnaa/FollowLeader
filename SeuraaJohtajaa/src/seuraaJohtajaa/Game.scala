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
    var countNumber = 3
    var maxVelocity = 2.0
    var mass = 60.0
    
    
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
      contents += buttonAddFollower
      contents += buttonRemoveFollower
    }
    
    val changeShipMaxSpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Ship max speed: " + maxVelocity.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeDisplaySpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Display speed: " + countNumber.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeFollowerMaxSpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Follower max speed: " + maxVelocity.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeLeaderMaxSpeedText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Leader max speed: " + maxVelocity.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val changeShipMassText = new TextArea(1,1) {
       border = EmptyBorder(10, 10, 10, 0)
       text = "Mass: " + mass.toString
       font = new Font("Arial", 0, 18)
       editable = false
      }
    
    val buttonChangeShipMaxSpeed = new Button("Ship max speed") { font = new Font("Arial", 0, 16)}
    val buttonChangeFollowerMaxSpeed = new Button("Follower max speed") { font = new Font("Arial", 0, 16)} 
    val buttonChangeLeaderMaxSpeed = new Button("Leader max speed") { font = new Font("Arial", 0, 16)} 
    val buttonChangeTimerCounter = new Button("Display speed") { font = new Font("Arial", 0, 16)}
    val buttonChangeShipMass = new Button("Ship mass") { font = new Font("Arial", 0, 16)}
    
    val changeTextFields = new FlowPanel() {
      contents += changeShipMaxSpeedText
      contents += VStrut(10)
      contents += changeLeaderMaxSpeedText
      contents += VStrut(10)
      contents += changeFollowerMaxSpeedText
      contents += VStrut(10)
      contents += changeShipMassText
      contents += VStrut(10)
      contents += changeDisplaySpeedText
    }
    
    val changeFields = new BoxPanel(Orientation.Vertical) {
      //border = LineBorder(5, 5, 5, 5)
      border = LineBorder(Color.black)
      border = EmptyBorder(20)
      background = Color.white
      contents += VStrut(10)
      contents += new Label("Change parameters") {
        font = new Font("Arial", 0, 18)
        //border = EmptyBorder(0,0,0,100)
        border = EmptyBorder(20)
        }
      contents += VStrut(15)
      contents += buttonChangeShipMaxSpeed
      contents += VStrut(10)
      contents += buttonChangeLeaderMaxSpeed
      contents += VStrut(10)
      contents += buttonChangeFollowerMaxSpeed
      contents += VStrut(10)
      contents += buttonChangeShipMass
      contents += VStrut(10)
      contents += buttonChangeTimerCounter
      contents += VStrut(10)
      //contents += changeTextFields
      /*contents += changeShipMaxSpeedText
      contents += VStrut(10)
      contents += changeLeaderMaxSpeedText
      contents += VStrut(10)
      contents += changeFollowerMaxSpeedText
      contents += VStrut(10)
      contents += changeShipMassText
      contents += VStrut(10)
      contents += changeDisplaySpeedText*/
    }
    
     
    
    class Arena(width: Int, height: Int) extends Panel {
      
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
    
    contents = new BorderPanel {
      layout(buttons) = BorderPanel.Position.North
      layout(changeFields) = BorderPanel.Position.East
      layout(gameArea) = BorderPanel.Position.West
      layout(commandText) = BorderPanel.Position.South
      //layout(changeTextFields) = BorderPanel.Position.South
      //border = LineBorder(Color.BLACK)
    }
    
    
    
    
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
          newGame(500, 500)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("600 x 600") {
          newGame(600, 600)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("900 x 600") {
          newGame(900, 600)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("700 x 700") {
          newGame(700, 700)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("800 x 800") {
          newGame(800, 800)
          font = new Font("Arial", 0, 16)
        })
        contents += new MenuItem(Action("1200 x 800") {
          newGame(1200, 800)
          font = new Font("Arial", 0, 16)
        })
      }
    }
    
    //listenTo(buttonStartGame)
    listenTo(buttonPause)
    listenTo(buttonChangeShipMaxSpeed)
    listenTo(buttonChangeLeaderMaxSpeed)
    listenTo(buttonChangeFollowerMaxSpeed)
    listenTo(buttonChangeShipMass)
    listenTo(buttonChangeTimerCounter)
    listenTo(buttonAddFollower)
    listenTo(buttonRemoveFollower)
    listenTo(gameArea.keys)
    listenTo(gameArea.mouse.clicks)
      
    reactions += {
      case ButtonClicked(`buttonPause`) => pause()
      case ButtonClicked(`buttonChangeShipMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New ship max speed: (0-4)", "Select new ship max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble > 0) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0 and 4.0" + "\n" + "New ship max speed", "Select new ship max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        if (isDouble(inputLine)) {
          maxVelocity = "%.1f".format(inputLine.get.toDouble).toDouble//inputLine.get.toDouble
          changeShipMaxSpeedText.text = "Ship max speed: " + maxVelocity.toString
          changeLeaderMaxSpeedText.text = "Leader max speed: " + maxVelocity.toString
          changeFollowerMaxSpeedText.text = "Follower max speed: " + maxVelocity.toString
          gameWorld.maxVelocity = maxVelocity
          gameWorld.followerMaxVelocity = maxVelocity
          gameWorld.leaderMaxVelocity = maxVelocity
          commandText.text = "Ship max speed changed to " + maxVelocity.toString
        }
        
      }
      case ButtonClicked(`buttonChangeLeaderMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New leader max speed: (0-4)", "Select new leader max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble > 0) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0 and 4.0" + "\n" + "New leader max speed", "Select new leader max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        if (isDouble(inputLine)) {
          val maxLeaderVelocity = "%.1f".format(inputLine.get.toDouble).toDouble//inputLine.get.toDouble
          changeLeaderMaxSpeedText.text = "Leader max speed: " + maxLeaderVelocity.toString
          gameWorld.leaderMaxVelocity = maxLeaderVelocity
          
          if (gameWorld.followerMaxVelocity < maxLeaderVelocity) {
            gameWorld.followerMaxVelocity = maxLeaderVelocity
            changeFollowerMaxSpeedText.text = "Follower max speed: " + maxLeaderVelocity.toString
            commandText.text = "Ship max speed changed to " + maxLeaderVelocity.toString
          }
          else {
            commandText.text = "Leader max speed changed to " + maxLeaderVelocity.toString
          }
        }
      }
      case ButtonClicked(`buttonChangeFollowerMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New follower max speed: (0-4)", "Select new follower max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble > 0) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0 and 4.0" + "\n" + "New follower max speed", "Select new follower max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        if (isDouble(inputLine)) {
          val maxFollowerVelocity = "%.1f".format(inputLine.get.toDouble).toDouble// inputLine.get.toDouble
          changeFollowerMaxSpeedText.text = "Follower max speed: " + maxFollowerVelocity.toString
          gameWorld.followerMaxVelocity = maxFollowerVelocity
          
          if (gameWorld.leaderMaxVelocity > maxFollowerVelocity) {
            gameWorld.leaderMaxVelocity = maxFollowerVelocity
            changeLeaderMaxSpeedText.text = "Leader max speed: " + maxFollowerVelocity.toString
            commandText.text = "Ship max speed changed to " + maxFollowerVelocity.toString
          }
          else {
            commandText.text = "Follower max speed changed to " + maxFollowerVelocity.toString
          }
        }
      }
      case ButtonClicked(`buttonChangeShipMass`) => {
        var inputLine = showInput(contents.head, "New ship mass: (30-100)", "Select new ship mass", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble >= 30) || !(inputLine.get.toDouble <= 100))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 30 and 100" + "\n" + "New ship mass", "Select new ship mass", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        if (isDouble(inputLine)) {
          mass = "%.1f".format(inputLine.get.toDouble).toDouble
          changeShipMassText.text = "Ship mass: " + mass.toString
          gameWorld.mass = mass
          commandText.text = "Ship mass changed to " + mass.toString
          gameWorld.newMaxVelChange()
        }
      }
      case ButtonClicked(`buttonChangeTimerCounter`) => {
        var inputLine = showInput(contents.head, "New display speed: (1-4)", "Select new display update frequency", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isInt(inputLine) || !(inputLine.get.toInt >= 1) || !(inputLine.get.toInt <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter an int between 1 and 4" + "\n" + "New display speed", "Select new display update frequency", Message.Error, Swing.EmptyIcon, Nil, "")   
          }
        }
        
        if (isInt(inputLine)) {
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
    
    def newGame(width: Int, height: Int) = {
      val arena  = new Arena(width, height)
      gameArea = arena
      contents = new BorderPanel {
        layout(buttons) = BorderPanel.Position.North
        //layout(arena) = BorderPanel.Position.Center
        layout(changeFields) = BorderPanel.Position.East
        layout(gameArea) = BorderPanel.Position.West
        layout(commandText) = BorderPanel.Position.South
      }
      
      
      
      listenTo(gameArea.mouse.clicks)
      gameWorld = new World(height, width, maxVelocity, mass)
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
  
  var gameWorld = new World(height, width, 2.0, 60)
  
  /*
  GameState = 0, means game is not-started
  GameState = 1, means game is started and running
  GameState = 2, means game is started but stopped
   */
  var gameState = 0
  
  def top = new Canvas(gameState, gameWorld, height, width)
    
  
  
}