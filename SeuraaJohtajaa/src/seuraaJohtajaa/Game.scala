package seuraaJohtajaa

import scala.swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}

object Game extends SimpleSwingApplication {
  
  val width      = 800
  val height     = 800
  
  def top = new MainFrame {
    
    title     = "Seuraa johtajaa"
    resizable = false
    
    minimumSize   = new Dimension(width,height)
    preferredSize = new Dimension(width,height)
    maximumSize   = new Dimension(width,height)
    
    val arena = new Panel {
      
      override def paintComponent(g: Graphics2D) = {

        // Piirretään valkoisella vanhan kuvan päälle suorakaide
        g.setColor(new Color(255, 25, 255))
        g.fillRect(0, 0, width, height)

        // Pyydetään graphics:ilta siloiteltua grafiikkaa ns. antialiasointia
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)          

        // vaihdetaan piirtoväriksi valkoinen ja pyydetään avaruutta pirtämään itsensä
        g.setColor(Color.white)
        World.draw(g) 
      }
      
    }
    
  }
  
}