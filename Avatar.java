import java.awt.Font;
import java.awt.Color;

/**
 * The class that describe the avatar (player)
 *  
 * @author Keith Vertanen
 * @author Michele Van DYne - added commenting
 * @author Kris McCoy - added sprite swap for directions & health text background box
 */
public class Avatar {
   public enum Facing {UP, DOWN, LEFT, RIGHT}
   private int x; 	           	             // current x-location
   private int y; 	                	       // current y-location
   private double torch = 4.0;	             // how powerful our torch is
   private int hp;		            	       // hit points
   private int damage;            	          // damage caused by weapon
   private Stats timer;                       // for timing display of hit points
   private Facing direction = Facing.DOWN;    // direction facing
	 
   private static final double TORCH_DELTA = 0.5;
	
	/**
	 * Constructor for the Avatar class
	 * @param x		 - current x-location
	 * @param y		 - current y-location
	 * @param hp	 - hit points
	 * @param damage - damage we caused by weapon
	 * @param torch  - how powerful our torch is
	 */
   public Avatar(int x, int y, int hp, int damage, double torch) {
      this.x = x;
      this.y = y;
      this.hp = hp;
      this.damage = damage;         
      this.torch = torch;		
   }

	/**
	 * Set a new location foran avatar
	 * @param x - the new x position
	 * @param y - the new y position
	 */
   public void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
      //System.out.println(x + " " + y); //print location of avatar to terminal
   }
   
   /**
	 * Set the direction the avatar is facing
    * @param direction - the direction the avatar is facing (as enumerated type)
	 */
   public void setDirection(Facing direction) {
      this.direction = direction;
   }
	
	/**
	 * Get the x position of the avatar
	 * @return the x position
	 */
   public int getX() {
      return x;
   }
	
	/**
	 * Get the y position of the avatar
	 * @return the y position
	 */
   public int getY() {
      return y;
   }
	
	/**
	 * Get the hit points left for the avatar
	 * @return the hit points remaining
	 */
   public int getHitPoints() {	
      return hp;
   }
	
	/**
	 * Get the current torch radius
	 * @return the torch radius
	 */
   public double getTorchRadius() {
      return torch;
   }
	
	/**
	 * Make our torch more powerful
	 */
   public void increaseTorch() {
      torch += TORCH_DELTA;
   }
	
	/**
	 * Make our torch less powerful
	 */
   public void decreaseTorch() {
      torch -= TORCH_DELTA;
      if (torch < 2.0)
         torch = 2.0;
   }
   
   
   
   /**
	 * The avatar has 4 different images depending on direction facing.
    * This method returns the name of the correct image
	 */
   public String getCorrectAvatarImage() {    
      switch (direction) {               
         case UP:    
            return "img-avatar-up.png";
         case LEFT:  
            return "img-avatar-left.png";
         case RIGHT: 
            return "img-avatar-right.png";
         default:  
            return "img-avatar-down.png";         
      } 
   }
   

	/**
	 * Draw the avatar
	 */
   public void draw() {
      double drawX = (x + 0.5 - World.offSetX) * Tile.SIZE;
      double drawY = (y + 0.5 - World.offSetY) * Tile.SIZE;
      
      String spriteImage = getCorrectAvatarImage();
      StdDraw.picture(drawX, drawY, spriteImage, Tile.SIZE, Tile.SIZE);
                        
      //Show health for a small amount of time after taking damage
      if ((timer != null) && (timer.elapsedTime() < World.DISPLAY_DAMAGE_SEC)) {
         String healthString = "" + hp;
         //Draw background box
         StdDraw.setPenColor(new Color(0, 0, 0, 150)); //black with alpha
         StdDraw.filledRectangle(drawX, drawY - Tile.SIZE/2 + 8, (int)(healthString.length()*4.5)+4, 8);                
         //With font size 14, each digit is 4 pixels wide and 8 pixels tall                       
         //Draw remaining health text
         StdDraw.setPenColor(StdDraw.YELLOW);
         StdDraw.setFont(new Font("SansSerif", Font.BOLD, 14));
         StdDraw.text(drawX, drawY - Tile.SIZE/2 + 8, healthString);
      }                          
   }

	/**
	 * Reduce hit points by amount of damage a monster attack has caused
	 * @param points - the number of points of damage a monster attack causes
	 */
   public void incurDamage(int points) {
      hp -= points;
      if (timer == null) timer = new Stats();
      timer.reset();
   }
	
	/**
	 * Get the amount of damage we cause when we attack
	 * @return - the amount if damage
	 */
   public int getDamage() {
      return damage;		
   }
			
	/**
	 * Test main program to make sure avatar methods are working
	 * @param args - unused
	 */
   public static void main(String [] args) {
      Avatar avatar = new Avatar(5, 5, 20, 4, 4.0);
      System.out.printf("%d %d %.1f\n", avatar.getX(), avatar.getY(), avatar.getTorchRadius());		
      avatar.setLocation(1, 4);
      System.out.printf("%d %d %.1f\n", avatar.getX(), avatar.getY(), avatar.getTorchRadius());
      avatar.increaseTorch();
      System.out.printf("%d %d %.1f\n", avatar.getX(), avatar.getY(), avatar.getTorchRadius());
      for (int i = 0; i < 6; i++) {
         avatar.decreaseTorch();
         System.out.printf("%d %d %.1f\n", avatar.getX(), avatar.getY(), avatar.getTorchRadius());
      }	
   }
}
