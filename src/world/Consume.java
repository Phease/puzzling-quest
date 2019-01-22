package world;

/**
 *@author mooneyandr 300327644 Andrew Mooney
 *an implementation of UsedOnThisStrategy for telling an item
 *to remove itself from a players inventory when it is used on itself
 *(only possible by the consume command)
 */
public class Consume implements UsedOnThisStrategy{
	private String name;
	private String required;
	private String disc;
	private int scoreInc;
	private boolean strUpgrade;
	
	/**
	 * @param s string used to parse the required variables to make a ConsumeItem
	 * format of this described in StrategyFormat.txt
	 */
	public Consume(String s){
		String[] elements = s.split("@", 6);
		name = elements[0];
		required = elements[1];
		disc = elements[2];
		scoreInc = Integer.parseInt(elements[3]);
		strUpgrade = Boolean.parseBoolean(elements[4]);
	}
	
	/**
	 * this implementation of usedOnThis that removes
	 * "This" from the inventory of the player "using"
	 * also may increase that players inventorySize(largest thing they can carry)
	 * also may increase that players score
	 */
	@Override
	public String usedOnThis(Item i, Item This, Player using) {
		if(i.getName().equalsIgnoreCase(required) && i.equals(This)){
			if(using.getInventory().removeItem(This)){
				using.addScore(scoreInc);
				if(strUpgrade){using.getInventory().incSize();}
				return disc;
			}
		}
		return null;
	}

}
