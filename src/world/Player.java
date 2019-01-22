package world;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Player {
	private int score = 0;
	private Container inventory;
	private String name;
	private World.Direction direction;
	private Room currentRoom;
	private Image image;
	private int x,y,carrySize,id;
	
	/**
	 * constructor of default player used for testing
	 */
	public Player(){
		x = 0;
		y = 0;
		this.id = 0;
		carrySize = 10;
		name = "default player";
		inventory = new Container(carrySize,name,"");
		currentRoom = new Room(0,0,"test player room|testing room|shittywall|east|south|west|floor|roof");
		direction = World.Direction.NORTH;
	}
	
	/**
	 * constructor of player
	 * @param nam name of the player
	 * @param startingRoom the room the player starts in
	 * @param startX x coordinate of startingRoom
	 * @param startY y coordinate of startingRoom
	 * @param id unique id of this player
	 * @param imageName name of the image used to display this player in the game
	 */
	public Player(String nam, Room startingRoom, int startX, int startY, int id, String imageName){
		x = startX;
		y = startY;
		this.id = id;
		carrySize = 10;
		name = nam;
		inventory = new Container(carrySize,name,"");
		currentRoom = startingRoom;
		direction = World.Direction.SOUTH;
		try {
			image = ImageIO.read(new File("playerImages/"+imageName+".png"));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "Missing image playerImages/"+imageName+".png", "Parsing error", JOptionPane.DEFAULT_OPTION);
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}
	public String getName(){
		return name;
	}
	
	public Container getInventory() {
		return inventory;
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	public void setCurrentRoom(Room newRoom) {
		currentRoom = newRoom;
	}
	public World.Direction getDirection(){
		return direction;
	}
	
	/**
	 * rotates the player to face the direction on their left
	 * @return World.Direction the player is facing after turning
	 */
	public World.Direction turnLeft(){
		if(direction == World.Direction.NORTH){direction = World.Direction.WEST;}
		else if(direction == World.Direction.EAST){direction = World.Direction.NORTH;}
		else if(direction == World.Direction.SOUTH){direction = World.Direction.EAST;}
		else if(direction == World.Direction.WEST){direction = World.Direction.SOUTH;}
		return direction;
	}
	
	/**
	 * rotates the player to face the direction on their right
	 * @return World.Direction the player is facing after turning
	 */
	public World.Direction turnRight(){
		if(direction == World.Direction.NORTH){direction = World.Direction.EAST;}
		else if(direction == World.Direction.EAST){direction = World.Direction.SOUTH;}
		else if(direction == World.Direction.SOUTH){direction = World.Direction.WEST;}
		else if(direction == World.Direction.WEST){direction = World.Direction.NORTH;}
		return direction;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Image getImage() {
		return image;
	}

	public int getScore() {
		return score;
	}
	
	/**
	 * Increases the players score by a specified amount
	 * that is not negative
	 * @param i amount of score the player has gained
	 * @return players score after increasing it
	 */
	public int addScore(int i) {
		if(i<0){return score;}//stop score getting decreased
		score = score + i;
		return score;
	}
}
