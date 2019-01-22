package world;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 * class representing a room in the game that players move through
 * as they progress through the game they also contain most items 
 * players get access to during the game
 */
public class Room {
	
	private String name;
	private String disc;
	private Image[] walls = new Image[5];
	private final int x,y;
	private Item[][] items;
	private Item[] wallItems = new Item[5];
	private ArrayList<Player> players;
	
	/**
	 * @param x the x of the room in the world
	 * @param y the y of the room in the world
	 * @param s string used to parse the required variables to make a Room
	 * format of this described in roomsFormat.txt
	 */
	public Room(int x,int y, String roomData){
		this.x = x;
		this.y = y;
		parseRoom(roomData);
		items = new Item[World.ROOMSIZE][World.ROOMSIZE];
		wallItems = new Item[5];
		players = new ArrayList<Player>();
	}
	
	/**
	 * add an item into a given wall of a room
	 * @param i item being added
	 * @param direction of wall based upon World.Direction.ordinal()
	 * @return true if item was added false if item already existed on given wall
	 */
	public boolean addWallItem(Item i,int direction){
		if(wallItems[direction] == null){
			wallItems[direction] = i;
			return true;
		}
		return false;
	}
	
	/**
	 * add an item into a room at a given spot
	 * @param i item being added
	 * @param x coordinate within room
	 * @param y coordinate within room
	 * @return true if item was added false if item already existed at given x,y
	 */
	public boolean addItem(Item i,int x, int y){
		if(items[x][y] == null){
			items[x][y] = i;
			return true;
		}
		return false;
	}
	
	/**
	 * add an item into a room in the first avalable spot
	 * @param i item being added
	 * @return true if item was added false if room is full
	 */
	public boolean addItem(Item item){
		for(int i = 0; i<World.ROOMSIZE;i++){
			for(int j = 0; j<World.ROOMSIZE;j++){
				if(items[i][j]== null){
					items[i][j] = item;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * removes an item from a specified UNROTATED spot in a room
	 * @param x within room of item to remove
	 * @param y within room of item to remove
	 * @return item removed
	 */
	public Item removeItem(int x, int y){
		Item i = items[x][y];
		items[x][y] = null;
		return i;
	}
	
	/**
	 * removes an item from a specified rotated spot in a room 
	 * (the room from a player perspective)
	 * @param x rotated x within room of item to remove
	 * @param y rotated y within room of item to remove
	 * @param p player who's perspective the room is being viewed from
	 * @return the item removed or null if no item was removed
	 */
	public Item removeItem(int x, int y, Player p){
		World.Direction direction = p.getDirection();
		int accX = x;
		int accY = y;
		//when direction == NORTH x,y don't need to be changed
		if(direction == World.Direction.EAST){
			accX = World.ROOMSIZE-1-y;
			accY = x;
		}
		else if(direction == World.Direction.SOUTH){
			accX = -(x-World.ROOMSIZE+1);
			accY = -(y-World.ROOMSIZE+1);
		}
		else if(direction == World.Direction.WEST){
			accX = y;
			accY = World.ROOMSIZE-1-x;
		}
		Item i = items[accX][accY];
		items[accX][accY] = null;
		return i;
	}
	
	
	/**
	 * get item given unrotated location without removing it
	 * @param x of item in room
	 * @param y of item in room
	 * @return item at given x,y in room
	 */
	public Item getItem(int x, int y){
		Item i = items[x][y];
		return i;
	}
	
	/**
	 * gets an item from a specified rotated spot in a room 
	 * (the room from a player perspective)
	 * @param x rotated x within room of item to get
	 * @param y rotated y within room of item to get
	 * @param p player who's perspective the room is being viewed from
	 * @return the item at that location or null if no item was removed
	 */
	public Item getItem(int x, int y, Player p){
		World.Direction direction = p.getDirection();
		int accX = x;
		int accY = y;
		//when direction == NORTH x,y don't need to be changed
		if(direction == World.Direction.EAST){
			accX = World.ROOMSIZE-1-y;
			accY = x;
		}
		else if(direction == World.Direction.SOUTH){
			accX = -(x-World.ROOMSIZE+1);
			accY = -(y-World.ROOMSIZE+1);
		}
		else if(direction == World.Direction.WEST){
			accX = y;
			accY = World.ROOMSIZE-1-x;
		}
		Item i = items[accX][accY];
		return i;
	}
	
	/**
	 * gets the wallItem at a given index in the wallItem array
	 * (index 0-3 follow world.Direstion.Oridnal())
	 * @param x the .ordinal() of the wall you want to find
	 * @return item at given index in the wallItem array or null if x out of bounds
	 */
	public Item getWallItem(int x){
		if(x<0 || x >= wallItems.length){return null;}
		return wallItems[x];
	}
	
	/**
	 * parses a room from a string according to format of described in roomsFormat.txt
	 * @param dat string used to parse an item
	 * @return if the string was valid or not
	 */
	private boolean parseRoom(String dat){
		String[] data = dat.split("\\|");
		if(data.length != 7){JOptionPane.showConfirmDialog(null, "incorrect number of paramiters in room line : " + data.length, "Parsing error", JOptionPane.DEFAULT_OPTION);return false;}
		name = data[0];
		disc = data[1];
		//for each index in the walls array load the corresponding image into the array
		for(int i = 0;i<5;i++){
			try {
				walls[i] = ImageIO.read(new File("itemImages/"+data[i+2]+ ".png"));
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(null, "Missing image itemImages/"+data[i+2]+".png", "Parsing error", JOptionPane.DEFAULT_OPTION);
				e.printStackTrace();
			}
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public String getDisc() {
		return disc;
	}

	/**
	 * gets the array of images representing the walls of the room
	 * images of wallItems are used over those of the corresponding 
	 * base wall.
	 * @return array of wall images including the floor and roof
	 */
	public Image[] getWalls() {
		Image[] wallItemImages = new Image[5];
		for(int i = 0;i<5;i++){
			if(wallItems[i] != null){wallItemImages[i] = wallItems[i].getImage();}
			else{wallItemImages[i] = walls[i];}
		}
		return wallItemImages;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Item[][] getItems() {
		return items;
	}

	public Item[] getWallItems() {
		return wallItems;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	/**
	 * removes a given player from a room
	 * @param p player to remove
	 * @return if player was removed or not
	 */
	public boolean removePlayer(Player p) {
		return players.remove(p);
	}
	
	/**
	 * gives a unmodified 2d array of images of the items in the room
	 * @return 2d array of images of the items in the room
	 */
	public Image[][] getItemImages(){//LEGACY CODE
		Image[][] images = new Image[World.ROOMSIZE][World.ROOMSIZE];
		for(int i = 0; i<World.ROOMSIZE;i++){
			for(int j = 0; j<World.ROOMSIZE;j++){
				if(items[i][j]!= null){images[i][j] = items[i][j].getImage();}
			}
		}
		return images;
	}
	
	/**
	 * gives a 2d array of images of the items in the room rotated
	 * to match the direction the given player is looking
	 * with any players in the room who also are not
	 * the given player included in gaps where no items are located
	 * @param p player who is viewing the room
	 * @return 2d array of images of the items in the room
	 */
	public Image[][] getItemImages(Player p){
		World.Direction direction = p.getDirection();
		Image[][] images = new Image[World.ROOMSIZE][World.ROOMSIZE];
		/*
		 each direction iterates differently over the items
		 and assigns their images to the 2d array differently
		 to make the room draw rotated correctly for each direction
		 the given player is facing
		*/
		if(direction == World.Direction.NORTH){
			for(int i = 0; i<World.ROOMSIZE;i++){
				for(int j = 0; j<World.ROOMSIZE;j++){
					if(items[i][j]!= null){images[i][j] = items[i][j].getImage();}
				}
			}
		}
		else if(direction == World.Direction.EAST){
			for(int i = World.ROOMSIZE-1; i>=0;i--){
				for(int j = 0 ; j<World.ROOMSIZE;j++){
					if(items[i][j]!= null){images[j][World.ROOMSIZE-1-i] = items[i][j].getImage();}
				}
			}
		}
		else if(direction == World.Direction.SOUTH){
			for(int i = 0; i<World.ROOMSIZE;i++){
				for(int j = 0; j<World.ROOMSIZE;j++){
					if(items[i][j]!= null){images[World.ROOMSIZE-1-i][World.ROOMSIZE-1-j] = items[i][j].getImage();}
				}
			}
		}
		else{
			for(int i = 0; i<World.ROOMSIZE;i++){
				for(int j = World.ROOMSIZE-1; j>=0;j--){
					if(items[i][j]!= null){images[World.ROOMSIZE-1-j][i] = items[i][j].getImage();}
				}
			}
		}
		for(Player pl : players){
			 if(!(pl == p))addPlayerImageArray(pl,images);
		}
		return images;
	}
	
	/**
	 * adds the player images to the rotated 2d array of itemImages
	 * except the given player
	 * @param p player viewing the room
	 * @param images rotates 2d array of images with items already added
	 */
	private void addPlayerImageArray(Player p, Image[][] images){
		for(int i = 0; i<World.ROOMSIZE;i++){
			for(int j = 0; j<World.ROOMSIZE;j++){
				if(images[i][j] == null){
					images[i][j] = p.getImage(); 
					return;
				}
			}
		}
	}
}