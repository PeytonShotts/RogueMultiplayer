package Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;

import Client.Main;
import MapCode.Map;
import Packet.*;

public class Player {
	
	public static int mapID;
	
	public float x;
	public float y;
	
	public double addX;
	public double addY;
	
	public int spriteX = 0;
	public int spriteY = 0;
	
	public boolean isWalking;
	
	public int walkTimer;
	public int attackTimer;
	
	public int tileX;
	public int tileY;
	
	public double accelSpeed = 0.03;
	public double maxSpeed = 1;
	
	public int moveX;
	public int moveY;
	
	public int width = 28;
	public int height = 30;
	
	public int hitTimer = 0;
	
	public int health = 100;
	public int maxHealth = 100;
	
	List<Integer> collisionList = new ArrayList<>(Arrays.asList(16, 226));
	public Connection c;

	

	
	public boolean isColliding(Map currentMap)
	{
		for (int colX = (int) ((this.x/32) - 1); colX < (this.x/32) + 2; colX++)
		{
			for (int colY = (int) ((this.y/32) - 1); colY < (this.y/32) + 2; colY++)
			{
				if (colX >= 0 && colY >= 0)
				{
					for (int type : collisionList)
					{
						for (int layer=0; layer<Main.currentMap.layers.length; layer++)
						{
							if (type == currentMap.layers[layer].data[colX][colY])
							{
								if (this.x < colX*32 + width &&
										this.x + width > colX*32 &&
										this.y < colY*32 + height &&
										height + this.y > colY*32) 
										{
											return true;
										}
							}
						}

					}
				
				}
			}
		}
		
		return false;
	}
	
	public void update()
	{
			double m = (int) Math.sqrt((addX*addX) + (addY*addY));
			if (m == 0) {m = 0.000001;}
			
			if (Math.abs(addX) > 0.001 | Math.abs(addY) > 0.001)
			{
				PacketUpdatePlayerPosition packet = new PacketUpdatePlayerPosition();
				packet.x = (int) this.x;
				packet.y = (int) this.y;
				
				Main.network.client.sendUDP(packet);
				
			}
			
			
			this.x += this.addX;
			if (this.isColliding(Main.currentMap))
			{
				this.x -= this.addX;
			}
			this.addX *= 0.0;
			
			this.y += this.addY;
			if (this.isColliding(Main.currentMap))
			{
				this.y -= this.addY;
			}
			this.addY *= 0.0;
			
			if (hitTimer > 0)
			{
				hitTimer += -1;
			}
			
			//decrease attack timer
			if (this.attackTimer > 0) {this.attackTimer -= 1;}
			
			//get nearest tile to player position
		    this.tileX = (int) (( (this.x+16) / 32)*32 + Main.offsetX);
		    this.tileY = (int) (( (this.y+16) / 32)*32 + Main.offsetY);
	}
	
	public void updateSprite()
	{
		//change sprite & update walkTimer if player is walking
		if (this.walkTimer > 10)
		{
			if (this.spriteX < 2)
			{
				this.spriteX++;
				this.walkTimer = 0;
			}
			else
			{
				this.spriteX = 0;
				this.walkTimer = 0;
			}
			
			PacketUpdatePlayerSprite packet = new PacketUpdatePlayerSprite();
			packet.spriteX = (byte) this.spriteX;
			packet.spriteY = (byte) this.spriteY;
			
			Main.network.client.sendUDP(packet);

		}
		
		if (Main.mouseOne)
		{
			if (Math.abs(Main.aimX) > Math.abs(Main.aimY))
			{
				if (Main.aimX < 0) { Main.player.spriteY = 1; }
				else if (Main.aimX > 0) { Main.player.spriteY = 2; }
				
				PacketUpdatePlayerSprite packet = new PacketUpdatePlayerSprite();
				packet.spriteX = (byte) this.spriteX;
				packet.spriteY = (byte) this.spriteY;
				
				Main.network.client.sendUDP(packet);
			}
			else
			{
				if (Main.aimY < 0) { Main.player.spriteY = 3; }
				else if (Main.aimY > 0) { Main.player.spriteY = 0; }
				
				PacketUpdatePlayerSprite packet = new PacketUpdatePlayerSprite();
				packet.spriteX = (byte) this.spriteX;
				packet.spriteY = (byte) this.spriteY;
				
				Main.network.client.sendUDP(packet);
			}
		}
		
	}
}
