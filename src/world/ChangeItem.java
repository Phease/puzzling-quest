package world;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *an implementation of UsedOnThisStrategy for telling an item
 *to change its name,discription,size and image to match those of
 *an item given in this objects constructor when a item of a spesific name is 
 *used on the item containting this strategy
 */
public class ChangeItem implements UsedOnThisStrategy{
	private String name;
	private String requires;
	private String disc;
	private Item changeInto;
	
	/**
	 * @param s string used to parse the rqeuired variables to make a chnageItem
	 * format of this described in StrategyFormat.txt
	 */
	public ChangeItem(String s){
		String[] elements = s.split("@",4);
		name = elements[0];
		requires = elements[1];
		disc = elements[2];
		changeInto = new Item(elements[3]);
	}
	
	/**
	 * this implementation of usedOnThis changes elements of "This"
	 * into that of "changeInto" this is used to change items images,
	 * Descriptions, size or names when something is used on them.
	 */
	@Override
	public String usedOnThis(Item usedOn,Item This, Player using) {
		if(usedOn.getName().equalsIgnoreCase(requires)){
			if(This.getName().equalsIgnoreCase(requires)){
				//so an item requiring itself can't just have a item with the same name used on it
				if(!usedOn.equals(This)){return null;}
			}
			This.setName(changeInto.getName());
			This.setDisc(changeInto.getDisc());
			This.setSize(changeInto.getSize());
			This.setImage(changeInto.getImage());
			This.setUseableOnThis(changeInto.getUseableOnThis());
		}
		return disc;
	}

}
