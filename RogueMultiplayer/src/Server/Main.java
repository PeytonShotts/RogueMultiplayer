package Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import MapCode.*;
import MapGen.MapGen;
import Mob.*;
import Projectile.*;
import Vector.Vector;
import Packet.*;
import Player.*;


public class Main extends Listener {

	static Server server = new Server(30000,30000);
	static final int port = 7777;
	
	public static List<Map> maps = new ArrayList<Map>();
	public static java.util.Map<Integer, Player> connectedPlayers = new HashMap<Integer, Player>();
	
	public static int tick = 0;
	
	public static int projectileCount = 0;
	public static int mobCount = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException{

		Map map0 = JsonConverter.convert("C:/Users/p05119/Desktop/newfolder2/jsonmap.json");
		maps.add(map0);
		//Map map1 = JsonConverter.convert("C:/Users/p05119/Desktop/newfolder2/jsonmap2.json");
		//maps.add(map1);
		Map map1 = MapGen.create(250, 250, 4);
		maps.add(map1);
		
		server = new Server();
		server.getKryo().register(PacketMapData.class);
		server.getKryo().register(PacketMapRequest.class);
		server.getKryo().register(PacketAddPlayer.class);
		server.getKryo().register(PacketRemovePlayer.class);
		server.getKryo().register(PacketUpdatePlayerPosition.class);
		server.getKryo().register(PacketUpdatePlayerSprite.class);
		server.getKryo().register(PacketPlayerLeaveMap.class);
		server.getKryo().register(PacketPlayerEnterMap.class);
		server.getKryo().register(Mob.class);
		server.getKryo().register(PacketAddMob.class);
		server.getKryo().register(PacketRemoveMob.class);
		server.getKryo().register(PacketUpdateMob.class);
		server.getKryo().register(PacketUpdateMobHealth.class);
		server.getKryo().register(PacketAddProjectile.class);
		server.getKryo().register(PacketRemoveProjectile.class);
		server.getKryo().register(Vector.class);
		server.getKryo().register(Projectile.class);
		server.getKryo().register(Player.class);
		server.getKryo().register(byte[].class);
		server.getKryo().register(int[].class);
		server.getKryo().register(int[][].class);
		server.getKryo().register(java.util.ArrayList.class);
		server.bind(port, port);
		server.start();
		server.addListener(new Main());
		System.out.println("Server Ready");	
		
		
		int i = 0;
		
		long taskTime = 0;
		long sleepTime = 1000/50;
		
		
		for (int spawn=0; spawn< 50; spawn++)
		{
			Mob newMob = new Chicken();
			newMob.position.x = 47*32; newMob.position.y = 47*32;
			spawnMob(newMob, map0);
		}
		
		
		while (true)
		{
			  taskTime = System.currentTimeMillis();
			  taskTime = System.currentTimeMillis()-taskTime;
			  if (sleepTime-taskTime > 0 ) {
			    Thread.sleep(sleepTime-taskTime);
			  }
			
			for (Map map : maps)
			{
				updateMobs(map);
				updateProjectiles(map);
			}
		}
		
	}
	
	public static void sendMap(Connection c, Map map)
	{
		//send map data
		try {
			byte[] mapByteData = SerializationUtils.serialize(map);
			
			int remainingData = mapByteData.length;
			
			int bytePosition = 0;
			int packetSize = 1000;
			while (remainingData > 0)
			{
					PacketMapData packet = new PacketMapData();

					packet.bytePosition = bytePosition;
					
					if (remainingData < packetSize)
					{
						packetSize = remainingData;
					}
					for (int l=0;l<packetSize;l++)
					{
						packet.data[l] = mapByteData[bytePosition+l];
					}
					
					if (remainingData == packetSize)
					{
						packet.finalPacket = true;
					}
					else
					{
						packet.finalPacket = false;
					}
					
					
					c.sendTCP(packet);
					remainingData += -packetSize;
					bytePosition += packetSize;
					
					Thread.sleep((long) 5);
			}
			
			
			
		} catch (InterruptedException e) {
				e.printStackTrace();}
	}
	
	public static void updateProjectiles(Map map) {
		
		for (Projectile projectile : map.projectiles.values())
		{
			if (projectile.time == 0)
			{
				PacketRemoveProjectile packet = new PacketRemoveProjectile();
				packet.id = projectile.id;
				server.sendToAllUDP(packet);
				
				map.projectiles.remove(projectile.id);
			}
			projectile.update();
		}
		
	}
	
	public static void updateMobs(Map map)
	{	
		for (Entry<Integer, Mob> mob : map.mobs.entrySet())
		{
			mob.getValue().update(map, map.projectiles);
			if (mob.getValue().health <= 0)
			{
				map.mobs.remove(mob.getKey());
				PacketRemoveMob mobRemove = new PacketRemoveMob();
				mobRemove.id = mob.getKey();
				server.sendToAllTCP(mobRemove);
			}
			else if (mob.getValue().health != mob.getValue().networkHealth)
			{
				PacketUpdateMobHealth packet = new PacketUpdateMobHealth();
				packet.id = mob.getKey();
				packet.health = mob.getValue().health;
				server.sendToAllTCP(packet);
				
				mob.getValue().networkHealth = mob.getValue().health;
				
			}
			else if (mob.getValue().position.x != mob.getValue().networkPosition.x |
				mob.getValue().position.y != mob.getValue().networkPosition.y)
			{
				PacketUpdateMob mobUpdate = new PacketUpdateMob();
				mobUpdate.id = mob.getKey();
				mobUpdate.position.x = mob.getValue().position.x;
				mobUpdate.position.y = mob.getValue().position.y;
				mobUpdate.spriteX = (byte) mob.getValue().spriteX;
				mobUpdate.spriteY = (byte) mob.getValue().spriteY;
				
				server.sendToAllUDP(mobUpdate);
				
				mob.getValue().networkPosition.x = mob.getValue().position.x;
				mob.getValue().networkPosition.y = mob.getValue().position.y;
			}
		}
		
	}
	
	public void connected(Connection c){
		int defaultMap = 0;
		
		Player player = new Player();
		player.x = maps.get(defaultMap).spawnPoint.x;
		player.y = maps.get(defaultMap).spawnPoint.y;
		player.connectionID = c.getID();
		
		//send new player data to other connected clients
		PacketAddPlayer newPlayerPacket = new PacketAddPlayer();
		newPlayerPacket.id = c.getID();
		newPlayerPacket.x = (int) maps.get(defaultMap).spawnPoint.x;
		newPlayerPacket.y = (int) maps.get(defaultMap).spawnPoint.y;
		server.sendToAllExceptTCP(c.getID(), newPlayerPacket);
		
		//send map to new player
		sendMap(c, maps.get(defaultMap));
		
		//add new player to map's player array
		maps.get(defaultMap).players.put(c.getID(), player);
		
		//add new player to server's player array
		connectedPlayers.put(c.getID(), player);
		connectedPlayers.get(c.getID()).mapID = defaultMap;
		
		/*
		//send mobs to new player
		for (Entry<Integer, Mob> mob : maps.get(1).mobs.entrySet())
		{
			PacketAddMob mobPacket = new PacketAddMob();
			mobPacket.position.x = mob.getValue().position.x;
			mobPacket.position.y = mob.getValue().position.y;
			mobPacket.health = mob.getValue().health;
			mobPacket.maxHealth = mob.getValue().maxHealth;
			mobPacket.id = mob.getKey();
			
			c.sendTCP(mobPacket);
		}
		*/
		
		System.out.println("Connection received." + c.getID());

		
	}
	
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdatePlayerPosition){
			
			PacketUpdatePlayerPosition packet = (PacketUpdatePlayerPosition) o;
			int mapID = connectedPlayers.get(c.getID()).mapID;
			maps.get(mapID).players.get(c.getID()).x = packet.x;
			maps.get(mapID).players.get(c.getID()).y = packet.y;
			
			packet.id = c.getID();

			for (Player player : maps.get(mapID).players.values())
			{
				if (player.connectionID != packet.id)
					{
						server.sendToUDP(player.connectionID, packet);
					}
			}
			
			
		}
		else if(o instanceof PacketUpdatePlayerSprite){
			
			PacketUpdatePlayerSprite packet = (PacketUpdatePlayerSprite) o;
			int mapID = connectedPlayers.get(c.getID()).mapID;
			packet.id = c.getID();
			
			for (Player player : maps.get(mapID).players.values())
			{
				if (player.connectionID != packet.id)
					{
						server.sendToUDP(player.connectionID, packet);
					}
			}
			
			
		}
		else if(o instanceof PacketAddProjectile){
			
			PacketAddProjectile packet = (PacketAddProjectile) o;
			int mapID = connectedPlayers.get(c.getID()).mapID;
			packet.projectile.id = projectileCount;
			maps.get(mapID).projectiles.put(projectileCount, packet.projectile);
			projectileCount++;
			
			for (Player player : maps.get(mapID).players.values())
			{
				server.sendToUDP(player.connectionID, packet);
			}
			
		}
		else if (o instanceof PacketMapRequest)
		{
			PacketMapRequest packet = (PacketMapRequest) o;
			sendMap(c, maps.get(packet.mapID));
			//remove player from current map's hashmap
			int oldMapID = connectedPlayers.get(c.getID()).mapID;
			maps.get(oldMapID).players.remove(c.getID());
			//add player to new map's hashmap
			int newMapID = packet.mapID;
			maps.get(newMapID).players.put(c.getID(), connectedPlayers.get(c.getID()));
			connectedPlayers.get(c.getID()).mapID = newMapID;
			
			PacketPlayerLeaveMap packet2 = new PacketPlayerLeaveMap();
			packet2.id = c.getID();
			for (Player player : maps.get(oldMapID).players.values())
			{
				server.sendToTCP(player.connectionID, packet2);
			}
			
			PacketPlayerEnterMap packet3 = new PacketPlayerEnterMap();
			packet3.id = c.getID();
			packet3.newPlayer = connectedPlayers.get(c.getID());
			for (Player player : maps.get(newMapID).players.values())
			{
				if (player.connectionID != c.getID())
					{
						server.sendToTCP(player.connectionID, packet3);
					}
			}
		}
		
	}
	
	public void disconnected(Connection c){
		
		int mapID = connectedPlayers.get(c.getID()).mapID;
		maps.get(mapID).players.remove(c.getID());
		PacketRemovePlayer packet = new PacketRemovePlayer();
		packet.id = c.getID();
		server.sendToAllTCP(packet);
		
		for (Player player : maps.get(mapID).players.values())
		{
			if (player.connectionID != c.getID())
				{
					server.sendToTCP(player.connectionID, packet);
				}
		}
		
	}
	
	public static void spawnMob(Mob mob, Map map)
	{
		map.mobs.put(mobCount, mob);
		mobCount++;
		
	}
}
