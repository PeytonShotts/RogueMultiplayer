package Server;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import Client.PacketAddMob;
import Client.PacketUpdateMob;
import MapCode.*;
import Mob.*;
import Projectile.Projectile;
import Vector.Vector;


public class Main extends Listener {

	static Server server = new Server(30000,30000);
	static final int port = 7777;
	
	static java.util.Map<Integer, Player> players = new HashMap<Integer, Player>();
	static java.util.Map<Integer, Mob> mobs = new HashMap<Integer, Mob>();
	static java.util.Map<Integer, Projectile> projectiles = new HashMap<Integer, Projectile>();
	static int mobCount = 0;
	
	static Map newMap = JsonConverter.convert("C:/Users/Peyton/Desktop/jsonmap.json");
	
	public static int tick = 0;
	public static int projectileCount = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		
		
		server = new Server();
		server.getKryo().register(PacketMapData.class);
		server.getKryo().register(PacketAddPlayer.class);
		server.getKryo().register(PacketUpdatePlayerPosition.class);
		server.getKryo().register(PacketUpdatePlayerSprite.class);
		server.getKryo().register(Mob.class);
		server.getKryo().register(PacketAddMob.class);
		server.getKryo().register(PacketUpdateMob.class);
		server.getKryo().register(PacketAddProjectile.class);
		server.getKryo().register(Vector.class);
		server.getKryo().register(Projectile.class);
		server.getKryo().register(byte[].class);
		server.getKryo().register(int[].class);
		server.getKryo().register(int[][].class);
		server.bind(port, port);
		server.start();
		server.addListener(new Main());
		System.out.println("The server is ready.");	
		
		
		byte[] mapByteData = ByteArrayConverter.convert(newMap);
		System.out.println(mapByteData.length);
		
		System.out.println(newMap.width);
		System.out.println(newMap.height);
		
		int i = 0;
		
		long taskTime = 0;
		long sleepTime = 1000/60;
		
		Mob newMob = new Mob();
		newMob.x = 44*32; newMob.y = 44*32;
		addMob(newMob);
		
		while (true)
		{
			  taskTime = System.currentTimeMillis();
			  taskTime = System.currentTimeMillis()-taskTime;
			  if (sleepTime-taskTime > 0 ) {
			    Thread.sleep(sleepTime-taskTime);
			  }
			i++;
			
			//update mobs
			for (Mob mob : mobs.values())
			{
				mob.update(newMap, projectiles);
			}
			//update projectiles
			updateProjectiles();
			
			PacketUpdateMob mobUpdate = new PacketUpdateMob();
			mobUpdate.x = mobs.get(0).x;
			mobUpdate.y = mobs.get(0).y;
			
			server.sendToAllUDP(mobUpdate);
			
			//System.out.println(newMob.x);
		}
		
	}
	
	public static void updateProjectiles() {
		
		for (Projectile projectile : projectiles.values())
		{
			if (projectile.time == 0)
			{
				projectiles.remove(projectile.id);
			}
			projectile.update();
		}
		
	}
	
	public void connected(Connection c){
		
		Player player = new Player();
		player.x = newMap.spawnPoint.x;
		player.y = newMap.spawnPoint.y;
		player.c = c;
		
		
		//send new player data to other connected clients
		PacketAddPlayer newPlayerPacket = new PacketAddPlayer();
		newPlayerPacket.id = c.getID();
		newPlayerPacket.x = (int) newMap.spawnPoint.x;
		newPlayerPacket.y = (int) newMap.spawnPoint.y;
		server.sendToAllExceptTCP(c.getID(), newPlayerPacket);
		
		for(Player p : players.values()){
			PacketAddPlayer packet2 = new PacketAddPlayer();
			packet2.id = p.c.getID();
			packet2.x = (int) newMap.spawnPoint.x;
			packet2.y = (int) newMap.spawnPoint.y;
			c.sendTCP(packet2);
		}
		
		//add new player to server array
		players.put(c.getID(), player);
		
		//send mobs to new player
		PacketAddMob mobPacket = new PacketAddMob();
		mobPacket.x = mobs.get(0).x;
		mobPacket.y = mobs.get(0).y;
		mobPacket.id = 0;
		c.sendTCP(mobPacket);
		
		System.out.println("Connection received.");
		
		//send map data
		try {
			byte[] mapByteData = SerializationUtils.serialize(newMap);
			
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
	
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdatePlayerPosition){
			PacketUpdatePlayerPosition packet = (PacketUpdatePlayerPosition) o;
			players.get(c.getID()).x = packet.x;
			players.get(c.getID()).y = packet.y;
			
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
			System.out.println("sending player position.");
		}
		else if(o instanceof PacketUpdatePlayerSprite){
			PacketUpdatePlayerSprite packet = (PacketUpdatePlayerSprite) o;
			
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
			System.out.println("sending player sprite.");
		}
		else if(o instanceof PacketAddProjectile){
			PacketAddProjectile packet = (PacketAddProjectile) o;
			
			server.sendToAllExceptTCP(c.getID(), packet);
			projectiles.put(packet.projectile.id, packet.projectile);
			projectileCount++;
			
			System.out.println("sending projectile to players.");
		}
		
	}
	
	public void disconnected(Connection c){

	}
	
	public static void addMob(Mob mob)
	{
		mobs.put(mobCount, mob);
		mobCount++;
	}
}
