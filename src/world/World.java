package world;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 * class representing the game world
 * contains all rooms
 * keeps track of and changes weather
 * moves players and NPCs
 */
public class World{
	private int time = 0;
	private int weatherCounter = 0;
	private boolean daytime = true;
	private boolean sunny = false;
	private Item merchant = null;
	private Item highTension = null;
	private int highTensionLocationState = 0;
	private Room[]  highTensionLocation = new Room[4];
	private ArrayList<Item> windows = new ArrayList<Item>();
	private Image[] windowImages = new Image[4];
	private ArrayList<Player> players = new ArrayList<Player>();
	private Room[][] gameWorld;
	public static final int ROOMSIZE = 5;
	public static final int CONTAINER_MAX = 10;
	public enum Direction{
		NORTH, EAST, SOUTH, WEST;
	}
	
	/**
	 * constructor of world
	 */
	public World(){
		parseRooms();
		parseItems();
		parseWallItems();
		makePlayers();
		loadWindowImages();
		makeNPCs();
		
	}
	
	/**
	 * method that creates all the players
	 * and sets their initial location 
	 * and starting inventory
	 */
	private void makePlayers(){
		Player p = new Player("sakuya",gameWorld[0][0],0,0,0,"sakuya");
		gameWorld[0][0].addPlayer(p);
		p.getInventory().addItem(new Item("Stick|A long, hard, wooden stick.|1|stick|"));
		players.add(p);

		p = new Player("marisa",gameWorld[0][0],0,0,1,"marisa");
		gameWorld[0][0].addPlayer(p);
		p.getInventory().addItem(new Item("Stick|A long, hard, wooden stick.|1|stick|"));
		players.add(p);

		p = new Player("tenshi",gameWorld[0][0],0,0,2,"tenshi");
		gameWorld[0][0].addPlayer(p);
		p.getInventory().addItem(new Item("Stick|A long, hard, wooden stick.|1|stick|"));
		players.add(p);

		p = new Player("reisen",gameWorld[0][0],0,0,3,"reisen");
		gameWorld[0][0].addPlayer(p);
		p.getInventory().addItem(new Item("Stick|A long, hard, wooden stick.|1|stick|"));
		players.add(p);
	}
	
	/**
	 * finds a player in the player array based on a given ID
	 * @param ID of player to find
	 * @return player with the ID matching the given one
	 */
	public Player getPlayerByID(int ID){
		return players.get(ID);
	}

	/**
	 * moves a given player in the direction they are facing
	 * @param p player being moved
	 * @return whether or not the player was able to move in the direction they are facing
	 */
	public boolean movePlayer(Player p){
		int moveX = p.getX();
		int moveY = p.getY();
		Item wallMovingThrough = p.getCurrentRoom().getWallItem(p.getDirection().ordinal());
		if(wallMovingThrough == null){return false;}
		else if(wallMovingThrough.checkBlockMovement()){return false;}
		if(p.getDirection() == World.Direction.NORTH){
			moveY = moveY - 1;
		}
		else if(p.getDirection() == World.Direction.EAST){
			moveX = moveX + 1;
		}
		else if(p.getDirection() == World.Direction.SOUTH){
			moveY = moveY + 1;
		}
		else if(p.getDirection() == World.Direction.WEST){
			moveX = moveX - 1;
		}
		if(moveX < 0 || moveX >= gameWorld.length || moveY < 0 || moveY >= gameWorld[0].length){return false;}
		else if(gameWorld[moveX][moveY] != null){
			p.getCurrentRoom().removePlayer(p);
			gameWorld[moveX][moveY].addPlayer(p);
			p.setX(moveX);
			p.setY(moveY);
			p.setCurrentRoom(gameWorld[moveX][moveY]);
			return true;
		}
		else{return false;}
	}
	
	/**
	 * generate NPCs that require an array of locations they move between
	 * and fill their array of locations they move between
	 * highTension is only NPC that requires an array of locations they move between
	 */
	private void makeNPCs(){
		highTension = new Item("highTensionJapaneseGoblin|ACTIVATE ME FOR COINS|1000|japanesegoblin|DispenseItem@Suika give Coin!@Gold coin|This is a gold coin I got from that goblin.|1|coin|Consume@Gold coin@Why did i just eat a gold coin.@2@false");
		highTensionLocation[0] = gameWorld[1][1];
		highTensionLocation[1] = gameWorld[2][1];
		highTensionLocation[2] = gameWorld[4][0];
		highTensionLocation[3] = gameWorld[0][2];
	}
	
	/**
	 * load all window weather images into the windowImages array
	 */
	private void loadWindowImages(){
		try {
			windowImages[0] = ImageIO.read(new File("itemImages/window day-true sunny-true.png"));
			windowImages[1] = ImageIO.read(new File("itemImages/window day-true sunny-false.png"));
			windowImages[2] = ImageIO.read(new File("itemImages/window day-false sunny-true.png"));
			windowImages[3] = ImageIO.read(new File("itemImages/window day-false sunny-false.png"));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "Missing weather image in itemImages", "Parsing error", JOptionPane.DEFAULT_OPTION);
			e.printStackTrace();
		}
	}
	
	/**
	 * advances all the time related variables like time and weatherCounter
	 * and changes the weather when appropriate
	 */
	public void advanceTime(){
		if(daytime && time == 2){
			time = 0;
			daytime = false;
			changeWeather();
		}
		else if(!daytime && time == 1){
			time = 0;
			daytime = true;
			changeWeather();
		}
		else{time++;}
		if(weatherCounter == 0){
			sunny = !sunny;
			weatherCounter = 3;
			changeWeather();
		}
		else{weatherCounter--;}
		moveNPCs();
	}
	
	/**
	 * changes the images of all windows whenever weather changes
	 * to match the current weather
	 */
	private void changeWeather(){
		Image currentWeather = null;
		if(daytime && sunny){currentWeather = windowImages[0];}
		else if(daytime && !sunny){currentWeather = windowImages[1];}
		else if(!daytime && sunny){currentWeather = windowImages[2];}
		else{currentWeather = windowImages[3];}
		for(Item i : windows){i.setImage(currentWeather);}
	}
	
	/**
	 * moves all the NPCs according to predefined rules specific to each NPC
	 * highTension alternates between 4 rooms
	 * and merchant disappears from the game world during night-time
	 */
	private void moveNPCs(){
		//remove highTension from its current location
		//change highTensionLocationState
		//place highTension in its new location
		highTensionLocation[highTensionLocationState].removeItem(4, 4);
		if(highTensionLocationState == 3){
			highTensionLocationState = 0;
		}
		else{highTensionLocationState++;}
		highTensionLocation[highTensionLocationState].addItem(highTension,4, 4);
		//put merchant in their room if day
		//if night remove merchant from their room
		if(daytime){gameWorld[3][1].addItem(merchant,4,3);}
		else{gameWorld[3][1].removeItem(4,3);}
	}
	
	/**
	 * parse the "rooms.txt" file to generate all the rooms
	 * and the dimensions of gameWorld
	 */
	private void parseRooms(){
		int x = 0;
		int y = 0;
		String line = "";
		try {
			Scanner scan = new Scanner(new File("rooms.txt"));
			if(scan.hasNextInt()){//get the x and y dimensions of the world from the first line of the txt.
				x = scan.nextInt();
				if(scan.hasNextInt()){
					y = scan.nextInt();
					scan.nextLine();
					gameWorld = new Room[x][y];//make the gameworld array based on the world dimensions
				}
			}
			if(x<=0 || y<=0 || gameWorld == null){
				JOptionPane.showConfirmDialog(null, "bad world dimensions must be at least 1*1", "Parsing error", JOptionPane.DEFAULT_OPTION);
				scan.close();
				return;
			}
			//for each x,y in gameWorld assign a room based on a line in rooms.txt
			for(int i = 0;i<x;i++){
				for(int j = 0;j<y;j++){
					if(scan.hasNextLine()) {
						line = scan.nextLine();
						while (line.equals("")) {//skips blank lines
							line = scan.nextLine();
						}
						Room r = new Room(i, j, line);
						if (r != null) {
							gameWorld[i][j] = r;

						}
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "Missing rooms.txt", "Parsing error", JOptionPane.DEFAULT_OPTION);
		}
	}

	/**
	 * parse the "items.txt" file to generate all the items
	 * and add them to their respective rooms
	 * also assigns items called "Merchant" to be the merchant NPC
	 */
	private void parseItems(){
		int worldX = -1;
		int worldY = -1;
		int roomX = -1;
		int roomY = -1;
		String line = "";
		try {
			Scanner scan = new Scanner(new File("items.txt"));
			while (scan.hasNextLine()) {
				//get the x and y coordinates of the room the item will be in and its x,y location within that room.
				if(!scan.hasNextInt()){parserFail("no x value for room loc of an item", scan);}
				worldX = scan.nextInt();

				if(!scan.hasNextInt()){parserFail("no y value for room loc of an item", scan);}
				worldY = scan.nextInt();

				if(!scan.hasNextInt()){parserFail("no x value for loc within room of an item", scan);}
				roomX = scan.nextInt();

				if(!scan.hasNextInt()){parserFail("no y value for loc within room of an item",scan);}
				roomY = scan.nextInt();
				
				//check that all the coordinate values are valid
				if(worldX < 0 || worldX >= gameWorld.length ||worldY < 0 || worldY >= gameWorld[0].length){parserFail("x,y of room for item out of bounds", scan);}
				if(gameWorld[worldX][worldY] == null){parserFail("room at x,y is null cannot make item there", scan);}
				if(roomX < 0 || roomX >= ROOMSIZE ||roomY < 0 || roomY >= ROOMSIZE){parserFail("x,y of item within room out of bounds",scan);}
				
				//make an item from a line and if it is a merchant assign it to merchant
				//also adds the item into its assigned room
				line = scan.nextLine();
				Item nextItem = new Item(line);
				if(nextItem.getName().equalsIgnoreCase("Merchant")){merchant = nextItem;}
				if(!gameWorld[worldX][worldY].addItem(new Item(line),roomX,roomY)){parserFail(("item already at location " + roomX + "," + roomY + " of room at " + worldX + "," + worldY), scan);}
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "Missing items.txt", "Parsing error", JOptionPane.DEFAULT_OPTION);
		}
		catch (ParseException e) {
			//error already described by parseFail if this is called
		}
	}
	
	/**
	 * parse the "wallItems.txt" file to generate all the wallItems
	 * and add them to their respective rooms
	 * also assigns items called "window" to the windows arrayList
	 */
	private void parseWallItems(){
		int worldX = -1;
		int worldY = -1;
		int wallDirection = -1;
		String line = "";
		try {
			Scanner scan = new Scanner(new File("wallItems.txt"));
			while (scan.hasNextLine()) {
				//get the x and y coordinates of the room the item will be in and its location within that room.
				if(!scan.hasNextInt()){parserFail("no x value for room loc of an item", scan);}
				worldX = scan.nextInt();

				if(!scan.hasNextInt()){parserFail("no y value for room loc of an item", scan);}
				worldY = scan.nextInt();

				if(!scan.hasNextInt()){parserFail("no value for direction of the wall", scan);}
				wallDirection = scan.nextInt();

				//check that all the coordinate values are valid
				if(worldX < 0 || worldX >= gameWorld.length ||worldY < 0 || worldY >= gameWorld[0].length){parserFail("x,y of room for item out of bounds", scan);}
				if(gameWorld[worldX][worldY] == null){parserFail("room at x,y is null cannot make item there", scan);}
				if(wallDirection < 0 || wallDirection > 3){parserFail("direction of wall out of bounds",scan);}

				//make an item from a line and if it is a window add it to the windows array
				//also adds the item into its assigned room
				line = scan.nextLine();
				Item wallItem = new Item(line);
				if(wallItem.getName().equalsIgnoreCase("window")){
					windows.add(wallItem);
				}
				if(!gameWorld[worldX][worldY].addWallItem(wallItem,wallDirection)){parserFail(("wallItem already at wall" + wallDirection + " of room at " + worldX + "," + worldY), scan);}
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "Missing wallItems.txt", "Parsing error", JOptionPane.DEFAULT_OPTION);
		}
		catch (ParseException e) {
			//error already described by parseFail if this is called
		}
	}

	/**
	 * displays error popup and throws ParseException
	 * used when parser in World has issues
	 * @param msg failure message
	 * @param s scanner at the point of the failure
	 * @throws ParseException
	 */
	private void parserFail(String msg, Scanner s) throws ParseException{
		s.close();
		JOptionPane.showConfirmDialog(null, msg, "Parsing error", JOptionPane.DEFAULT_OPTION);
		throw new ParseException(null, 0);
	}
}
