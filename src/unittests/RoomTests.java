package unittests;

import static org.junit.Assert.*;
import org.junit.Test;

import world.Room;
import world.World;
import world.Item;
import world.Player;

/**
 * tests methods of the room class.
 * @author hammatalex 300327355 Alexander Hammatt
 */
public class RoomTests
{
	/**
	 * test that an item can be added to a room and found in that room.
	 * also tests getItem.
	 */
	@Test
	public void testAddItemGetItem()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		assertTrue(room.addItem(item1));
		assertTrue(room.getItem(0, 0).equals(item1));
	}

	/**
	 * test that the other addItem method used by parsers and other internals functions correctly by placing an item and picking it back up.
	 * test that this method has not placed the item somewhere it shouldn't have
	 */
	@Test
	public void testAddItemDirect()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		assertTrue(room.addItem(item1, 1, 3));
		assertTrue(room.getItem(1,3).equals(item1));
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				if (i!=1&&j!=3) assertTrue(room.getItem(i, j)==(null));
			}
		}
	}

	/**
	 * test that too many items cannot be put into a room.
	 */
	@Test
	public void testAddItemFull()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		for (int i = 0; i < 25; i++)
		{
			assertTrue(room.addItem(item1));
		}
		assertFalse(room.addItem(item1));
	}

	/**
	 * test that an item can be added to the wall and then exists on that wall.
	 */
	@Test
	public void testAddWallItem()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		room.addWallItem(item1, World.Direction.NORTH.ordinal());
		assertEquals(room.getWallItem(World.Direction.NORTH.ordinal()), item1);
	}

	/**
	 * tests that multiple items can't be put on the same wall and that the original item stays there.
	 */
	@Test
	public void testAddWallItemFull()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Item item2 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		room.addWallItem(item1, World.Direction.NORTH.ordinal());
		assertFalse(room.addWallItem(item2, World.Direction.NORTH.ordinal()));
		assertEquals(room.getWallItem(World.Direction.NORTH.ordinal()), item1);
	}

	/**
	 * test that an item is correctly removed from a room and that the correct item is returned.
	 */
	@Test
	public void testRemoveItem()
	{
		Item item1 = new Item("apple|a|1|apple|");
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		room.addItem(item1);

		Player p = new Player();

		assertEquals(room.removeItem(0, 0, p), item1);
		assertNull(room.getItem(0, 0));
	}

	/**
	 * check that the room is correctly empty upon creation and that the removeItem does not fail on trying to remove nothing.
	 */
	@Test
	public void testRemoveItemNoItem()
	{
		Room room = new Room(0, 0, "test|testdesc|stonebrickwall2|stonebrickwall2|stonebrickwall2|stonebrickwall2|oakfloor|roof");
		Player p = new Player();
		assertNull(room.removeItem(0, 0, p));
	}
}
