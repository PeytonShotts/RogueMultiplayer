package Server;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class Main extends Listener {

	static Server server;
	static final int port = 7777;
	static java.util.Map<Integer, Player> players = new HashMap<Integer, Player>();
	
	static Vector2f ballPosition = new Vector2f();
	static Vector2f ballDirection = new Vector2f();
	
	Map newMap = MapGen.create(50, 50);
	
	public static int tick = 0;
	
	public static void main(String[] args) throws IOException{
		
		server = new Server();
		server.getKryo().register(PacketMapData.class);
		server.getKryo().register(PacketAddPlayer.class);
		server.getKryo().register(PacketUpdatePlayerPosition.class);
		server.getKryo().register(PacketUpdatePlayerSprite.class);
		server.getKryo().register(int[].class);
		server.getKryo().register(int[][].class);
		server.bind(port, port);
		server.start();
		server.addListener(new Main());
		System.out.println("The server is ready");
		
		ballPosition.x = 400 - 15; ballPosition.y = 250 - 15;
		ballDirection.x = 1; ballDirection.y = 1;
		
		
		while (true)
		{
			//server loop
		}
	}
	
	public void connected(Connection c){
		
		System.out.println("Connection received. Sending Map...");
		
		PacketMapData packet = new PacketMapData();
		
		//convert map to int array
		packet.width = newMap.width;
		packet.height = newMap.height;

		packet.tileArray = new int[packet.width][packet.height];
		
		for (int mapY=0; mapY<newMap.height; mapY++)
		{
			for (int mapX=0; mapX<newMap.width; mapX++)
			{
				if (newMap.tileArray[mapX][mapY].isRoom == true)
				{
					packet.tileArray[mapX][mapY] = 1;
				}
				else
				{
					packet.tileArray[mapX][mapY] = 0;
				}
			}
		}
		
		packet.spawnX = (int) newMap.spawnPoint.x;
		packet.spawnY = (int) newMap.spawnPoint.y;
		
		//send map data to client
		c.sendTCP(packet);
		
		
		Player player = new Player();
		player.x = newMap.spawnPoint.x;
		player.y = newMap.spawnPoint.y;
		player.c = c;
		
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
		
		players.put(c.getID(), player);
		System.out.println("Connection received.");
		
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
		
	}
	
	public void disconnected(Connection c){

	}
}
