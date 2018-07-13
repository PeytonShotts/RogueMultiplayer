package Client;
import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Server.Tile;



public class Network extends Listener {

	Client client;
	String ip = "localhost";
	int port = 7777;
	
	public void connect(){
		client = new Client(16384, 16384);
		client.getKryo().register(PacketMapData.class);
		client.getKryo().register(PacketAddPlayer.class);
		client.getKryo().register(PacketUpdatePlayerPosition.class);
		client.getKryo().register(PacketUpdatePlayerSprite.class);
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
			
			Main.currentMap = new Map(packet.width, packet.height);
			//convert int array to map
			for (int mapY=0; mapY<packet.height; mapY++)
			{
				for (int mapX=0; mapX<packet.width; mapX++)
				{
					if (packet.tileArray[mapX][mapY] == 1)
					{
						Main.currentMap.tileArray[mapX][mapY].isRoom = true;
					}
					else
					{
						Main.currentMap.tileArray[mapX][mapY].isRoom = false;
					}
				}
			}
			
			System.out.println("map loaded");
			Main.mapLoaded = true;
			
			Main.currentMap.spawnPoint.x = packet.spawnX;
			Main.currentMap.spawnPoint.y = packet.spawnY;
			
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
			
		}
		
	}
}