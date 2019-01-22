package ui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import world.Player;
import world.Room;

/**
 * @author lianglinc 300305167 Lincoln Liang
 *
 *	The visual rendering of the game world. It draws the room and the items inside the room (including players).
 *
 */

@SuppressWarnings("serial")
public class GameCanvas extends Canvas implements MouseListener{
	private static final int imageGapFromWall = 200;
	private static final int imageShiftY = 30;
	private static final int imageGapX = 125;
	private static final int imageGapY = 50;
	private static final int canvasHeight = 400;
	private static final int canvasWidth = 1000;
	private static final int floorHeight = 250;
	private static final int backWallHeight = canvasHeight - floorHeight;
	private static final int imageScaleDivisor = 2;
	
	private Image[] roomImages;
	private Image currentImage;
	private Image[][] itemImages;
	private ApplicationWindow appWindow;
	private Player currentPlayer;

	/**
	 * Creates a canvas that renders the game world, its items, and detects mouse events.
	 * 
	 * @param a - the application window of the game.
	 * @param p - the player of the game that we want to draw the game for.
	 */
	
	public GameCanvas(ApplicationWindow a, Player p) {
		currentPlayer = p;
		setImages(currentPlayer.getCurrentRoom());
		appWindow = a;
		addMouseListener(this);
	}
	
	/**
	 * Updates the current player so we can draw the room view from their perspective.
	 * 
	 * @param p - the player we want to draw the canvas for.
	 */
	
	public void updatePlayer(Player p) {
		currentPlayer = p;
	}

	/**
	 * Sets the room images so that it can be drawn on the screen.
	 * It will set the current image depending on which direction we are looking at.
	 *
	 * @param r - the room we want to draw on the canvas
	 */

	public void setImages(Room r) { 
		roomImages = r.getWalls();
		currentImage = roomImages[currentPlayer.getDirection().ordinal()];
		itemImages = r.getItemImages(currentPlayer);
	}

	/**
	 * Overrides the paint method in the Canvas class.
	 * Draws the back wall image, the floor image, then the item images.
	 * Draws items from the start to end so that items that are closer to the player overlap items further away.
	 * Paints items slightly larger the closer they are to the player. 
	 */

	@Override
	public void paint(Graphics g) {
		
		//create buffer to help make repainting not make the screen flicker as much
		Image offsc = createImage(canvasWidth,canvasWidth);
		Graphics tmp = offsc.getGraphics();	
		tmp.clearRect(0, 0, canvasWidth, canvasHeight);
		super.paint(tmp);
		
		//draw back wall
		tmp.drawImage(currentImage, 0, 0, canvasWidth, backWallHeight, null);

		//draw the floor
		tmp.drawImage(roomImages[4], 0, backWallHeight, canvasWidth, floorHeight, null);
		
		//draw the items in the room
        for (int i = 0; i < itemImages.length; i++) {
        	for (int j = 0; j < itemImages[0].length; j++) {
        		if (itemImages[i][j] != null) {
        			int imageWidth = itemImages[i][j].getWidth(null) / imageScaleDivisor;
        			int imageHeight = itemImages[i][j].getHeight(null) / imageScaleDivisor;
        			
        			tmp.drawImage(itemImages[i][j], imageGapFromWall + imageGapX * i, 
        			backWallHeight - imageHeight + imageGapY * j + imageShiftY, 
        			imageWidth + j * 3, imageHeight + j * 3, null); 
        		}
        	}
        }
        
        //draw the offscreen buffer onto the screen
		g.drawImage(offsc, 0, 0, null);
    }

	/**
	 * Overrides the mouseClicked method in the MouseListener interface with our own implementation.
	 * Looks through the image array from the end to the start so it selects the closest image to the player.
	 * Passes its co-ordinates to the application window for it to decide what to do with it.
	 * Passes the direction the player is facing to the application window if the back wall is clicked instead. 
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//check if clicked on item image
		for (int i = itemImages.length - 1; i >= 0; i--) {
        	for (int j = itemImages[0].length - 1; j >= 0; j--) {
        		if (itemImages[i][j] != null) {
        			int imageWidth = itemImages[i][j].getWidth(null) / imageScaleDivisor;
        			int imageHeight = itemImages[i][j].getHeight(null) / imageScaleDivisor;
        			
	        		if (e.getX() >= imageGapFromWall + imageGapX * i 
	        		&& e.getX() <= imageGapFromWall + imageWidth + j * 3 + imageGapX * i
	        		&& e.getY() >= backWallHeight - imageHeight + imageGapY * j + imageShiftY 
	        		&& e.getY() <= backWallHeight + imageGapY * j + imageShiftY + j * 3) {
	        			appWindow.clickedOn(i, j);
	        			return;
	        		}
        		}
        	}
        }
		
		//otherwise check if clicked on back wall
		if (e.getX() >= 0 && e.getX() <= canvasWidth && e.getY() >= 0 && e.getY() <= backWallHeight) {
			appWindow.clickOnWall(currentPlayer.getDirection().ordinal());
		}

	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}