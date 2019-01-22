package world;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *the strategy items use to trigger different event when activated
 */
public interface ActivateStrategy {
	
	/**
	 * activate an item
	 * @param p player activating the item
	 * @return description of event taking place or null/"" if event not possible
	 */
	public String activate(Player p);
}
