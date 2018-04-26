package seuraaJohtajaa

import scala.swing._
import scala.swing.event._
import Swing._
import scala.swing.Dialog._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import scala.collection.mutable.Buffer
import java.awt.event.ActionListener
import java.awt.geom.Ellipse2D



//luokka uuden pääikkunan luomiseksi
class Canvas(var gameState: Int, var gameWorld: World, defaultHeight: Int, defaultWidth: Int, defaultMaxVelocity: Double, defaultMass: Double) extends MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    
    var gameArea = new Arena(defaultWidth, defaultHeight)
    var maxVelocity = defaultMaxVelocity
    var mass = defaultMass
    var displayDelay = 4
    
    
    //minimumSize   = new Dimension(width, height)
    //preferredSize = new Dimension(mainFrameWidth,mainFrameHeight)
    //maximumSize   = new Dimension(mainFrameWidth,mainFrameHeight)
    
    
    /*
     * Nappuloita käyttöliittymän yläosassa
     * 
     * buttonPause, keskeyttää simulaation tai uudelleen käynnistää sen
     * buttonAddFollower, lisää uuden seuraajan simulaatioon, mikäli niitä ei ole vielä maksimimäärää
     * buttonRemoveFollower, poistaa seuraajan simulaatiosta
     */
    val buttonPause = new Button("Pause") { font = new Font("Arial", 0, 16)}
    val buttonAddFollower = new Button("Add follower") { font = new Font("Arial", 0, 16)}
    val buttonRemoveFollower = new Button("Remove follower") { font = new Font("Arial", 0, 16)}
    val buttonChangeMode = new Button("Change mode") { font = new Font("Arial", 0, 16)}
    
    val buttons = new FlowPanel {
      background = Color.white
      border = EmptyBorder(5, 5, 5, 5)
      contents += buttonPause
      contents += buttonAddFollower
      contents += buttonRemoveFollower
      contents += buttonChangeMode
    }
    
    /*
     * Naappuloita käyttöliittymän oikeassa laidassa parametrien muuttamiseksi
     * 
     * buttonChangeShipMaxSpeed, muutetaan alusten maksiminopeutta
     * buttonChangeFollowerMaxSpeed, muutetaan seuraajan maksiminopeutta
     * buttonChangeLeaderMaxSpeed, muutetaan johtajan maksiminopeutta
     * buttonChangeDisplayDelay, muutetaan simulaation päivitysnopeutta 
     * buttonChangeShipMass, muutetaan alusten massaa eli käytännössä kiihtyvyyttä
     */
    val buttonChangeShipMaxSpeed = new Button("Ship max speed") { font = new Font("Arial", 0, 16)}
    val buttonChangeFollowerMaxSpeed = new Button("Follower max speed") { font = new Font("Arial", 0, 16)} 
    val buttonChangeLeaderMaxSpeed = new Button("Leader max speed") { font = new Font("Arial", 0, 16)} 
    val buttonChangeDisplayDelay = new Button("Display delay") { font = new Font("Arial", 0, 16)}
    val buttonChangeShipMass = new Button("Ship mass") { font = new Font("Arial", 0, 16)}
    
    val changeFields = new BoxPanel(Orientation.Vertical) {
      border = EmptyBorder(20)
      background = Color.white
      contents += VStrut(10)
      contents += new Label("Change parameters") {
        font = new Font("Arial", 0, 18)
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
      contents += buttonChangeDisplayDelay
    }
    
     
    //käyttöliittymän osa, jossa simulaatio tapahtuu
    class Arena(width: Int, height: Int) extends Panel {
      
      preferredSize = new Dimension(width, height)
      minimumSize   = new Dimension(width, height)
      maximumSize   = new Dimension(width,height)
      border = LineBorder(Color.black)
      
      override def paintComponent(g: Graphics2D) = {
        
        if (gameState == 0) {
          g.setColor(new Color(255, 255, 255))
          g.fillRect(0, 0, width, height)
        }

        //Piirretään valkoisella vanhan kuvan päälle suorakaide
        g.setColor(new Color(255, 255, 255))
        g.fillRect(0, 0, width, height)

        //Pyydetään graphics:ilta siloiteltua grafiikkaa ns. antialiasointia
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)          

        //Piirretään pelimaailma
        gameWorld.draw(g)
        
      }
   
    } 
    
    //tekstikenttä käyttölittymän alareunassa, johon päivitetään muuttuneet parametrit ja pelin tila
    val commandText = new TextArea(1, 1) {
      editable = false
      border = EmptyBorder(10)
      font = new Font("Arial", 0, 18)
      
    }
    
    //tekstikenttä käyttölittymän alareunassa, simulaation alapuolella, jossa kerrotaan parametrien arvot
    val printValuesText = new TextArea(1, 1) {
      editable = false
      border = EmptyBorder(10)
      font = new Font("Arial", 0, 18)
      text = "Leader max speed: " + gameWorld.leaderMaxVelocity.toString + 
        " | Follower max speed: " + gameWorld.followerMaxVelocity.toString +
        " | Ship mass: " + gameWorld.mass.toString +
        " | Display delay: " + displayDelay.toString
    }
    
    
    val userInputValuesText = new BoxPanel(Orientation.Vertical) {
      border = EmptyBorder(10)
      background = Color.white
      contents += printValuesText
      contents += commandText
    }
    
    
    //eri kenttien asettelu käyttöliittymässä
    contents = new BorderPanel {
      layout(buttons) = BorderPanel.Position.North
      layout(changeFields) = BorderPanel.Position.East
      layout(gameArea) = BorderPanel.Position.West
      layout(userInputValuesText) = BorderPanel.Position.South
    }
    
    /*
     * Käyttöliittymän valikko 
     * 
     * Ohjelma suljetaan kohdasta File -> Exit
     * 
     * Simulaatio käynnistetään kohdasta New Game
     * ja valitsemalla ruutukoko
     */
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
    
    //tapahtummien "kuuntelu"
    listenTo(buttonPause)
    listenTo(buttonChangeShipMaxSpeed)
    listenTo(buttonChangeLeaderMaxSpeed)
    listenTo(buttonChangeFollowerMaxSpeed)
    listenTo(buttonChangeShipMass)
    listenTo(buttonChangeDisplayDelay)
    listenTo(buttonAddFollower)
    listenTo(buttonRemoveFollower)
    listenTo(buttonChangeMode)
    listenTo(gameArea.mouse.clicks)
      
    //tapahtumiin reagointi
    reactions += {
      case ButtonClicked(`buttonPause`) => pause()
      case ButtonClicked(`buttonChangeShipMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New ship max speed: (0.1 <= x <= 4)", "Select new ship max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble >= 0.1) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0.1 and 4.0" + "\n" + "New ship max speed (0.1 <= x <= 4)", "Select new ship max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        //alusten maksiminopeutta muutetaan vain, jos on annettu double-tyyppinen arvo
        if (isDouble(inputLine)) {
          maxVelocity = "%.1f".format(inputLine.get.toDouble).toDouble//inputLine.get.toDouble
          gameWorld.maxVelocity = maxVelocity
          gameWorld.followerMaxVelocity = maxVelocity
          gameWorld.leaderMaxVelocity = maxVelocity
          commandText.text = "Ship max speed changed to " + maxVelocity.toString
          updatePrintValues()
        }
        
      }
      case ButtonClicked(`buttonChangeLeaderMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New leader max speed: (0.1 <= x <= 4)", "Select new leader max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble >= 0.1) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0.1 and 4.0" + "\n" + "New leader max speed: (0.1 <= x <= 4)", "Select new leader max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        //johtajan maksiminopeutta muutetaan vain, jos on annettu double-tyyppinen arvo
        if (isDouble(inputLine)) {
          val maxLeaderVelocity = "%.1f".format(inputLine.get.toDouble).toDouble//inputLine.get.toDouble
          gameWorld.leaderMaxVelocity = maxLeaderVelocity
          
          //seuraajien nopeus ei voi olla alle johtaja-aluksen nopeuden
          if (gameWorld.followerMaxVelocity < maxLeaderVelocity) {
            maxVelocity = maxLeaderVelocity
            gameWorld.maxVelocity = maxVelocity
            commandText.text = "Ship max speed changed to " + maxLeaderVelocity.toString
            gameWorld.followerMaxVelocity = maxLeaderVelocity
            
          }
          else {
            commandText.text = "Leader max speed changed to " + maxLeaderVelocity.toString
          }
          updatePrintValues()
        }
      }
      case ButtonClicked(`buttonChangeFollowerMaxSpeed`) => {
        var inputLine = showInput(contents.head, "New follower max speed: (0.1 <= x <= 4)", "Select new follower max speed", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble >= 0.1) || !(inputLine.get.toDouble <= 4))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 0.1 and 4.0" + "\n" + "New follower max speed: (0.1 <= x <= 4)", "Select new follower max speed", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        //seuraajien maksiminopeutta muutetaan vain, jos on annettu double-tyyppinen arvo
        if (isDouble(inputLine)) {
          val maxFollowerVelocity = "%.1f".format(inputLine.get.toDouble).toDouble// inputLine.get.toDouble
          gameWorld.followerMaxVelocity = maxFollowerVelocity
          
          //johtaja-aluksen nopeu ei voi ylittää seuraajien maksiminopeutta
          if (gameWorld.leaderMaxVelocity > maxFollowerVelocity) {
            gameWorld.leaderMaxVelocity = maxFollowerVelocity
            maxVelocity = maxFollowerVelocity
            gameWorld.maxVelocity = maxVelocity
            commandText.text = "Ship max speed changed to " + maxFollowerVelocity.toString
          }
          else {
            commandText.text = "Follower max speed changed to " + maxFollowerVelocity.toString
          }
          updatePrintValues()
        }
      }
      case ButtonClicked(`buttonChangeShipMass`) => {
        var inputLine = showInput(contents.head, "New ship mass: (30 <= x <= 70)", "Select new ship mass", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isDouble(inputLine) || !(inputLine.get.toDouble >= 30) || !(inputLine.get.toDouble <= 70))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter a double between 30 and 70" + "\n" + "New ship mass: (30 <= x <= 70)", "Select new ship mass", Message.Error, Swing.EmptyIcon, Nil, "")    
          }
        }
        
        //alusten massaa muutetaan vain, jos on annettu double-tyyppinen arvo
        if (isDouble(inputLine)) {
          mass = "%.1f".format(inputLine.get.toDouble).toDouble
          gameWorld.mass = mass
          commandText.text = "Ship mass changed to " + mass.toString
          gameWorld.newMaxVelChange()
          updatePrintValues()
        }
      }
      case ButtonClicked(`buttonChangeDisplayDelay`) => {
        var inputLine = showInput(contents.head, "New display delay: (1 <= x <= 10)", "Select new display delay", Message.Question, Swing.EmptyIcon, Nil, "")
        while (!(inputLine == None) && (!isInt(inputLine) || !(inputLine.get.toInt >= 1) || !(inputLine.get.toInt <= 10))) {
          if (inputLine.exists(_ == None)) {
            Unit
          }
          else {
            inputLine = showInput(contents.head, "Please enter an int between 1 and 10" + "\n" + "New display delay: (1 <= x <= 10)", "Select new display delay", Message.Error, Swing.EmptyIcon, Nil, "")   
          }
        }
        
        //ruuden päivitysnopeutta muutetaan vain, jos on annettu int-tyyppinen arvo
        if (isInt(inputLine)) {
          displayDelay = inputLine.get.toInt
          gameTimer.setInitialDelay(displayDelay)
          gameTimer.setDelay(displayDelay)
          if (gameTimer.isRunning) {
            gameTimer.restart()
          }
           
          commandText.text = "Display delay changed to " + displayDelay.toString
          updatePrintValues()
        }
        
      }
      case ButtonClicked(`buttonAddFollower`) => {
        if (gameState != 0 && gameWorld.addFollower()) {
          commandText.text = "Follower added"
        }
        else if (gameState != 0){
          commandText.text = ""
        }
      }
      case ButtonClicked(`buttonRemoveFollower`) => {
        if (gameState != 0 && gameWorld.removeFollower()) {
          commandText.text = "Follower removed"
        }
        else if (gameState != 0){
          commandText.text = ""
        }
      }
      case ButtonClicked(`buttonChangeMode`) => {
        if (gameState != 0 && gameWorld.inFleetMode) {
          commandText.text = "Game mode changed to queue mode"
          gameWorld.inFleetMode = false
        }
        else if (gameState != 0) {
          commandText.text = "Game mode changed to fleet mode"
          gameWorld.inFleetMode = true
        }
      }
      case MouseClicked(_, p, _, _, _) => {
        if (gameState == 1)
        gameWorld.manualTarget(p.x, p.y)
      }
      
    }
    
    //tarkistetaan onko arvo tyyppiä double
    private def isDouble(inputLine: Option[String]) = {
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
    
    //tarkastetaan onko arvo tyyppiä int
    private def isInt(inputLine: Option[String]) = {
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
    
    //pysäytetään simulaatio
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
    }
    
    //päivitetään ruudulle muuttuneet parametrien arvot
    private def updatePrintValues() = {
     printValuesText.text = "Leader max speed: " + gameWorld.leaderMaxVelocity.toString + 
        " | Follower max speed: " + gameWorld.followerMaxVelocity.toString +
        " | Ship mass: " + gameWorld.mass.toString +
        " | Display delay: " + displayDelay.toString
    }
    
    //luodaan uusi simulaatio
    def newGame(width: Int, height: Int) = {
      val arena  = new Arena(width, height)
      gameArea = arena
      contents = new BorderPanel {
        layout(buttons) = BorderPanel.Position.North
        layout(changeFields) = BorderPanel.Position.East
        layout(gameArea) = BorderPanel.Position.West
        layout(userInputValuesText) = BorderPanel.Position.South
      }
      
      listenTo(gameArea.mouse.clicks)
      gameWorld = new World(height, width, maxVelocity, mass)
      gameWorld.createInitialShips()
      updatePrintValues()
      gameState = 1
      commandText.text = "New game started"
      gameTimer.restart()
    }
    
    //päivitetään simulaation alusten paikat ja piirretään ne tapahtumankäsittelijällä
    val gameUpdater = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        gameWorld.step()
        gameArea.repaint() 
        
      }  
    }
    
    //ajastin pelin päivitystä varten
    val gameTimer = new javax.swing.Timer(displayDelay, gameUpdater)
    gameTimer.start()
    
    
  }


//peli-olio, joka käynnistää ohjelman
object Game extends SimpleSwingApplication {
  
  //alkuarvot kun ohjelma käynnistetään
  val defaultWidth      = 600
  val defaultHeight     = 600
  val defaultMaxVelocity = 2.0
  val defaultMass = 50
  
  //luodaan uusi pelimaailma
  var gameWorld = new World(defaultHeight, defaultWidth, defaultMaxVelocity, defaultMass)
  
  /*
  GameState = 0, means game is not-started
  GameState = 1, means game is started and running
  GameState = 2, means game is started but stopped
   */
  
  //alussa peliä ei ole vielä käynnistetty
  var gameState = 0
  
  def top = new Canvas(gameState, gameWorld, defaultHeight, defaultWidth, defaultMaxVelocity, defaultMass)
    
  
  
}