package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import world.Container;
import world.Item;
import world.World;

/**
 * A ContainerWindow is an interface for accessing containers within the game.
 * It simply shows a list of the items.
 * @author pheaseedwi 300315747 Edwin Phease
 *
 */
@SuppressWarnings("serial")
public class ContainerWindow extends JFrame {
	
	private Container container;
	private List<Item> items;
	private JPanel itemsPanel;
	private Item selected = null;
	private ApplicationWindow parentWindow = null;
	private ContainerWindow inventoryWindow;
	private boolean isInventory;
	private int x,y;
	private JButton takeButton;
	private JButton dropButton;
	
	private static final String gameTitle = "Puzzling Quest";
	private static final int frameWidth = 400;
	private static final int frameHeight = 500;
	private static final int textHeight = 30;
	private static final int invHeight = 150;
	private static final int rowLength = 5;
	private static final int borderSize = 3;
	private static final int itemWidth = 60;
	
	/**
	 * Create a container within its own JFrame
	 * @param container
	 * @param window
	 * @param x co-ord of container in room
	 * @param y co-ord of container in room
	 */
	public ContainerWindow(Container container, ApplicationWindow window, int x, int y) {
		this.container = container;
		this.items = container.getContents();
		this.parentWindow = window;
		this.x = x;
		this.y = y;
		isInventory = false;
		initialiseWindow();
	}
	
	/**
	 * Create a container within a given JPanel
	 * @param panel
	 * @param window
	 */
	public ContainerWindow(JPanel panel, ApplicationWindow window) {
		itemsPanel = panel;
		this.parentWindow = window;
		isInventory = true;
	}
	
	/**
	 * Initialise the container window with the items and buttons.
	 */
	private void initialiseWindow() {
		setTitle(gameTitle);
		setSize(frameWidth, frameHeight);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel namePanel = new JPanel();
		namePanel.setPreferredSize(new Dimension(frameWidth, textHeight));
		JTextField name = new JTextField(container.getName());
		name.setEditable(false);
		namePanel.add(name);
		this.add(namePanel);
		
		//add items in container to window
		itemsPanel = new JPanel();
		itemsPanel.setPreferredSize(new Dimension(frameWidth, invHeight));
		this.add(itemsPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		takeButton = new JButton("Take item");
		takeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				takeItem();
			}
		});
		buttonPanel.add(takeButton);

		JButton closeButton = new JButton("Close menu");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		buttonPanel.add(closeButton);
		
		this.add(buttonPanel);
		
		
		JPanel invTextPanel = new JPanel();
		invTextPanel.setPreferredSize(new Dimension(frameWidth, textHeight));
		JTextField inventory = new JTextField("Player inventory");
		inventory.setEditable(false);
		invTextPanel.add(inventory);
		this.add(invTextPanel);
		
		//add items in player's inventory to panel
		JPanel inventoryPanel = new JPanel();
		inventoryWindow = new ContainerWindow(inventoryPanel,parentWindow);
		inventoryWindow.setItems(parentWindow.getInv());
		this.add(inventoryPanel);
		

		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new FlowLayout());
		dropButton = new JButton("Insert item into container");
		dropButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertFromInv(inventoryWindow.getSelected());
			}
		});
		buttonPanel2.add(dropButton);
		this.add(buttonPanel2);
		
		redraw(false);
		
	    //put frame in centre of screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation(dim.width/2-this.getSize().width/2, 
	    		dim.height/2-this.getSize().height/2);
	    
		this.setVisible(true);
	}
	
	/**
	 * Set the items in the container.
	 * @param items
	 */
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	/**
	 * Close the frame.
	 */
	public void exit() {
		this.dispose();
	}
	
	/**
	 * Insert an item into this container from the inventory.
	 * @param item
	 */
	public void insertFromInv(Item item) {
		if (item==null) {
			return;
		}
		if (container.addItem(item)) {
			parentWindow.updateInfo(item.getName()+" was placed in "+container.getName());
			int k =inventoryWindow.getIndex(item);
			inventoryWindow.removeItem(item);
			parentWindow.command("depositItem,"+parentWindow.getPlayerID()+","+x+","+y+","+k);
			inventoryWindow.clearSelected();
		} else {
			parentWindow.updateInfo(item.getName()+
					" could not be placed in "+container.getName());
		}
		redraw(true);
	}
	
	/**
	 * Take an item from this container
	 */
	public void takeItem() {
		if (selected != null) {
			if (!parentWindow.invFull()) {
				if (parentWindow.sendItemToInv(selected)) {
					int k =getIndex(selected);
					container.removeItem(selected);
					parentWindow.command("takeItem,"+
							parentWindow.getPlayerID()+","+x+","+y+","+k);
					selected = null;
				}
				redraw(true);
			}
		}
	}
	
	/**
	 * Given an item, find the index of that item in the ArrayList.
	 * @param item
	 * @return
	 */
	public int getIndex(Item item) {
		int itemIndex = -1;
		for (int k=0; k<items.size(); k++){
			if (item.equals(items.get(k))) itemIndex = k;
		}
		if (itemIndex==-1) System.out.println("ITEM NOT FOUND "+item.getName());
		return itemIndex;
	}
	
	/**
	 * Redraw the ContainerWindow, and if requested, redraw the parent ApplicationWindow also.
	 * @param redrawParent
	 */
	public void redraw(boolean redrawParent) {
		if (parentWindow.invFull()) {
			takeButton.setEnabled(false);
		} else {
			takeButton.setEnabled(true);
		}
		if (items.size() >= World.CONTAINER_MAX) {
			dropButton.setEnabled(false);
		} else {
			dropButton.setEnabled(true);
		}
		updateItemsPanel();
		inventoryWindow.updateItemsPanel();
		if (redrawParent) parentWindow.redraw();
	}
	
	/**
	 * Remove an item from this container
	 * @param item
	 */
	public void removeItem(Item item) {
		items.remove(item);
	}
	
	/**
	 * Update the list of items in the panel,
	 * with the selected item highlighted.
	 */
	public void updateItemsPanel() {
		itemsPanel.removeAll();
		itemsPanel.setPreferredSize(new Dimension(frameWidth, invHeight));
		int k = 0;
		for (int i=0 ;i<2 ; i++) {
			Item item = null;
			JPanel row = new JPanel();
			itemsPanel.add(row);
			for (int j=0; j<rowLength; j++) {
				if (k < items.size()) {
					item = items.get(k);
				} else {
					item = null;
				}
				JLabel itemLabel = new JLabel();
				itemLabel.setPreferredSize(new Dimension(itemWidth,itemWidth));
				Border border;
				if (item!=null && selected!=null && selected.equals(item)) {
					border = BorderFactory.createLineBorder(Color.yellow, borderSize);
				} else {
					border = BorderFactory.createLineBorder(Color.black, borderSize);
				}
				itemLabel.setBorder(border);
				if (item!= null) {
					Image image = item.getImage();
					image = getScaledImage(image, itemWidth, itemWidth);
					itemLabel.setIcon(new ImageIcon(image));
					itemLabel.addMouseListener(new mouseListener(item));
				}
				row.add(itemLabel);
				k++;
			}
		}
		itemsPanel.validate();
		this.repaint();
	}
	
	/**
	 * Mouse listener that sets the clicked-on item to be selected.
	 * @author Edwin
	 *
	 */
	private class mouseListener implements MouseListener {
		private Item item;
		private mouseListener(Item item) {
			this.item = item;
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			selected = item;
			updateItemsPanel();
			if (isInventory) {
				parentWindow.itemSelect(item);
			}
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
		}
		public void mouseExited(MouseEvent arg0) {
		}
		public void mousePressed(MouseEvent arg0) {
		}
		public void mouseReleased(MouseEvent arg0) {
		}
	}
	
	/**
	 * Given a image, rescale it to size.
	 * This method courtesy of stackoverflow.
	 * @param srcImg
	 * @param w
	 * @param h
	 * @return
	 */
	public static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
	    		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    if (srcImg.getHeight(null) > srcImg.getWidth(null)) {
	    	int newWidth = srcImg.getWidth(null)*h/srcImg.getHeight(null);
	    	int lBuffer = (w - newWidth)/2 - borderSize;
	    	g2.drawImage(srcImg, lBuffer, 0, newWidth, h, null);
	    } else	{
	    	int newHeight = srcImg.getHeight(null)*w/srcImg.getWidth(null);
	    	int tBuffer = (h - newHeight)/2;
	    	g2.drawImage(srcImg, 0, tBuffer, w, newHeight, null);
	    }
	    g2.dispose();
	    return resizedImg;
	}
	
	/**
	 * Clear the selected item.
	 * @return formerly selected item
	 */
	public Item clearSelected() {
		Item s = selected;
		selected = null;
		return s;
	}
	
	/**
	 * Get the selected item.
	 * @return selected item
	 */
	public Item getSelected() {
		return selected;
	}
	

}
