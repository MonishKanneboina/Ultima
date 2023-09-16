/**
 * Ultima 0.1 Main game loop
 * 
 * @author Keith Vertanen
 * @author Michele Van Dyne - added commenting
 *
 */
public class Ultima {   

   /**
    * The main method for the Ultima game loop
    */
   public static void main(String [] args)  {
      final String level = "40x40.txt"; //change level file here
      final int SLEEP_MS = 100;         
   
      World world = new World(level);
      StdDraw.show(0);
      world.draw();
   
      // Keep looping as long as avatar hasn't died
      while (world.avatarAlive() && world.getNumMonsters() > 0) {
         //See if a move has been requested & process it
         world.handleKeyPress();          
         // Redraw everything and then sleep for a bit
         StdDraw.clear();
         world.draw();
         StdDraw.show(SLEEP_MS);         
      }       
   
      if (world.getNumMonsters() == 0) 
         System.out.println("You win!");
      else 
         System.out.println("You lost!");
   }
}
