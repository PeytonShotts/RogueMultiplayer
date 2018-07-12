package Server;

import java.util.LinkedList;

public class Map {
	
	int width;
	int height;
	
	Vector spawnPoint = new Vector();
	
	Tile[][] tileArray;

	LinkedList<Room> roomList = new LinkedList<Room>();
	LinkedList<Projectile> projectileList = new LinkedList<Projectile>();
	//LinkedList<Mob> mobList = new LinkedList<Mob>();
	
	
	public Map(int mapWidth, int mapHeight)
	{
		tileArray = new Tile[mapWidth][mapHeight];
		width = mapWidth;
		height = mapWidth;
	}
	
	public void addRoom(Room newRoom)
	{
		roomList.add(newRoom);
	}
	
	
}
