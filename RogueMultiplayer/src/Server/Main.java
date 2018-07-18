package Server;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import MapCode.*;
import Mob.*;
import Projectile.*;
import Vector.Vector;
import Packet.*;


public class Main extends Listener {

	static Server server = new Server(30000,30000);
	static final int port = 7777;
	
	
	static java.util.Map<Integer, Map> maps = new HashMap<Integer, Map>();
	static java.util.Map<Integer, Player> players = new HashMap<Integer, Player>();
	static java.util.Map<Integer, Mob> mobs = new ConcurrentHashMap<Integer, Mob>();
	static java.util.Map<Integer, Projectile> projectiles = new ConcurrentHashMap<Integer, Projectile>();
	static int mobCount = 0;
	
	public static int currentMap = 1;
	
	
	public static int tick = 0;
	public static int projectileCount = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException{

		Map map1 = JsonConverter.convert("C:/Users/p05119/Desktop/newfolder2/jsonmap.json");
		maps.put(1, map1);
		Map map2 = JsonConverter.convert("C:/Users/p05119/Desktop/newfolder2/jsonmap2.json");
		maps.put(2, map2);
		
		
		server = new Server();
		server.getKryo().register(PacketMapData.class);
		server.getKryo().register(PacketMapRequest.class);
		server.getKryo().register(PacketAddPlayer.class);
		server.getKryo().register(PacketRemovePlayer.class);
		server.getKryo().register(PacketUpdatePlayerPosition.class);
		server.getKryo().register(PacketUpdatePlayerSprite.class);
		server.getKryo().register(Mob.class);
		server.getKryo().register(PacketAddMob.class);
		server.getKryo().register(PacketRemoveMob.class);
		server.getKryo().register(PacketUpdateMob.class);
		server.getKryo().register(PacketUpdateMobHealth.class);
		server.getKryo().register(PacketAddProjectile.class);
		server.getKryo().register(PacketRemoveProjectile.class);
		server.getKryo().register(Vector.class);
		server.getKryo().register(Projectile.class);
		server.getKryo().register(byte[].class);
		server.getKryo().register(int[].class);
		server.getKryo().register(int[][].class);
		server.bind(port, port);
		server.start();
		server.addListener(new Main());
		System.out.println("Server Ready");	
		
		
		int i = 0;
		
		long taskTime = 0;
		long sleepTime = 1000/60;
		
		for (int spawn=0; spawn< 50; spawn++)
		{
			Mob newMob = new Chicken();
			newMob.position.x = 47*32; newMob.position.y = 47*32;
			addMob(newMob);
		}
		
		
		while (true)
		{
			  taskTime = System.currentTimeMillis();
			  taskTime = System.currentTimeMillis()-taskTime;
			  if (sleepTime-taskTime > 0 ) {
			    Thread.sleep(sleepTime-taskTime);
			  }
			i++;
			
			//update mobs
			updateMobs();

			//update projectiles
			updateProjectiles();
			
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
	
	public static void updateProjectiles() {
		
		for (Projectile projectile : projectiles.values())
		{
			if (projectile.time == 0)
			{
				PacketRemoveProjectile packet = new PacketRemoveProjectile();
				packet.id = projectile.id;
				server.sendToAllUDP(packet);
				
				projectiles.remove(projectile.id);
			}
			projectile.update();
		}
		
	}
	
	public static void updateMobs()
	{
		for (Entry<Integer, Mob> mob : mobs.entrySet())
		{
			mob.getValue().update(maps.get(currentMap), projectiles);
			if (mob.getValue().health <= 0)
			{
				mobs.remove(mob.getKey());
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
		
		Player player = new Player();
		player.x = maps.get(1).spawnPoint.x;
		player.y = maps.get(1).spawnPoint.y;
		player.c = c;
		
		
		//send new player data to other connected clients
		PacketAddPlayer newPlayerPacket = new PacketAddPlayer();
		newPlayerPacket.id = c.getID();
		newPlayerPacket.x = (int) maps.get(1).spawnPoint.x;
		newPlayerPacket.y = (int) maps.get(1).spawnPoint.y;
		server.sendToAllExceptTCP(c.getID(), newPlayerPacket);
		
		
		for(Player p : players.values()){
			PacketAddPlayer packet2 = new PacketAddPlayer();
			packet2.id = p.c.getID();
			packet2.x = (int) p.x;
			packet2.y = (int) p.y;
			c.sendTCP(packet2);
		}
		
		//add new player to server array
		players.put(c.getID(), player);
		
		//send map to new player
		sendMap(c, maps.get(1));
		
		//send mobs to new player
		for (Entry<Integer, Mob> mob : mobs.entrySet())
		{
			PacketAddMob mobPacket = new PacketAddMob();
			mobPacket.position.x = mob.getValue().position.x;
			mobPacket.position.y = mob.getValue().position.y;
			mobPacket.health = mob.getValue().health;
			mobPacket.maxHealth = mob.getValue().maxHealth;
			mobPacket.id = mob.getKey();
			
			c.sendTCP(mobPacket);
		}
		
		System.out.println("Connection received.");

		
	}
	
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdatePlayerPosition){
			PacketUpdatePlayerPosition packet = (PacketUpdatePlayerPosition) o;
			players.get(c.getID()).x = packet.x;
			players.get(c.getID()).y = packet.y;
			
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
		}
		else if(o instanceof PacketUpdatePlayerSprite){
			PacketUpdatePlayerSprite packet = (PacketUpdatePlayerSprite) o;
			
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
		}
		else if(o instanceof PacketAddProjectile){
			PacketAddProjectile packet = (PacketAddProjectile) o;
			packet.projectile.id = projectileCount;
			server.sendToAllUDP(packet);
			
			projectiles.put(projectileCount, packet.projectile);
			projectileCount++;
			
		}
		else if (o instanceof PacketMapRequest)
		{
			PacketMapRequest packet = (PacketMapRequest) o;
			sendMap(c, maps.get(packet.mapID));
		}
		
	}
	
	public void disconnected(Connection c){
		
		players.remove(c.getID());
		PacketRemovePlayer packet = new PacketRemovePlayer();
		packet.id = c.getID();
		server.sendToAllTCP(packet);

	}
	
	public static void addMob(Mob mob)
	{
		mobs.put(mobCount, mob);
		mobCount++;
	}
}
