package Client;

import java.util.LinkedList;

public class Map {
	
	int width;
	int height;
	
	Vector spawnPoint = new Vector();
	
	Tile[][] tileArray;

	LinkedList<Room> roomList = new LinkedList<Room>();
	LinkedList<Projectile> projectileList = new LinkedList<Projectile>();
	LinkedList<Mob> mobList = new LinkedList<Mob>();
	
	
	public Map(int mapWidth, int mapHeight)
	{
		this.tileArray = new Tile[mapWidth][mapHeight];
		
		for (int initY = 0; initY < mapHeight; initY++)
		{
			for (int initX = 0; initX < mapHeight; initX++)
			{
				tileArray[initX][initY] = new Tile();
			}
		}
		
		width = mapWidth;
		height = mapWidth;
	}
	
	public void addRoom(Room newRoom)
	{
		roomList.add(newRoom);
	}
	
	
}
