package world;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *a implementation of UsedOnThisStrategy for telling an item
 *to lock its container so that a player cannot open a conatiner within
 *the item this is part of
 *it also provides the ability to unlock the container of the item this is part of
 */
public class Lock implements UsedOnThisStrategy{
	private String name;
	private String required;
	private String disc;
	private String unLockedDisc;
	private Image unLockedImage;
	
	/**
	 * @param s string used to parse the required variables to make a Lock
	 * format of this described in StrategyFormat.txt
	 */
	public Lock(String s){
		String[] elements = s.split("@", 5);
		name = elements[0];
		required = elements[1];
		disc = elements[2];
		unLockedDisc = elements[3];
		try {
			unLockedImage = ImageIO.read(new File("itemImages/"+elements[4]+ ".png"));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "Missing image itemImages/"+elements[4]+".png", "Parsing error", JOptionPane.DEFAULT_OPTION);
			e.printStackTrace();
		}
	}
	
	/**
	 * this implementation of usedOnThis
	 * unlocks or unblocks "This" when a specific item is used on it
	 * unlocking or unblocking is decided by the name variable of this object.
	 */
	@Override
	public String usedOnThis(Item i, Item This, Player using) {
		if(i.getName().equalsIgnoreCase(required)){
			if(name.equalsIgnoreCase("Lock")){This.unlock();}
			else if (name.equalsIgnoreCase("BlockMovement")){This.unBlockMovement();}
			else {return(null);}
			This.setImage(unLockedImage);
			This.setDisc(unLockedDisc);
			return disc;
		}
		return null;
	}
	
	
}
