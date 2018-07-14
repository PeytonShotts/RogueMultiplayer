package Client;

import java.util.Random;

import MapCode.Map;

public class Mob {
	
	public float x;
	public float y;
	
	public int width;
	public int height;
	
	protected float addX;
	protected float addY;
	
	int moveCooldown;
	
	boolean isHit;
	boolean isInRange;
	
	protected int spriteX;
	protected int spriteY;
	
	int timeAlive = 0;
	
	protected int health;
	protected int maxHealth;
	
	protected float speed;
	
	Random rand = new Random();
	
	public void update()
	{
		if (this.timeAlive < 50)
		{
			timeAlive++;
		}
		else
		{
			if (this.isInRange)
			{
				this.addX += (float) (-Util.getDirectionVector(Main.player, this).x*this.speed);
				this.addY += (float) (-Util.getDirectionVector(Main.player, this).y*this.speed);
			}
			else
			{
				moveCooldown -= 1;
				if (moveCooldown < 0)
				{
					randomMove();
				}
			}
			
			
			this.x += this.addX;
			if (this.isColliding(Main.currentMap) | (this.isCollidingWithPlayer(Main.player) && Main.player.hitTimer != 0))
			{
				this.x -= this.addX;
			}
			this.addX += this.addX*-0.1;
			
			this.y += this.addY;
			if (this.isColliding(Main.currentMap) | (this.isCollidingWithPlayer(Main.player) && Main.player.hitTimer != 0))
			{
				this.y -= this.addY;
			}
			this.addY += this.addY*-0.1;
			
			if (isCollidingWithPlayer(Main.player) && Main.player.hitTimer == 0)
			{
				knockBackPlayer();
				
			}
			
			if (Util.getDistance(Main.player, this) < 8*32)
			{
				this.isInRange = true;
			}
			else
			{
				this.isInRange = false;
			}
			
			
		}
		
		
		
	}
	
	public void randomMove()
	{
		int moveDirection = rand.nextInt(8);
		
		switch (moveDirection)
		{
			
			case 0:
				addX = -2;
				addY =  0;
				break;
			case 1:
				addX =  2;
				addY =  0;
				break;
			case 2:
				addY =  2;
				addX =  0;
				break;
			case 3:
				addY = -2;
				addX =  0;
				break;
		}
		
		if (moveDirection > 3)
		{
			addX = 0;
			addY = 0;
		}
		
		moveCooldown = 50;
	}
	
	public boolean isColliding(Map currentMap)
	{
		for (int colX = (int) ((this.x/32) - 1); colX < (this.x/32) + 2; colX++)
		{
			for (int colY = (int) ((this.y/32) - 1); colY < (this.y/32) + 2; colY++)
			{
				if (colX > 0 && colX < currentMap.width &&
						colY > 0 && colY < currentMap.height)
				{
					if ( currentMap.tileArray[colX][colY].isRoom == false)
					{
						if (this.x+5 < colX*32 + 32 &&
							this.x+5 + this.width > colX*32 &&
							this.y+5 < colY*32 + 32 &&
						    this.height + this.y > colY*32) 
					{
							return true;
					}
				}
				}
			}
		}
		
		return false;
	}
	
	public boolean isCollidingWithPlayer(Player player)
	{
		if (this.x < player.x + player.width &&
				this.x + this.width > player.x &&
				this.y < player.y + player.height &&
				this.height + this.y > player.y) {
			
				    return true;
				}
		return false;
	}
	
	public void knockBackPlayer()
	{
		Vector collisionVector = new Vector();
		float xDist = (int) (Main.player.x - this.x); float yDist = (int) (Main.player.y - this.y);
		float magnitude = (int) Math.sqrt( (xDist*xDist) + (yDist*yDist) );
		collisionVector.x = (xDist / magnitude);
		collisionVector.y = (yDist / magnitude);
		
		Main.player.addX += collisionVector.x * 3;
		Main.player.addY += collisionVector.y * 3;
		
		System.out.println(collisionVector.x);
		System.out.println(collisionVector.y);
		
		Main.player.hitTimer = 50;
		
		Main.player.health += -10;
	}
	
	
}
