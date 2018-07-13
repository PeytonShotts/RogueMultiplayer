package Client;

import Server.Tile;
import Server.Vector;
import Server.layer;

public class Map implements java.io.Serializable{
	
	int width;
	int height;
	
	layer[] layers;
	
	Vector spawnPoint = new Vector();
	
	Tile[][] tileArray;
	
	
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
	
	
	
}