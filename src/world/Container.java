package world;

import java.util.ArrayList;

/**
 * @author mooneyandr 300327644 Andrew Mooney
 *class representing a container eather the players inventory or part of an item
 */
public class Container{
	int size;
	String unlockedBy;
	String name;
	ArrayList<Item> contains;
	
	/**
	 * @param maxSize size of the largest thing this container can hold + 1
	 * @param name of the container
	 * @param items that start within the container
	 */
	public Container(int maxSize,String name, String items){
		size = maxSize;
		this.name = name;
		contains = new ArrayList<Item>();
		parseContainer(items);
	}
	
	/**
	 * parser for the items within a container form a string
	 * @param s string describing the items that start within the container
	 * format of this described in StrategyFormat.txt
	 */
	private void parseContainer(String s){
		String[] elements = s.split("@",11);
		for(int i = 1; i< elements.length;i++){
			contains.add(new Item(elements[i]));
		}
	}
	public String getName(){return name;} 
	
	/**
	 * adds an item to the container 
	 * @param i item to add
	 * @return whether or not the item was able to be added to the container
	 */
	public boolean addItem(Item i){
		if(contains.size() >= World.CONTAINER_MAX || i.getSize() >= this.size){return false;}
		contains.add(i);
		return true;
	}
	
	/**
	 * removes an item from the container 
	 * @param i item to add
	 * @return whether or not the item was able to be removed from the container
	 */
	public boolean removeItem(Item i){
		if(!contains.contains(i)){return false;}
		contains.remove(i);
		return true;
	}
	
	public ArrayList<Item> getContents() {
		return contains;
	}
	
	public int getSize(){return size;}
	
	/**
	 * Increase size of container by 1
	 * (largest thing container can hold)
	 */
	public void incSize(){
		size = size+1;
	}
	
}
