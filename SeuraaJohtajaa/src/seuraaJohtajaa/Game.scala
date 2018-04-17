package seuraaJohtajaa

import scala.swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import scala.collection.mutable.Buffer
import java.awt.event.ActionListener
import java.awt.geom.Ellipse2D

object Game extends SimpleSwingApplication {
  
  val width      = 800
  val height     = 800
  
  val gameWorld = new World(height, width)
  
  def top = new MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    
    minimumSize   = new Dimension(width,height)
    preferredSize = new Dimension(width,height)
    maximumSize   = new Dimension(width,height)
    
    
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
    
    contents = arena
    
    /*while(true) {
      
    }*/
    
    val listener = new ActionListener(){
      def actionPerformed(e : java.awt.event.ActionEvent) = {
        gameWorld.step()
        arena.repaint() 
      }  
    }
    
    val timer = new javax.swing.Timer(6, listener)
    timer.start()
    
    
  }
  
}