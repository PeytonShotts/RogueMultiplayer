package Client;
import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Mob.Mob;
import Projectile.Projectile;
import Vector.Vector;
import Packet.*;



public class Network extends Listener {

	Client client;
	String ip = "localhost";
	int port = 7777;
	
	public void connect(){
		client = new Client(25000, 25000);
		client.getKryo().register(PacketMapData.class);
		client.getKryo().register(PacketAddPlayer.class);
		client.getKryo().register(PacketRemovePlayer.class);
		client.getKryo().register(PacketUpdatePlayerPosition.class);
		client.getKryo().register(PacketUpdatePlayerSprite.class);
		client.getKryo().register(Mob.class);
		client.getKryo().register(PacketAddMob.class);
		client.getKryo().register(PacketUpdateMob.class);
		client.getKryo().register(PacketAddProjectile.class);
		client.getKryo().register(PacketRemoveProjectile.class);
		client.getKryo().register(Vector.class);
		client.getKryo().register(Projectile.class);
		client.getKryo().register(byte[].class);
		client.getKryo().register(int[].class);
		client.getKryo().register(int[][].class);
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
				Main.mapLoaded = true;
				System.out.println("map loaded");
			}
			
			
		}
		else if(o instanceof PacketAddPlayer){
			PacketAddPlayer packet = (PacketAddPlayer) o;
			Player newPlayer = new Player();
			newPlayer.x = packet.x;
			newPlayer.y = packet.y;
			Main.players.put(packet.id, newPlayer);
			
			System.out.println("new player joined");
		}else if(o instanceof PacketUpdatePlayerPosition){
			PacketUpdatePlayerPosition packet = (PacketUpdatePlayerPosition) o;
			Main.players.get(packet.id).x = packet.x;
			Main.players.get(packet.id).y = packet.y;
			
		}else if(o instanceof PacketUpdatePlayerSprite){
			PacketUpdatePlayerSprite packet = (PacketUpdatePlayerSprite) o;
			Main.players.get(packet.id).spriteX = packet.spriteX;
			Main.players.get(packet.id).spriteY = packet.spriteY;
			
		}else if(o instanceof PacketAddMob){
			PacketAddMob packet = (PacketAddMob) o;
			Mob newMob = new Mob();
			newMob.position.x = packet.position.x;
			newMob.position.y = packet.position.y;
			Main.mobs.put(packet.id, newMob);
			
		}else if(o instanceof PacketUpdateMob){
			PacketUpdateMob packet = (PacketUpdateMob) o;
			Main.mobs.get(packet.id).position.x = packet.position.x;
			Main.mobs.get(packet.id).position.y = packet.position.y;
			
		}else if(o instanceof PacketAddProjectile){
			PacketAddProjectile packet = (PacketAddProjectile) o;
			Main.projectiles.put(Main.projectileCount, packet.projectile);
			Main.projectileCount++;
		}else if(o instanceof PacketRemoveProjectile){
			PacketRemoveProjectile packet = (PacketRemoveProjectile) o;
			Main.projectiles.remove(packet.id);
			
		}else if(o instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			Main.players.remove(packet.id);
			
		}
		
	}
}
