package world; 
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 * class representing an item or wallItem in the game
 */
public class Item {
	protected static int itemCount = 0;
	protected static ArrayList<Item> allItems = new ArrayList<Item>();
	private final int id;
	private String name;
	private String disc;
	private int size;
	private Image image;
	private HashMap<String,UsedOnThisStrategy> useableOnThis = new HashMap<String,UsedOnThisStrategy>();
	private Container container;
	private ActivateStrategy activateStrat;
	private boolean locked;
	private boolean blockMovement;

	/**
	 * gets a item from the static array of items by its ID
	 * @param id of item you are looking for
	 * @return item you are looking for or null if item doesn't exist
	 */
	public static Item getItemByID(int id){
		if(allItems.get(id).getID() == id){return allItems.get(id);}
		else {
			for(Item i : allItems){
				if(i.getID() == id){return i;}
			}
		}
		return null;
	}
	/**
	 * @param s string used to parse the required variables to make an item
	 * format of this described in itemFormat.txt
	 */
	public Item(String itemData){
		id = itemCount;
		allItems.add(this);
		itemCount++;
		locked = false;
		blockMovement = false;
		parseItem(itemData);
	}
	
	/**
	 * @return this items container
	 */
	public Container open(){
		return container;
	}
	
	/**
	 * @return boolean if this items container is locked or not
	 */
	public boolean checkLocked(){
		return locked;
	}
	
	public void unlock(){
		this.locked = false;
	}
	
	/**
	 * @return boolean if this item blocks movement or not (only effects items used as wallitems)
	 */
	public boolean checkBlockMovement(){
		return blockMovement;
	}
	
	public void unBlockMovement(){
		this.blockMovement = false;
	}
	
	/**
	 * activate this item
	 * @param p player activating the item
	 * @return description of event "" if item unactivateable
	 */
	public String activate(Player p){
		if(activateStrat==null){return null;}
		else return activateStrat.activate(p);
	}
	
	/**
	 * use another item on this one
	 * @param i item being used on this item
	 * @return description of event "" if item i cannot be used on this
	 */
	public String useOnThis(Item i,Player using){
		if(i == null){return "";}
		if(useableOnThis.containsKey(i.getName())){
			return useableOnThis.get(i.getName()).usedOnThis(i,this,using);
		}
		return "";
	}
	
	/**
	 * parses an item from a string according to format of described in itemFormat.txt
	 * @param dat string used to parse an item
	 * @return if the string was valid or not
	 */
	private boolean parseItem(String dat){
		//split dat into its components defined in itemFormat.txt
		String[] data = dat.split("\\|",5);
		if(data.length != 5){JOptionPane.showConfirmDialog(null, "incorrect number of paramiters in item line : " + data.length, "Parsing error", JOptionPane.DEFAULT_OPTION);return false;}
		name = data[0].trim();
		disc = data[1];
		size = Integer.parseInt(data[2]);
		if(size < 0 ){JOptionPane.showConfirmDialog(null, "size must not be negitive", "Parsing error", JOptionPane.DEFAULT_OPTION);return false;}
		try {
			image = ImageIO.read(new File("itemImages/"+data[3]+ ".png"));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "Missing image itemImages/"+data[3]+".png", "Parsing error", JOptionPane.DEFAULT_OPTION);
			e.printStackTrace();
		}
		//split the last component into the "strategies" that is it composed of
		String[] strategies = data[4].split("/");
		/*
		 for each strategy split it into its components and check the first one
		 which specifies what "strategy" it is
		 do specific actions required for each different strategy
		*/
		for(String s : strategies){
			s = s.trim();
			if(s != null && s.length()>1){
				String[] strat = s.split("@", 4);
				if(strat != null){
					if(strat[0] != null){
						if(strat[0].equalsIgnoreCase("ChangeItem")){
							useableOnThis.put(strat[1], new ChangeItem(s));
						}
						else if(strat[0].equalsIgnoreCase("Lock")){
							this.locked = true;
							useableOnThis.put(strat[1], new Lock(s));
						}
						else if(strat[0].equalsIgnoreCase("Container")){
							container = new Container(size,this.name, s);
						}
						else if(strat[0].equalsIgnoreCase("DispenseItem")){
							activateStrat = new DispenseItem(s);
						}
						else if(strat[0].equalsIgnoreCase("BlockMovement")){
							this.blockMovement = true;
							useableOnThis.put(strat[1], new Lock(s));
						}
						else if(strat[0].equalsIgnoreCase("Consume")){
							useableOnThis.put(strat[1], new Consume(s));
						}
						else if(strat[0].equalsIgnoreCase("ChangeUsedItem")){
							useableOnThis.put(strat[1], new ChangeUsedItem(s));
						}
					}
				}
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

	public int getSize() {
		return size;
	}

	public Image getImage() {
		return image;
	}
	
	public int getID(){
		return id;
	}

	public HashMap<String,UsedOnThisStrategy> getUseableOnThis() {
		return useableOnThis;
	}
	
	/**
	 * compares the unique id of this item and a give
	 * @param i item being compared to this
	 * @return if the items are the same
	 */
	public boolean equals(Item i){
		return (i.getID()==this.id);
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDisc(String disc) {
		this.disc = disc;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public void setUseableOnThis(HashMap<String, UsedOnThisStrategy> useableOnThis) {
		this.useableOnThis = useableOnThis;
	}

}
