package unittests;

import static org.junit.Assert.*;
import org.junit.Test;

import world.Item;
import world.Player;

/**
 * tests methods of the item class.
 * @author hammatalex 300327355 Alexander Hammatt
 */
public class ItemTests
{
	/**
	 * check that an item equals itself.
	 */
	@Test
	public void testEquals()
	{
		Item item1 = new Item("apple|a|1|apple|");
		assertTrue(item1.equals(item1));
	}

	/**
	 * check that two different items which are otherwise completely identical do not equal each other, as they are separate items.
	 */
	@Test
	public void testNotEqual()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|");
		assertFalse(item1.equals(item2));
	}

	/**
	 * check that two completely different items are not equal to each other.
	 */
	@Test
	public void testNotEqualDifferent()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item ("boulder|a|1|boulder|container");

		assertFalse(item1.equals(item2));
	}

	/**
	 * check that an item gotten by an item id is equal to the item which we used to get the ID.
	 */
	@Test
	public void testGetItemByID()
	{
		Item item1 = new Item("apple|a|1|apple|");
		assertTrue(Item.getItemByID(item1.getID()).equals(item1));
	}

	/**
	 * check that an item which is otherwise completely identical to the other is not equal to the other as it was gotten with a different ID.
	 */
	@Test
	public void testGetItemByIDFalse()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|");
		assertFalse(Item.getItemByID(item2.getID()).equals(item1));
	}

	/**
	 * test that the item is locked when created and then unlocked when the appropriate item is used on it.
	 */
	@Test
	public void testUnlock()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|lock@apple@asdf@a@boulder");

		assertTrue(item2.checkLocked());

		item2.useOnThis(item1, null);

		assertFalse(item2.checkLocked());
	}

	/**
	 * check that the item is locked before and after the wrong item is used on it.
	 */
	@Test
	public void testLocked()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|lock@boulder@asdf@a@boulder");

		assertTrue(item2.checkLocked());

		item2.useOnThis(item1,null);

		assertTrue(item2.checkLocked());
	}

	/**
	 * check that a container can correctly hold things smaller than its size and the item can then be gotten from that container.
	 */
	@Test
	public void testContainer()
	{
		Item item1 = new Item("apple|a|2|apple|container");
		Item item2 = new Item("apple|a|1|apple|");
		assertTrue(item1.open().addItem(item2));
		assertTrue(item1.open().getContents().contains(item2));
	}

	/**
	 * check that a container will correctly reject items equal to or larger than itself and the items are then not inside the container.
	 */
	@Test
	public void testContainerTooSmall()
	{
		Item item1 = new Item("apple|a|2|apple|container");
		Item item2 = new Item("apple|a|2|apple|");
		Item item3 = new Item("apple|a|3|apple|");
		assertFalse(item1.open().addItem(item2));
		assertFalse(item1.open().getContents().contains(item2));
		assertFalse(item1.open().addItem(item3));
		assertFalse(item1.open().getContents().contains(item3));
	}

	@Test
	public void testContainerRemove()
	{
		Item item1 = new Item("apple|a|2|apple|container");
		Item item2 = new Item("apple|a|1|apple|");
		assertTrue(item1.open().addItem(item2));
		assertTrue(item1.open().getContents().contains(item2));
		assertTrue(item1.open().removeItem(item2));//check it thinks it removed it
		assertFalse(item1.open().getContents().contains(item2));//check it actually removed it
		assertFalse(item1.open().removeItem(item2));//check it doesnt think it can remove something which was not there.
	}

	/**
	 * test the dispenser gives the proper message when used and puts the item into the players inventory.
	 */
	@Test
	public void testDispenser()
	{
		Player p = new Player();
		Item item1 = new Item("apple|a|1|apple|dispenseitem@a@apple|a|1|apple|");
		assertTrue(item1.activate(p).equals("a"));
		assertTrue(p.getInventory().getContents().get(0).getName().equals("apple"));
	}

	/**
	 * test the dispenser gives the proper message when used and puts the item into the players inventory.
	 */
	@Test
	public void testDispenserFull()
	{
		Player p = new Player();
		Item item1 = new Item("apple|a|1|apple|dispenseitem@a@apple|a|1|apple|");
		for (int i = 0; i < 10; i++) p.getInventory().addItem(item1);
		assertTrue(item1.activate(p).equals("cannot add to inventory"));
	}

	/**
	 * test that an item changes into the correct item when the correct item is used on it.
	 */
	@Test
	public void testItemChangeInto()
	{
		Item item1 = new Item("apple|a|1|apple|changeitem@apple3@a@apple2|a|1|apple|");
		Item item2 = new Item("apple3|a|1|apple|");
		item1.useOnThis(item2,null);
		assertTrue(item1.getName().equals("apple2"));
	}

	/**
	 * test that an item does not change when the incorrect item is used on it.
	 */
	@Test
	public void testItemChangeIntoWrong()
	{
		Item item1 = new Item("apple|a|1|apple|changeitem@apple3@a@apple2|a|1|apple|");
		Item item2 = new Item("apple2|a|1|apple|");
		item1.useOnThis(item2,null);
		assertFalse(item1.getName().equals("apple2"));
	}

	/**
	 * test that an item with changeitem on it cannot have an identicle item used on it, but can have itself used on it.
	 */
	@Test
	public void testItemChangeIntoSelf()
	{
		Item item1 = new Item("apple|a|1|apple|changeitem@apple@a@apple2|a|1|apple|");
		item1.useOnThis(item1,null);
		assertTrue(item1.getName().equals("apple2"));
		item1 = new Item("apple|a|1|apple|changeitem@apple@a@apple2|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|changeitem@apple@a@apple2|a|1|apple|");
		item1.useOnThis(item2,null);
		assertFalse(item1.getName().equals("apple2"));
	}

	/**
	 * test that a block movement item blocks movement until the specified item is used on it.
	 */
	@Test
	public void testBlockMovement()
	{
		Item item1 = new Item("apple|a|1|apple|BlockMovement@apple@a@a@apple");
		Item item2 = new Item("apple|a|1|apple|");
		assertTrue(item1.checkBlockMovement());
		Player p = new Player();
		item1.useOnThis(item2, p);
		assertFalse(item1.checkBlockMovement());
	}

	/**
	 * test that a block movement item blocks movement before and after having the incorrect item used on it.
	 */
	@Test
	public void testBlockMovementWrong()
	{
		Item item1 = new Item("apple|a|1|apple|BlockMovement@apple@a@a@apple");
		Item item2 = new Item("apple2|a|1|apple|");
		assertTrue(item1.checkBlockMovement());
		Player p = new Player();
		item1.useOnThis(item2, p);
		assertTrue(item1.checkBlockMovement());
	}

	/**
	 * test that an item which exists in the players inventory which is set to alter the players carrying weight will do so on being consumed.
	 * test that the item will dissappear from the players inventory after being eaten
	 */
	@Test
	public void testConsume()
	{
		Item item1 = new Item("apple|a|1|apple|consume@apple@a@1@True");
		Player p = new Player();
		p.getInventory().addItem(item1);
		assertTrue(p.getInventory().getContents().contains(item1));
		int initialCarryingCapacity = p.getInventory().getSize();
		item1.useOnThis(item1, p);
		assertFalse(p.getInventory().getContents().contains(item1));
		int finalCarryingCapcity = p.getInventory().getSize();
		assertTrue(initialCarryingCapacity<finalCarryingCapcity);
	}

	/**
	 * test that an item can be used on itself, but not have an identicle item used on it, when it has Consume
	 */
	@Test
	public void testConsumeUsedItemSelf()
	{
		Item item1 = new Item("apple|a|1|apple|consume@apple@a@1@True");
		Player p = new Player();
		p.getInventory().addItem(item1);
		item1.useOnThis(item1, p);
		assertFalse(p.getInventory().getContents().contains(item1));
		Item item2 = new Item("apple|a|1|apple|consume@apple@a@1@True");
		p.getInventory().addItem(item1);
		p.getInventory().addItem(item2);
		item1.useOnThis(item2, p);
		assertTrue(p.getInventory().getContents().contains(item1));
		assertTrue(p.getInventory().getContents().contains(item2));
	}

	/**
	 * test that having a spesific item correctly used on an item changed the item which was used, not the item it was used on.
	 */
	@Test
	public void testChangeUsedItem()
	{
		Item item1 = new Item("apple|a|1|apple|ChangeUsedItem@apple2@a@apple3|a|1|apple|");
		Item item2 = new Item("apple2|a|1|apple|");
		Player p = new Player();
		item1.useOnThis(item2, p);
		assertTrue(item2.getName().equals("apple3"));
	}

	/**
	 * test that an having an incorrect item on it does not change the item used.
	 */
	@Test
	public void testChangeUsedItemWrong()
	{
		Item item1 = new Item("apple|a|1|apple|ChangeUsedItem@apple@a@apple3|a|1|apple|");
		Item item2 = new Item("apple2|a|1|apple|");
		Player p = new Player();
		item1.useOnThis(item2, p);
		assertFalse(item2.getName().equals("apple3"));
	}

	/**
	 * test that an item can be used on itself, but not have an identicle item used on it, when it has ChangeUsedItem
	 */
	@Test
	public void testChangeUsedItemSelf()
	{
		Item item1 = new Item("apple|a|1|apple|ChangeUsedItem@apple@a@apple3|a|1|apple|");
		Player p = new Player();
		item1.useOnThis(item1, p);
		assertTrue(item1.getName().equals("apple3"));
		item1 = new Item("apple|a|1|apple|ChangeUsedItem@apple@a@apple3|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|ChangeUsedItem@apple@a@apple3|a|1|apple|");
		item1.useOnThis(item2, p);
		assertFalse(item1.getName().equals("apple3"));
	}
}
