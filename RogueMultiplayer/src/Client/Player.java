package Client;

import MapCode.Map;

public class Player {
	
	float x;
	float y;
	
	double addX;
	double addY;
	
	String walkDirection = "down";
	int spriteX = 0;
	int spriteY = 0;
	
	boolean isWalking;
	
	int walkTimer;
	int attackTimer;
	
	int tileX;
	int tileY;
	
	double accelSpeed = 0.03;
	double maxSpeed = 1;
	
	int moveX;
	int moveY;
	
	int width = 28;
	int height = 30;
	
	int hitTimer = 0;
	
	int health = 100;
	int maxHealth = 100;
	
	public boolean isColliding(Map currentMap)
	{
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
			this.addX += this.addX*-0.1;
			
			this.y += this.addY;
			if (this.isColliding(Main.currentMap))
			{
				this.y -= this.addY;
			}
			this.addY += this.addY*-0.1;
			
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
			packet.spriteX = this.spriteX;
			packet.spriteY = this.spriteY;
			
			Main.network.client.sendUDP(packet);

		}
		
		if (Main.leftClick)
		{
			if (Math.abs(Main.aimX) > Math.abs(Main.aimY))
			{
				if (Main.aimX < 0) { Main.player.spriteY = 1; }
				else if (Main.aimX > 0) { Main.player.spriteY = 2; }
			}
			else
			{
				if (Main.aimY < 0) { Main.player.spriteY = 3; }
				else if (Main.aimY > 0) { Main.player.spriteY = 0; }
			}
		}
		
	}
}
