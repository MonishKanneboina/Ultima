/**
 * Class representing a tile in the Ultima game
 * 
 * @author Keith Vertanen
 * @author Michele Van Dyne - added commenting
 * @author Kris McCoy - altered TileType and removed opacity options
 *
 */
public class Tile {
   public enum TileType {PATH, LAVA, WATER, BUSH, TOMBSTONE, GRASS, ROCK, STONEWALL, STONEWALLFRONT, CRATE}
   public static final int SIZE = 32;           // Size of a tile in pixels
   public static final int TILE_DAMAGE = 2;     // If a TileType causes damage, how much damage it should cause

   private TileType type;                       // Type of tile this is
   private boolean lit = false;                 // Is the tile currently illuminated?     
   private int x, y;

   /**
    * Constructor for the tile class
    * converts a character from the file into our enumerated type
    * @param code - letter code that determines the type of tile
    */
   public Tile(String code) {
      this(code, 0, 0);        
   }
   
   /**
    * Constructor for the tile class
    * converts a character from the file into our enumerated type
    * @param code - letter code that determines the type of tile
    * @param x - horizontal location of this Tile within the matrix
    * @param y - vertical location of this Tile within the matrix
    */
   public Tile(String code, int x, int y) {
      if      (code.equals("P"))  type = TileType.PATH;
      else if (code.equals("L"))  type = TileType.LAVA;
      else if (code.equals("W"))  type = TileType.WATER;
      else if (code.equals("B"))  type = TileType.BUSH;
      else if (code.equals("G"))  type = TileType.GRASS;
      else if (code.equals("R"))  type = TileType.ROCK;
      else if (code.equals("S"))  type = TileType.STONEWALL;
      else if (code.equals("F"))  type = TileType.STONEWALLFRONT;
      else if (code.equals("T"))  type = TileType.TOMBSTONE;
      else if (code.equals("C"))  type = TileType.CRATE;
      else type = TileType.GRASS; //default
      this.x = x;
      this.y = y;
   }
   
   public int getX() { 
      return x; 
   }
   
   public int getY(){ 
      return y; 
   }

   /**
    * Get whether this tile is lit or not
    * @return true if lit, false otherwise
    */
   public boolean getLit() {
      return lit;
   }

   /**
    * Set whether the tile is lit or not
    */
   public void setLit(boolean value) {
      lit = value;
   }

   /**
    * Get the amount of damage caused by this tile
    * @return the damage caused
    */
   public int getDamage() {
      if (type == TileType.LAVA)
         return TILE_DAMAGE;
      return 0;
   }

   /**
    * Can the hero walk through this tile
    */
   public boolean isPassable() {      
      switch (type) {
         case PATH:
         case GRASS:
         case LAVA:
            return true;   
         default:    
            return false;  
      }       
   }

   /**
    * Draw the tile at the given location
    * @param x the x position of the tile
    * @param y the y position of the tile
    */
   public void draw(int x, int y) {
      double drawX = (x + 0.5 - World.offSetX) * Tile.SIZE;
      double drawY = (y + 0.5 - World.offSetY) * Tile.SIZE;
   
      if (lit) {
         switch (type) {
            case PATH:  StdDraw.picture(drawX, drawY, "img-path.png", Tile.SIZE, Tile.SIZE);    
               break;
            case LAVA:  StdDraw.picture(drawX, drawY, "img-lava.png", Tile.SIZE, Tile.SIZE);          
               break;
            case WATER: StdDraw.picture(drawX, drawY, "img-water.png", Tile.SIZE, Tile.SIZE);         
               break;
            case GRASS: StdDraw.picture(drawX, drawY, "img-grass.png", Tile.SIZE, Tile.SIZE);    
               break;
            case BUSH:  StdDraw.picture(drawX, drawY, "img-bush.png", Tile.SIZE, Tile.SIZE);        
               break;
            case ROCK: StdDraw.picture(drawX, drawY, "img-rock.png", Tile.SIZE, Tile.SIZE);     
               break;
            case STONEWALL: StdDraw.picture(drawX, drawY, "img-stonewall-top.png", Tile.SIZE, Tile.SIZE);     
               break;
            case STONEWALLFRONT: StdDraw.picture(drawX, drawY, "img-stonewall-front.png", Tile.SIZE, Tile.SIZE);     
               break;
            case TOMBSTONE: StdDraw.picture(drawX, drawY, "img-tombstone.png", Tile.SIZE, Tile.SIZE);     
               break;
            case CRATE: StdDraw.picture(drawX, drawY, "img-crate.png", Tile.SIZE, Tile.SIZE);     
               break;
            default: StdDraw.picture(drawX, drawY, "img-grass.png", Tile.SIZE, Tile.SIZE);                                         
         }           
      } else {
         StdDraw.picture(drawX, drawY, "img-blank.gif", Tile.SIZE, Tile.SIZE);     
      }
   }
   

   /**
    * Test main method to ensure tile methods are correct
    */
   public static void main(String[] args){      
      final int WIDTH = 10;  
      final int HEIGHT = 2;
   
      StdDraw.setCanvasSize(WIDTH * SIZE, HEIGHT * SIZE);
      StdDraw.setXscale(0.0, WIDTH * SIZE);
      StdDraw.setYscale(0.0, HEIGHT * SIZE);
   
      String [] codes = {"P", "B", "L", "W", "F", "G", "T", "S", "C", "R"};
      for (int i = 0; i < WIDTH; i++) {
         for (int j = 0; j < HEIGHT; j++) {
            Tile tile = new Tile(codes[i]);
            if ((i + j) % 2 == 0)
               tile.setLit(true);
            System.out.printf("%d %d : lit %s  \tpassable %s\n", i, j, tile.getLit(), tile.isPassable()); 
            tile.draw(i, j);
         }
      }       
   }     
}