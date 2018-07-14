package MapCode;

import Client.Vector;

import java.util.ArrayList;
import java.util.List;

import Client.Mob;
import Client.Projectile;

public class Map implements java.io.Serializable{
	
	public int width;
	public int height;
	
	public int numLayers;
	
	public layer[] layers = new layer[4];
	public Vector spawnPoint = new Vector(45, 45);
	
	public List<Mob> mobList = new ArrayList<Mob>();
	public List<Projectile> projectileList = new ArrayList<Projectile>();
	
	public Map(int mapWidth, int mapHeight, int numLayers)
	{
		width = mapWidth;
		height = mapHeight;
		this.numLayers = numLayers;
		
		for (int i=0; i<numLayers; i++)
		{
			layers[i] = new layer(mapWidth, mapHeight);
		}
	}
	
	
	
}