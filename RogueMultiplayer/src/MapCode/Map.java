package MapCode;

import Mob.Mob;
import Player.Player;
import Vector.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Projectile.*;

public class Map implements java.io.Serializable{
	
	public int type = 0;
	
	public java.util.Map<Integer,Player> players = new HashMap<Integer,Player>(); 
	public java.util.Map<Integer,Mob> mobs = new ConcurrentHashMap<Integer,Mob>(); 
	public java.util.Map<Integer,Projectile> projectiles = new ConcurrentHashMap<Integer,Projectile>(); 
	
	public int width;
	public int height;
	
	public int numLayers;
	
	public layer[] layers = new layer[4];
	public Vector spawnPoint = new Vector(42*32, 42*32);
	
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