import java.util.*;
import java.io.*;

/**
 * Class to set up the world for game play in Ultima
 * 
 * @author Keith Vertanen
 * @author Michele Van Dyne - eliminated use of StdIn, added commenting
 * @author Kris McCoy - changed key input handling to reduce input buffering
 * @author Dagar Rehan - Added preferred window sizing with view window offset
 * @author YOUR NAME HERE!! - Added recursive torch ligthing method   
 *
 */
public class World {
   public static int offSetX = 0;            
   public static int offSetY = 0;
   public final static double DISPLAY_DAMAGE_SEC = 1.0;    // How long to display health after damage     

   private Tile [][] tiles     = null;         // Stores all the tiles in a 2D array
   private int width           = 0;            // Stores the width, first dimension in array
   private int height          = 0;            // Stores the height, second dimension in array
   private Avatar avatar       = null;         // Where the player is

   private int preferredWindowSizeX = 20;
   private int preferredWindowSizeY = 20;
   private int windowSizeX;
   private int windowSizeY;
   

   private ArrayList<Monster> monsters = new ArrayList<Monster>();     // Holds the monster objects
   
   /**
    * Constructor for the world class
    * @param filename - the String name of a file that will hold the configuration parameters for the world
    */
   public World(String filename)
   {
      try
      {
         Scanner scan = new Scanner(new File(filename));
      
         // First two lines specify the size of the world
         width   = scan.nextInt();
         height  = scan.nextInt();
      
         windowSizeX = Math.min(preferredWindowSizeX, width);
         windowSizeY = Math.min(preferredWindowSizeY, height);
      
         // Read in the avatar data from file
         avatar = new Avatar(scan.nextInt(),         // x-position 
            scan.nextInt(),         // y-position
            scan.nextInt(),         // hit points
            scan.nextInt(),         // damage
            scan.nextDouble());     // torch radius
      
         tiles = new Tile[width][height];
      
         // Read in the map tiles from file
         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {                   
               String code = scan.next();
               tiles[j][height - i - 1] = new Tile(code, j, height - i - 1);
            }
         }
      
         // Read in the monsters from file
         while (scan.hasNext()) {
            Monster monster = new Monster(
                   this,                 // reference to the World object
                   scan.next(),          // code for type of monster
                   scan.nextInt(),       // x-location
                   scan.nextInt(),       // y-location
                   scan.nextInt(),       // hit points
                   scan.nextInt(),       // damage points
                   scan.nextInt(),       // sleep ms
                   scan.next(),          // code for type of movement
                   scan.nextInt()        // aggroRadius
                   );      
            monsters.add(monster);
         }
         scan.close();
      } catch (FileNotFoundException e) {
         System.out.println("Failed to load file: " + filename);
      }
   
      // Set up the drawing canvas size  
      int canvasX = Math.min(width, windowSizeX);
      int canvasY = Math.min(height, windowSizeY);           
   
      StdDraw.setCanvasSize(canvasX * Tile.SIZE, canvasY * Tile.SIZE);
      if (width < windowSizeX ){
         StdDraw.setXscale(0.0, width * Tile.SIZE);
      } else {
         StdDraw.setXscale(0.0, windowSizeX * Tile.SIZE);
      }
      if (height < windowSizeY ){
         StdDraw.setYscale(0.0, height * Tile.SIZE);
      } else {
         StdDraw.setYscale(0.0, windowSizeY * Tile.SIZE);
      }
   
      // Initial lighting
      light(avatar.getX(), avatar.getY(), avatar.getTorchRadius());
   
      // Fire up the monster threads
      for (Monster monster : monsters) {
         Thread thread = new Thread(monster);
         thread.start();
      }
   }
   
   /**
    * Accessor for the matrix of Tile objects
    */
   public Tile[][] getTileMatrx() {
      return tiles;
   }


   /**
    * Accessor for the Tile the Avatar is currently occupying        
    */
   public Tile getAvatarTile() {
      return tiles[avatar.getX()][avatar.getY()];
   }
   

   /**
    * Figure out if the game should end
    * @return true if avatar still alive, false otherwise
    */
   public boolean avatarAlive() {
      return (avatar.getHitPoints() > 0);
   }

   /**
    * Monster attempting to move to (x, y)
    *      Damage is how much damage this monster will cause if they hit Avatar.
    * @param x - the new x location of a monster
    * @param y - the new y location of a monster
    * @param monster - the monster to be moved
    */
   public synchronized void monsterMove(int x, int y, Monster monster) {
      // Can't attempt to move off board
      if ((x < 0) || (y < 0) || (x >= width) || ( y >= height))
         return;
   
      // Dead monsters move off the board
      if (monster.getHitPoints() <= 0) {
         monster.setLocation(-1, -1);
         return;
      }
   
      // See if we can't actually move there
      if (!tiles[x][y].isPassable())
         return;
                     
   
      // Check if avatar is in this location.  If so, attack but stay put.
      if ((avatar.getX() == x) && (avatar.getY() == y)) {
         avatar.incurDamage(monster.getAttackDamage()); //attack avatar
         //if standing in lava, get hurt
         int damage = tiles[monster.getX()][monster.getY()].getDamage();
         if (damage > 0)
            monster.incurDamage(damage);
         return;
      }
   
      // If there's a monster already in our destination, don't move there.
      for (Monster m : monsters) {
         if ((m != monster) && (m.getX() == x) && (m.getY() == y))
            return;
      }                 
                  
      // If we haven't returned yet, must be a valid move.  Relocate.
      monster.setLocation(x, y);
      
      // if new location is lava, get hurt
      int damage = tiles[x][y].getDamage();
      if (damage > 0)
         monster.incurDamage(damage);
   }

   /**
    * Attempt to move the Avatar to the new (x, y) location.
    * @param x - the new x location
    * @param y - the new y location
    */
   public synchronized void avatarMove(int x, int y) {
   
      // Can't attempt to move off board
      if ((x < 0) || (y < 0) || (x >= width) || ( y >= height))
         return;
   
      // See if we can't actually move there
      if (!tiles[x][y].isPassable())
         return;
   
      // Check to see if there is a monster there
      for (Monster monster : monsters) {
         if ((monster.getX() == x) && (monster.getY() == y)) {
            monster.incurDamage(avatar.getDamage());
            return;
         }
      }       
      int damage = tiles[x][y].getDamage();
      if (damage > 0)
         avatar.incurDamage(damage);
      avatar.setLocation(x, y);
   }
   
   

   /**
    * Handle keyboard input
    * @param ch - the character input from the keyboard
     */
   public void handleKeyPress() {
      char key = 0;
      char[] keyOptions = {'w','W','a','A','s','S','d','D','-','+','='};
   
      for (char k : keyOptions) 
         if (StdDraw.isKeyPressed((int)k)) {
            key = k;
            break; //be content with first key found            
         }
   
      //If no key currently pressed, is there a pending move in the buffer?
      if (key == 0 && StdDraw.hasNextKeyTyped()) { 
         key = StdDraw.nextKeyTyped();            
      }
   
      //Empty the buffer (as only 1 move is allowed to be buffered) (prevents input lag)
      while (StdDraw.hasNextKeyTyped()) 
         StdDraw.nextKeyTyped();
              
      int deltaX = 0;
      int deltaY = 0;
      switch (key) {   
         case 'W':
         case 'w':   
            avatar.setDirection(Avatar.Facing.UP);
            deltaY++;
            break;
         case 'S':
         case 's':
            avatar.setDirection(Avatar.Facing.DOWN);    
            deltaY--;
            break;
         case 'A':
         case 'a':       
            avatar.setDirection(Avatar.Facing.LEFT);
            deltaX--;
            break;
         case 'D':
         case 'd':   
            avatar.setDirection(Avatar.Facing.RIGHT);    
            deltaX++;
            break;
         case '=':
         case '+':
            avatar.increaseTorch();
            break;
         case '-':               
            avatar.decreaseTorch();
            break;          
      }
   
      if ((deltaX != 0) || (deltaY != 0)) {
         int x = avatar.getX() + deltaX;
         int y = avatar.getY() + deltaY;
         avatarMove(x, y);
      }
      
      //after a move, adjust which tiles are lit by the torch
      light(avatar.getX(), avatar.getY(), avatar.getTorchRadius());      
   }   

   /**
    * Draw all the lit tiles
    */
   public synchronized void draw() {
      //Determine offSet (difference between actual map and just the part in our view window)
      offSetX = avatar.getX() - windowSizeX / 2;
      offSetY = avatar.getY() - windowSizeY / 2;      
      if (offSetX < 0) offSetX = 0;
      if (offSetY < 0) offSetY = 0;
      if (offSetX > width - windowSizeX) offSetX = width - windowSizeX;
      if (offSetY > height - windowSizeY) offSetY = height - windowSizeY;
      
      for (int x = 0 + offSetX; x < offSetX + windowSizeX; x++) {
         for (int y = 0 + offSetY; y < offSetY + windowSizeY; y++) {
            tiles[x][y].draw(x,y);
         }
      }
   
      for (int i = monsters.size() - 1; i >=0; i--) {
         Monster monster = monsters.get(i);
         int x = monster.getX();
         int y = monster.getY();
      
         // Draw monsters, but not the dead ones
         if (monster.getHitPoints() <= 0) {
            monsters.remove(i);
         } else {
            if (tiles[x][y].getLit())
               monster.draw();
         }
      }  
           
      avatar.draw();
   }

   /**
    * Return the number of alive monsters
    * @return
    */
   public int getNumMonsters() {
      return monsters.size();
   }
         
   /**
    * Set all tiles to either on or off
    * @param value true (for lit), false otherwise
    */
   private void setAllTilesLit(boolean value) {
      for (int x = 0 ; x < width; x++) 
         for (int y = 0; y < height; y++)
            tiles[x][y].setLit(value);               
   }  

   /**
    * Light the current position and all tiles to a surrounding radius
    * @param x - the current x position
    * @param y - the current y position
    * @param r - the radius of the avatar's torch
    * @return the number of tiles that were lit
    */
   public int light(int x, int y, double r) {
      setAllTilesLit(false); //Set all tiles to unlit
      int result = light(x, y, x, y, r);      
      return result;
   }

   /**
    * Recursively light from (x, y) limiting to radius r
    * @param x - the initial x position
    * @param y - the initial y position
    * @param currentX - the current x position
    * @param currentY - the current y position
    * @param r - the radius of the avatar's torch
    * @return the number of tiles lit
    */
   private int light(int x, int y, int currentX, int currentY, double r)  {
      // ***** <YOUR CODE GOES HERE> *****
      //Replace this code with a recursive method that only lights up tiles within the radius
      if (currentX < 0 || currentY < 0 || currentX > width - 1 || currentY > height - 1 || tiles[currentX][currentY].getLit() || Math.sqrt(Math.pow(currentX - x, 2)) + Math.sqrt(Math.pow(currentY - y, 2)) > r)
         return 0;
      tiles[currentX][currentY].setLit(true);
      return 1 + light(x, y, currentX - 1, currentY, r) + light(x, y, currentX, currentY - 1, r) + light(x, y, currentX + 1, currentY, r) + light(x, y, currentX, currentY + 1, r);
   }    

}
