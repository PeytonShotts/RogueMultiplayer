package Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Map.Map;
import MapGen.MapGen;
import Mob.*;
import Packet.*;
import Player.Player;
import Projectile.Projectile;
import Vector.Vector;


public class Server extends Listener {

	public static com.esotericsoftware.kryonet.Server server = new com.esotericsoftware.kryonet.Server(30000,30000);
	static final int port = 7777;
	
	public static List<Map> maps = new ArrayList<Map>();
	public static java.util.Map<Integer, Player> connectedPlayers = new ConcurrentHashMap<Integer, Player>();
	
	public static int tick = 0;
	
	public static int projectileCount = 0;
	public static int mobCount = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException{

		Map map0 = JsonConverter.convert("C:/Users/peyton/git/RogueMultiplayer/RogueMultiplayer/src/maps/courtyard_1.0.3.json");
		map0.spawnPoint.x = 60*32;
		map0.spawnPoint.y = 71*32;
		map0.tileset = "castle";
		maps.add(map0);
		map0.type = 0;
		Map map1 = MapGen.create(250, 250, 4);
		map1.tileset = "owlish";
		maps.add(map1);
		map1.type = 1;
		
		server.getKryo().register(PacketMapData.class);
		server.getKryo().register(PacketMapRequest.class);
		server.getKryo().register(PacketAddPlayer.class);
		server.getKryo().register(PacketRemovePlayer.class);
		server.getKryo().register(PacketUpdatePlayerPosition.class);
		server.getKryo().register(PacketUpdatePlayerSprite.class);
		server.getKryo().register(PacketPlayerLeaveMap.class);
		server.getKryo().register(PacketPlayerEnterMap.class);
		server.getKryo().register(PacketPlayerHit.class);
		server.getKryo().register(PacketGetPlayerName.class);
		server.getKryo().register(PacketSetPlayerName.class);
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
		server.addListener(new Server());
		System.out.println("Server Ready");	
		System.out.println(map1.mobs.size());	
		
		long taskTime = 0;
		long sleepTime = 1000/60;
		
		/*
		for (int spawn=0; spawn< 5; spawn++)
		{
			Mob newMob = new Chicken();
			newMob.position.x = 55*32+(spawn*33); newMob.position.y = 65*32;
			spawnMob(newMob, map0);
		}
		*/
		
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
			
			for (Player player : connectedPlayers.values())
			{
				player.serverUpdate();
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
					
					Thread.sleep((long) 3);
			}
			
			
			
		} catch (InterruptedException e) {
				e.printStackTrace();}
	}
	
	public static void updateProjectiles(Map map) {
		
		for (Projectile projectile : map.projectiles.values())
		{	
			for (int colX = (int) ((projectile.position.x/32) - 1); colX < (projectile.position.x/32) + 2; colX++)
			{
				for (int colY = (int) ((projectile.position.y/32) - 1); colY < (projectile.position.y/32) + 2; colY++)
				{
					if (colX > 0 && colX < map.width &&
							colY > 0 && colY < map.height)
					{
						for (int type : Mob.collisionList)
						{
							if ( map.layers[0].data[colX][colY] == type)
							{
								if (projectile.position.x < colX*32 + 32 &&
									projectile.position.x + projectile.size > colX*32 &&
									projectile.position.y < colY*32 + 32 &&
									projectile.size + projectile.position.y > colY*32) 
								{
									PacketRemoveProjectile packet = new PacketRemoveProjectile();
									packet.id = projectile.id;
									server.sendToAllUDP(packet);
									map.projectiles.remove(projectile.id);
								}
							}
						}

					}
				}
			}
			
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
			boolean playerNearby = false;
			for (Player player : connectedPlayers.values())
			{
				if (Math.hypot(mob.getValue().position.x - player.x, mob.getValue().position.y - player.y) < 320)
				{
					playerNearby = true;
					break;
				}
			}
			if (playerNearby == true)
			{
				mob.getValue().update(map, map.projectiles);
				if (mob.getValue().health <= 0)
				{
					map.mobs.remove(mob.getKey());
					PacketRemoveMob mobRemove = new PacketRemoveMob();
					mobRemove.id = mob.getKey();
					
					for (Player player : map.players.values())
					{
						server.sendToTCP(player.connectionID, mobRemove);
					}
				}
				else if (mob.getValue().health != mob.getValue().networkHealth)
				{
					PacketUpdateMobHealth packet = new PacketUpdateMobHealth();
					packet.id = mob.getKey();
					packet.health = mob.getValue().health;
					for (Player player : map.players.values())
					{
						server.sendToTCP(player.connectionID, packet);
					}
					
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
					
					for (Player player : map.players.values())
					{
						server.sendToUDP(player.connectionID, mobUpdate);
					}
					
					mob.getValue().networkPosition.x = mob.getValue().position.x;
					mob.getValue().networkPosition.y = mob.getValue().position.y;
				}
			}
		}
		
	}
	
	public void connected(Connection c){
		int defaultMap = 0;
		
		Player player = new Player();
		player.x = maps.get(defaultMap).spawnPoint.x;
		player.y = maps.get(defaultMap).spawnPoint.y;
		player.connectionID = c.getID();
		
		//send map to new player
		sendMap(c, maps.get(defaultMap));
		
		//add new player to map's player array
		maps.get(defaultMap).players.put(c.getID(), player);
		
		//add new player to server's player array
		connectedPlayers.put(c.getID(), player);
		connectedPlayers.get(c.getID()).mapID = defaultMap;
		
		
		//send new player data to other connected clients
		PacketAddPlayer newPlayerPacket = new PacketAddPlayer();
		newPlayerPacket.id = c.getID();
		newPlayerPacket.x = (int) maps.get(defaultMap).spawnPoint.x;
		newPlayerPacket.y = (int) maps.get(defaultMap).spawnPoint.y;
		newPlayerPacket.name = connectedPlayers.get(c.getID()).name;
		server.sendToAllExceptTCP(c.getID(), newPlayerPacket);
		
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
		
		//get player name
		PacketGetPlayerName getName = new PacketGetPlayerName();
		server.sendToTCP(c.getID(), getName);
		
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
		else if (o instanceof PacketGetPlayerName)
		{
			PacketGetPlayerName packet = (PacketGetPlayerName) o;
			connectedPlayers.get(c.getID()).name = packet.name;
			
			PacketSetPlayerName packet2 = new PacketSetPlayerName();
			packet2.id = c.getID();
			packet2.name = packet.name;
			server.sendToAllExceptTCP(c.getID(), packet2);
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
		//map.mobs.put(mobCount, mob);
		//mobCount++;
		
	}
}
