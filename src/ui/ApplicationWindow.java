package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import networking.ClientConnection;
import world.Container;
import world.Item;
import world.Player;
import world.World;

/**
 * The application window provides appropriate menus and buttons allowing
 * the user to perform actions in the game.
 * @author pheaseedwi 300315747 Edwin Phease
 * @author hammatalex 300327355 Alexander Hammatt - minor tweaks only.
 *
 */

public class ApplicationWindow {

	/**
	 * An action represents what will occur when the player clicks on an item in the world.
	 * @author pheaseedwi 300315747 Edwin Phease
	 *
	 */
	public enum Action {
		Drop,Open,Activate,Collect,Examine,Talk,Use,Consume,Poke;
	}

	private JFrame frame;
	private GameCanvas display;
	private Action action;
	private JPanel inventoryPanel;
	private World world;
	private Player currentPlayer;
	private ContainerWindow invWindow;
	private JTextArea infoText;
	private ContainerWindow containerOpen;
	private ClientConnection serverClient;

	private static final String gameTitle = "Puzzling Quest";
	private static final int frameWidth = 1000;
	private static final int frameHeight = 700;
	private static final int displayHeight = 400;
	private static final int controlHeight = 500;
	private static final String map  ="-----------  \n|s| | | | |  \n- - - - - -  \n"
			+"| / / /   |  \n--------- -  \n| | |f/   |  \n-------|-|-  \n"
			+"\nLegend:\ns: Start\nf: finish\n/:Sealed door";



	public ApplicationWindow() {
		world = new World();
		currentPlayer = world.getPlayerByID(0);
	}
	
	/**
	 * When in Multiplayer, set the client connection.
	 * This is used to receive and send messages from the server.
	 * @param c the clientConnection
	 */
	public void setClient(ClientConnection c) {
		serverClient = c;
	}

	/**
	 * Initialise the display of the game, by creating the JFrame,
	 * and adding the rendered display, buttons and menu bar.
	 */
	public void initialiseDisplay() {
		frame = new JFrame();
		
		//make frame display confirmation on close
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    ApplicationWindow thisWindow = this;
	    frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        	MainWindow.exit(thisWindow, frame);
	        }
	    });
	    
	    frame.setTitle(gameTitle);
	    frame.setSize(frameWidth, frameHeight);
	    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

	    MainWindow.addMenu(frame, this);

	    //add key listener, for movement.
	    KeyListener keys = new Keys();
	    frame.addKeyListener(keys);
		display = new GameCanvas(this, currentPlayer);
	    display.setSize(frameWidth, displayHeight);
	    display.addKeyListener(keys);
	    frame.add(display);

	    JPanel controlPanel = new JPanel();
	    controlPanel.setSize(frameWidth, controlHeight);
	    controlPanel.setLayout(new FlowLayout());
	    frame.add(controlPanel);

	    //initialise infoText, on controlPanel, to wrap text and be scrollable
	    infoText = new JTextArea(">>Welcome to Puzzling Quest<<");
	    infoText.setEditable(false);
	    infoText.setOpaque(false);
	    infoText.setWrapStyleWord(true);
	    infoText.setLineWrap(true);
	    infoText.setFocusable(false);//the textbox cannot take focus from the canvas
	    JScrollPane scrollPane = new JScrollPane(infoText,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setPreferredSize(new Dimension(frameWidth/5, controlHeight/6));
	    scrollPane.setAlignmentX(0f);
	    controlPanel.add(scrollPane);

	    //add buttons to control panel
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setSize(frameWidth/2, controlHeight);
	    buttonPanel.setLayout(new GridLayout(3,3));
	    controlPanel.add(buttonPanel);

	    ButtonGroup group = new ButtonGroup();

	    for (int i=0; i<Action.values().length; i++) {
	    	JRadioButton button = new JRadioButton(Action.values()[i].toString());
	    	button.setActionCommand(i+"");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int j = Integer.parseInt(arg0.getActionCommand());
					action = Action.values()[j];
				}
			});
			group.add(button);
			buttonPanel.add(button);
			button.setFocusable(false);//the buttons cannot take focus from the canvas
	    }

	    //add inventory to control panel
	    inventoryPanel = new JPanel();
	    inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
		invWindow = new ContainerWindow(inventoryPanel, this);
	    updateInventory();
	    controlPanel.add(inventoryPanel);
	    
	    //put frame in centre of screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setLocation(dim.width/2-frame.getSize().width/2, 
	    		dim.height/2-frame.getSize().height/2);

	    frame.setVisible(true);

	    display.requestFocus();//the canvas requests focus for the key listener.
	}

	/**
	 * Draw all the items in the player's inventory.
	 */
	private void updateInventory() {
		List<Item> items = getInv();
		invWindow.setItems(items);
		invWindow.updateItemsPanel();

	}
	
	/**
	 * An alert from the Canvas that an Item in the Canvas was clicked on.
	 * Takes the co-ordinates of the item that was clicked on.
	 * Acts appropriately, based off the currently selected action.
	 * @param i x co-ord of item clicked on
	 * @param j y co-ord of item clicked on
	 */
	public void clickedOn(int i, int j) {
		if (action==null) {
			infoText.setText("No action selected.");
			return;
		}
		Item item = currentPlayer.getCurrentRoom().getItem(i, j, currentPlayer);
		//if item is null, the image clicked on is a player
		if (item==null) {
			switch (action) {
			case Examine:
				infoText.setText("It's a fellow player");
				break;
			case Talk:
				infoText.setText("This player is too busy to talk.");
				break;
			case Poke:
				infoText.setText("You're too shy to poke that person.");
				break;
			default:
				infoText.setText("Cannot "+action.toString()+" a player");
				break;
			}
			return;
		}
		//else, run action on the item
		switch (action) {
		case Activate:
			String s = item.activate(currentPlayer);
			if (s==null) { //item could not be activated
				infoText.setText("A " +item.getName() +" can't be activated!");
			} else {
				infoText.setText(s);
				command("activate,"+currentPlayer.getID()+","+i+","+j);
				redraw();
			}
			break;
		case Collect:
			if (currentPlayer.getInventory().addItem(item)) { //attempt to add item to inventory
				currentPlayer.getCurrentRoom().removeItem(i, j, currentPlayer);
				infoText.setText("Item "+item.getName()+" collected!");
				command("collect,"+currentPlayer.getID()+","+i+","+j);
			} else { //display appropriate message informing that item could not be picked up
				if (invFull()) {
					infoText.setText("Cannot pick up the "+item.getName()
						+", inventory full!");
				} else if (item.getSize()<13) {
					infoText.setText("The "+item.getName()+
							" is just a little too heavy. If only you were stronger...");
				} else if (item.getSize()==66) {
					infoText.setText("You wince in pain as the heat scalds your fingers. "
							+"That's far too hot to touch.");
				} else {
					infoText.setText("The "+item.getName()+" is much too heavy to pick up.");
				}
			}
			redraw();
			break;
		case Drop:
			infoText.setText("You can't drop an item already in the world!");
			break;
		case Examine:
			examine(item);
			break;
		case Open:
			closeOpenContainer();
			if (item.checkLocked()) {
				infoText.setText(item.getName()+" is locked!");
			} else {
				Container container = item.open();
				if (container==null) { //item was not a container
					infoText.setText("Cannot open the "+item.getName());
				} else {
					infoText.setText("Opened the "+item.getName());
					containerOpen = new ContainerWindow(container, this, i, j);
				}
			}
			break;
		case Poke:
			infoText.setText("You poke the "+item.getName()+". Your finger hurts");
			break;
		case Consume:
			infoText.setText("You cannot consume an item in the world, pick it up first.");
			break;
		case Talk:
			if (item.getName().equals("Merchant")) {
				infoText.setText("Hey there, adventurer. Would you perhaps be interested"
						+ " in some of my big, juicy melons? Just one gold coin.");
			} else if (item.getName().equals("highTensionJapaneseGoblin")) {
				infoText.setText("H-hi there. I just happened to have some spare gold coins. "
						+ "Um, I guess you could have one. "
						+"I-it's not like I brought them for your sake! Baka!");
			}
			else {
				infoText.setText("The " + item.getName() + " is strangely silent."
					+"\nPerhaps it's not in the mood to talk today");
			}
			break;
		case Use:
			if(invWindow.getSelected() != null){ //check an item is selected, to use
				String oldItemName = item.getName();
				Item selected = invWindow.getSelected();
				int itemIndex = invWindow.getIndex(selected);
				s = item.useOnThis(selected, currentPlayer);
				command("use,"+currentPlayer.getID()+","+i+","+j+","+itemIndex);
				if (s.equals("")) { //item could not be used
					infoText.setText("Cannot use the "+invWindow.getSelected().getName()+
							" on "+oldItemName);
				}
				else {
					infoText.setText("Used the "+invWindow.getSelected().getName()+
							" on the "+oldItemName+"\n" + s);
				}
				redraw();
			} else {
				infoText.setText("No item selected to use on the " + item.getName());
				redraw();
			}
			break;
		default:
			//should never happen
			infoText.setText("No response for the selected action available.");
			break;
		}
	}

	/**
	 * An alert from the Inventory that the user has selected an item in their inventory.
	 * If the appropriate action is selected, perform an action.
	 * @param item selected
	 */
	public void itemSelect(Item item) {
		if (action==null) {
			return;
		}
		switch (action) {
		case Drop:
			if (currentPlayer.getCurrentRoom().addItem(item)) { //attempt to add item to room
				infoText.setText("Item "+item.getName()+" dropped!");
				int itemIndex = invWindow.getIndex(item);
				currentPlayer.getInventory().removeItem(item);
				command("drop,"+currentPlayer.getID()+","+itemIndex);
				invWindow.clearSelected();
				redraw();
			} else {
				infoText.setText("Could not drop the "+item.getName()+".");
			}
			break;
		case Examine:
			examine(item);
			break;
		case Open:
			if (item.open()==null) {
				infoText.setText("Cannot open the "+item.getName());
			} else {
				infoText.setText("You can't open a backpack while it's on your back. "+
						"Put it down first.");
			}
			break;
		case Consume:
			invWindow.clearSelected();
			int itemIndex = invWindow.getIndex(item);
			String s = item.useOnThis(item, currentPlayer);
			if (s==null||s.equals("")) { //item could not be consumed
				infoText.setText("Cannot consume the "+item.getName());
			} else {
				infoText.setText("You consumed the "+item.getName()+". "+s
						+"\nScore: "+currentPlayer.getScore());
				command("consume,"+currentPlayer.getID()+","+itemIndex);
				redraw();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * An alert from the Canvas that a wall was clicked on.
	 * Use the appropriate action on the wall.
	 * @param dir
	 */
	public void clickOnWall(int dir) {
		if (action==null) return;
		Item item = currentPlayer.getCurrentRoom().getWallItem(dir);
		if (item==null) return;
		switch(action) {
		case Use:
			if(invWindow.getSelected() != null){ //check if item is selected to use
				String oldItemName = item.getName();
				Item selected = invWindow.getSelected();
				int itemIndex = invWindow.getIndex(selected);
				String s = item.useOnThis(selected, currentPlayer);
				command("useWall,"+currentPlayer.getID()+","+dir+","+itemIndex);
				if (s.equals("")) { //item could not be used
					infoText.setText("Cannot use the "+invWindow.getSelected().getName()+
							" on "+oldItemName);
				}
				else {
					infoText.setText("Used the "+invWindow.getSelected().getName()+
							" on the "+oldItemName+"\n" + s);
				}
				redraw();
			} else {
				infoText.setText("No item selected to use on the " + item.getName());
			}
			break;
		case Examine:
			examine(item);
			break;
		case Poke:
			infoText.setText("You poke the "+item.getName()+
					", and wince as crippling pain shoots through your finger.");
			break;
		default:
			infoText.setText("Cannot "+action.toString()+" a " + item.getName());
			break;
		}
	}
	
	/**
	 * Examine an item, update infoText accordingly.
	 * @param item
	 */
	private void examine(Item item) {
		if (item.getName().equals("Map")) {
			infoText.setText(map);
		}
		else infoText.setText(item.getName() +":\n"+ item.getDisc());
		infoText.setCaretPosition(0);
	}

	/**
	 * Attempt to send item to player's inventory
	 * @param item
	 * @return success of adding item
	 */
	public boolean sendItemToInv(Item item) {
		if (currentPlayer.getInventory().addItem(item)) {
			infoText.setText("Item "+item.getName()+" collected!");
			redraw();
			return true;
		} else {
			infoText.setText("Cannot pick up the "+item.getName()+", inventory full!");
			redraw();
			return false;
		}
	}
	
	/**
	 * Update the infoText to inform user of event
	 * @param s
	 */
	public void updateInfo(String s) {
		infoText.setText(s);
	}

	/**
	 * Check if the inventory is full
	 * @return
	 */
	public boolean invFull() {
		return getInv().size() >= World.CONTAINER_MAX;
	}

	/**
	 * Get current player's inventory
	 * @return
	 */
	public List<Item> getInv() {
		return currentPlayer.getInventory().getContents();
	}

	/**
	 * If a container is currently open, close the window.
	 * This prevents attempts to modify container from other rooms.
	 */
	private void closeOpenContainer() {
		if (containerOpen!=null) {
			containerOpen.dispose();
			containerOpen = null;
		}
	}

	/**
	 * Redraw the window, canvas, inventory and any containers open.
	 */
	public void redraw() {
		if (infoText!=null) {
			infoText.repaint();
			updateInventory();
			display.setImages(currentPlayer.getCurrentRoom());
			display.repaint();
			if (containerOpen!=null) {
				containerOpen.redraw(false);
			}
		}
	}
	
	/**
	 * Move the current player forward, to a new room.
	 */
	private void moveForward() {
		closeOpenContainer();
		if (world.movePlayer(currentPlayer)) {
			redraw();
			command("move,"+currentPlayer.getID());
			infoText.setText(currentPlayer.getCurrentRoom().getName()
				+":\n"+currentPlayer.getCurrentRoom().getDisc()
				+"\nScore: "+currentPlayer.getScore());
		} else {
			infoText.setText("You walk into the wall. Ouch.");
		}
	}

	/**
	 * Turn the current player left
	 */
	private void turnLeft() {
		closeOpenContainer();
		currentPlayer.turnLeft();
		infoText.setText("You turn to face "+currentPlayer.getDirection().toString());
		redraw();
		command("turnLeft,"+currentPlayer.getID());
	}
	
	/**
	 * Turn the current player right
	 */
	private void turnRight() {
		closeOpenContainer();
		currentPlayer.turnRight();
		infoText.setText("You turn to face "+currentPlayer.getDirection().toString());
		redraw();
		command("turnRight,"+currentPlayer.getID());
	}
	
	//Methods for interacting with the server
	
	/**
	 * Send a command to the server
	 * @param s
	 */
	public void command(String s) {
		if (serverClient!=null) serverClient.send(s);
	}
	
	/**
	 * Set the current Player by ID. 
	 * Should only be called once, when joining the server.
	 * @param id
	 */
	public void setPlayerID(int id) {
		currentPlayer = world.getPlayerByID(id);
		if (display!=null) display.updatePlayer(currentPlayer);
	}
	
	/**
	 * Another player collected a given item
	 * @param playerID
	 * @param x co-ord
	 * @param y co-ord
	 */
	public void collectItem(int playerID, int x, int y) {
		Player player = world.getPlayerByID(playerID);
		Item item = player.getCurrentRoom().getItem(x, y, player);
		player.getInventory().addItem(item);
		player.getCurrentRoom().removeItem(x, y, player);
		redraw();
	}
	
	/**
	 * Another player moved forward
	 * @param id
	 */
	public void movePlayerForward(int id) {
		world.movePlayer(world.getPlayerByID(id));
		redraw();
	}
	
	/**
	 * Another player turned left
	 * @param id
	 */
	public void turnLeft(int id) {
		world.getPlayerByID(id).turnLeft();
		redraw();
	}
	
	/**
	 * Another player turned right
	 * @param id
	 */
	public void turnRight(int id) {
		world.getPlayerByID(id).turnRight();
		redraw();
	}
	
	/**
	 * Another player dropped an item in their inventory
	 * @param id
	 * @param i index of item in inventory
	 */
	public void dropItem(int id, int i) {
		Player player = world.getPlayerByID(id);
		Item item = player.getInventory().getContents().get(i);
		player.getCurrentRoom().addItem(item);
		player.getInventory().removeItem(item);
		redraw();
	}
	
	/**
	 * Another player used an item in their inventory on an item in their room.
	 * @param id
	 * @param x co-ord
	 * @param y co-ord
	 * @param i index of item in inventory
	 */
	public void useItem(int id, int x, int y, int i) {
		Player player = world.getPlayerByID(id);
		Item item = player.getCurrentRoom().getItem(x, y, player);
		item.useOnThis(player.getInventory().getContents().get(i), player);
		redraw();
	}
	
	/**
	 * Another player took an item from a container in their room.
	 * @param id
	 * @param x co-ord
	 * @param y co-ord
	 * @param i index of item in inventory
	 */
	public void takeItem(int id, int x, int y, int i) {
		Player player = world.getPlayerByID(id);
		Item containerItem = player.getCurrentRoom().getItem(x, y, player);
		Container container = containerItem.open();
		Item item = container.getContents().get(i);
		container.removeItem(item);
		player.getInventory().addItem(item);
		redraw();
	}
	
	/**
	 * Another player deposited an item into a container in their room.
	 * @param id
	 * @param x co-ord
	 * @param y co-ord
	 * @param i index of item in inventory
	 */
	public void depositItem(int id, int x, int y, int i) {
		Player player = world.getPlayerByID(id);
		Item containerItem = player.getCurrentRoom().getItem(x, y, player);
		Container container = containerItem.open();
		Item item = player.getInventory().getContents().get(i);
		container.addItem(item);
		player.getInventory().removeItem(item);
		redraw();
	}
	
	/**
	 * Another player activated an item in their room.
	 * @param id
	 * @param x co-ord
	 * @param y co-ord
	 */
	public void activate(int id, int x, int y) {
		Player player = world.getPlayerByID(id);
		Item item = player.getCurrentRoom().getItem(x, y, player);
		item.activate(player);
		redraw();
	}
	
	/**
	 * Another player used an item on a wall
	 * @param id
	 * @param dir
	 * @param i index of item in inventory
	 */
	public void useWall(int id, int dir, int i) {
		Player player = world.getPlayerByID(id);
		Item item = player.getCurrentRoom().getWallItem(dir);
		item.useOnThis(player.getInventory().getContents().get(i), player);
		redraw();
	}
	
	/**
	 * Another player consumed an item in their inventory
	 * @param id
	 * @param i index of item in inventory
	 */
	public void consume(int id, int i) {
		Player player = world.getPlayerByID(id);
		Item item = player.getInventory().getContents().get(i);
		item.useOnThis(item, player);
	}
	
	public void clockTick() {
		world.advanceTime();
		redraw();
	}
	
	/**
	 * Return this player's id.
	 * @return
	 */
	public int getPlayerID() {
		return currentPlayer.getID();
	}
	

	/**
	 * A KeyListener for the hotkeys.
	 * @author Edwin
	 *
	 */
	private class Keys implements KeyListener {
		@Override
		public void keyPressed(KeyEvent arg0) {
			int key = arg0.getKeyCode();
			if (key==KeyEvent.VK_UP) {
				moveForward();
			} else if (key==KeyEvent.VK_LEFT) {
				turnLeft();
			} else if (key==KeyEvent.VK_RIGHT) {
				turnRight();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
}
