package Client;
import java.io.IOException;

import org.apache.commons.lang3.SerializationUtils;
import org.newdawn.slick.geom.RoundedRectangle;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Mob.*;
import Projectile.Projectile;
import Vector.Vector;
import Packet.*;
import ParticleCode.CircleExplosion;
import Player.Player;



public class Network extends Listener {

	public Client client;
	String ip = "73.177.127.130";
	int port = 7777;
	
	public void connect(){
		client = new Client(25000, 25000);
		client.getKryo().register(PacketMapData.class);
		client.getKryo().register(PacketMapRequest.class);
		client.getKryo().register(PacketAddPlayer.class);
		client.getKryo().register(PacketRemovePlayer.class);
		client.getKryo().register(PacketUpdatePlayerPosition.class);
		client.getKryo().register(PacketUpdatePlayerSprite.class);
		client.getKryo().register(PacketPlayerLeaveMap.class);
		client.getKryo().register(PacketPlayerEnterMap.class);
		client.getKryo().register(Mob.class);
		client.getKryo().register(PacketAddMob.class);
		client.getKryo().register(PacketRemoveMob.class);
		client.getKryo().register(PacketUpdateMob.class);
		client.getKryo().register(PacketUpdateMobHealth.class);
		client.getKryo().register(PacketAddProjectile.class);
		client.getKryo().register(PacketRemoveProjectile.class);
		client.getKryo().register(Vector.class);
		client.getKryo().register(Projectile.class);
		client.getKryo().register(Player.class);
		client.getKryo().register(byte[].class);
		client.getKryo().register(int[].class);
		client.getKryo().register(int[][].class);
		client.getKryo().register(java.util.ArrayList.class);
		client.addListener(this);
		
		client.start();
		try {
			client.connect(5000, ip, port, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void received(Connection c, Object o){
		if(o instanceof PacketMapData){
			PacketMapData packet = (PacketMapData) o;
			for (int l=0;l<1000;l++)
			{
				Main.mapBytes[packet.bytePosition+l] = packet.data[l];
			}
			
			System.out.println(packet.bytePosition);
			
			if (packet.finalPacket == true)
			{
				Main.currentMap = SerializationUtils.deserialize(Main.mapBytes);
				Main.mapLoaded = true;
				System.out.println("map loaded");
			}
			
			
		}
		else if(o instanceof PacketAddPlayer){
			PacketAddPlayer packet = (PacketAddPlayer) o;
			Player newPlayer = new Player();
			newPlayer.x = packet.x;
			newPlayer.y = packet.y;
			Main.currentMap.players.put(packet.id, newPlayer);
			
			System.out.println("new player joined");
		}else if(o instanceof PacketUpdatePlayerPosition){
			PacketUpdatePlayerPosition packet = (PacketUpdatePlayerPosition) o;
			Main.currentMap.players.get(packet.id).x = packet.x;
			Main.currentMap.players.get(packet.id).y = packet.y;
			
		}else if(o instanceof PacketUpdatePlayerSprite){
			PacketUpdatePlayerSprite packet = (PacketUpdatePlayerSprite) o;
			Main.currentMap.players.get(packet.id).spriteX = packet.spriteX;
			Main.currentMap.players.get(packet.id).spriteY = packet.spriteY;
			
		}else if(o instanceof PacketAddMob){
			PacketAddMob packet = (PacketAddMob) o;
			Chicken newMob = new Chicken();
			newMob.position.x = packet.position.x;
			newMob.position.y = packet.position.y;
			newMob.health = packet.health;
			newMob.maxHealth = packet.maxHealth;
			Main.currentMap.mobs.put(packet.id, newMob);
			
		}else if(o instanceof PacketUpdateMob){
			PacketUpdateMob packet = (PacketUpdateMob) o;

			if (Main.mapLoaded == true && Main.currentMap.mobs.containsKey(packet.id))
			{
				Main.currentMap.mobs.get(packet.id).position.x = packet.position.x;
				Main.currentMap.mobs.get(packet.id).position.y = packet.position.y;
				Main.currentMap.mobs.get(packet.id).spriteX = packet.spriteX;
				Main.currentMap.mobs.get(packet.id).spriteY = packet.spriteY;
			}
			
		}else if(o instanceof PacketAddProjectile){
			PacketAddProjectile packet = (PacketAddProjectile) o;
			Main.currentMap.projectiles.put(packet.projectile.id, packet.projectile);
			
		}else if(o instanceof PacketRemoveProjectile){
			PacketRemoveProjectile packet = (PacketRemoveProjectile) o;
			
			Main.currentMap.projectiles.remove(packet.id);
			
		}else if(o instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			Main.currentMap.players.remove(packet.id);
			
		}else if(o instanceof PacketUpdateMobHealth){
			PacketUpdateMobHealth packet = (PacketUpdateMobHealth) o;
			Main.currentMap.mobs.get(packet.id).health = packet.health;
			
		}else if(o instanceof PacketRemoveMob){
			PacketRemoveMob packet = (PacketRemoveMob) o;
			Main.currentMap.mobs.remove(packet.id);
			
		}else if(o instanceof PacketPlayerLeaveMap){
			PacketPlayerLeaveMap packet = (PacketPlayerLeaveMap) o;
			Main.currentMap.players.remove(packet.id);
			
		}else if(o instanceof PacketPlayerEnterMap){
			PacketPlayerEnterMap packet = (PacketPlayerEnterMap) o;
			Main.currentMap.players.put(packet.id, packet.newPlayer);
			
		}
		
	}
}
