package Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;

import Client.GameClient;
import Map.Map;
import Packet.*;

public class Player implements java.io.Serializable{
	
	public int mapID;
	
	public float x;
	public float y;
	
	public double addX;
	public double addY;
	
	public int spriteX = 0;
	public int spriteY = 0;
	
	public boolean isWalking;
	
	public int walkTimer;
	public int attackTimer;
	
	public float tileX;
	public float tileY;
	
	public double accelSpeed = 0.03;
	public double maxSpeed = 1;
	
	public int moveX;
	public int moveY;
	
	public int width = 28;
	public int height = 30;
	
	public int hitTimer = 0;
	public int hitCooldown;
	
	public float health = 100;
	public int maxHealth = 100;
	
	public int connectionID;
	
	List<Integer> collisionList = new ArrayList<>(Arrays.asList(16, 226));

	public boolean isColliding(Map currentMap)
	{
		for (int colX = (int) ((this.x/32) - 1); colX < (this.x/32) + 2; colX++)
		{
			for (int colY = (int) ((this.y/32) - 1); colY < (this.y/32) + 2; colY++)
			{
				if (colX >= 0 && colY >= 0)
				{

							if (currentMap.layers[currentMap.layerCount-1].data[colX][colY] > 0)
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
				
				GameClient.network.client.sendUDP(packet);
				
			}
			
			
			this.x += this.addX;
			if (this.isColliding(GameClient.currentMap))
			{
				this.x -= this.addX;
			}
			this.addX *= 0.8;
			
			this.y += this.addY;
			if (this.isColliding(GameClient.currentMap))
			{
				this.y -= this.addY;
			}
			this.addY *= 0.8;
			
			if (hitTimer > 0)
			{
				hitTimer += -1;
			}
			
			//decrease attack timer
			if (this.attackTimer > 0) {this.attackTimer -= 1;}
			
			//get nearest tile to player position
		    this.tileX = (( (this.x+16) / 32));
		    this.tileY = (( (this.y+16) / 32));
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
			
			GameClient.network.client.sendUDP(packet);

		}
		
		if (GameClient.mouseOne)
		{
			if (Math.abs(GameClient.aimX) > Math.abs(GameClient.aimY))
			{
				if (GameClient.aimX < 0) { GameClient.player.spriteY = 1; }
				else if (GameClient.aimX > 0) { GameClient.player.spriteY = 2; }
				
				PacketUpdatePlayerSprite packet = new PacketUpdatePlayerSprite();
				packet.spriteX = (byte) this.spriteX;
				packet.spriteY = (byte) this.spriteY;
				
				GameClient.network.client.sendUDP(packet);
			}
			else
			{
				if (GameClient.aimY < 0) { GameClient.player.spriteY = 3; }
				else if (GameClient.aimY > 0) { GameClient.player.spriteY = 0; }
				
				PacketUpdatePlayerSprite packet = new PacketUpdatePlayerSprite();
				packet.spriteX = (byte) this.spriteX;
				packet.spriteY = (byte) this.spriteY;
				
				GameClient.network.client.sendUDP(packet);
			}
		}
		
	}
	
	public void serverUpdate()
	{
		if (hitCooldown != 0)
		{
			hitCooldown += -1;
		}
	}
}
