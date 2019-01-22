package world;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *an implementation of ActivateStrategy for making an item
 *allow an item to add an instance of a specific item to a players inventory
 *whenever it is activated
 */
public class DispenseItem implements ActivateStrategy{
	private String name;
	private String disc;
	private String dispense;
	
	/**
	 * @param s string used to parse the required variables to make a DispenceItem
	 * format of this described in StrategyFormat.txt
	 */
	public DispenseItem(String s){
		String[] elements = s.split("@",3);
		name = elements[0];
		disc = elements[1];
		dispense = elements[2];
	}
	
	/**
	 * adds a new instance of a specific item into the
	 * activating players inventory
	 */
	@Override
	public String activate(Player p) {
		if(!p.getInventory().addItem(new Item(dispense))){return "cannot add to inventory";}
		else return disc;
	}

}
