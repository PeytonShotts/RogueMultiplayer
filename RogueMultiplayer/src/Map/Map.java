package Map;

import Mob.Mob;
import Player.Player;
import Vector.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Image;

import Projectile.*;

public class Map implements java.io.Serializable{
	
	public int type = 0;
	
	public java.util.Map<Integer,Player> players = new ConcurrentHashMap<Integer,Player>(); 
	public java.util.Map<Integer,Mob> mobs = new ConcurrentHashMap<Integer,Mob>(); 
	public java.util.Map<Integer,Projectile> projectiles = new ConcurrentHashMap<Integer,Projectile>(); 
	
	public int width;
	public int height;
	public int layerCount;
	
	public layer[] layers;
	public Vector spawnPoint = new Vector(42*32, 42*32);
	
	public List<Mob> mobList = new ArrayList<Mob>();
	public List<Projectile> projectileList = new ArrayList<Projectile>();

	public String tileset;
	
	public Map(int mapWidth, int mapHeight, int layerCount)
	{
		width = mapWidth;
		height = mapHeight;
		this.layerCount = layerCount;
		layers = new layer[layerCount];
		
		for (int i=0; i<layerCount; i++)
		{
			layers[i] = new layer(mapWidth, mapHeight);
		}
	}
	
	
}