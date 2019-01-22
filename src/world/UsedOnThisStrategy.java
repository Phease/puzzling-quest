package world;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *the strategy items use to trigger different events when
 *a specific item is used on them
 */
public interface UsedOnThisStrategy {
	
	/**
	 * method to use a specific item on another item
	 * this can change either item or the player using them.
	 * @param i item being used on this
	 * @param This the item having something used on it
	 * @param using player using i on This
	 * @return description of event taking place or null/"" if event not possible
	 */
	public String usedOnThis(Item i,Item This,Player using);
}
