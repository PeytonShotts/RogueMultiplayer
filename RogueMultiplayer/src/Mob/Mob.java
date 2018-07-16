package Mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Client.Main;
import Client.Player;
import Client.Util;
import MapCode.Map;
import Projectile.Projectile;
import Vector.Vector;

public class Mob {
	
	public float x;
	public float y;
	
	public int width = 32;
	public int height = 32;
	
	public float addX;
	public float addY;
	
	int moveCooldown;
	
	public boolean isHit;
	
	boolean isInRange;
	Player playerInRange;
	
	public int spriteX;
	public int spriteY;
	
	int timeAlive = 0;
	
	public int health;
	public int maxHealth;
	
	protected float speed;
	
	List<Integer> collisionList = new ArrayList<>(Arrays.asList(16, 226));
	
	Random rand = new Random();
	
	public void update(Map map, java.util.Map<Integer, Projectile> projectiles)
	{
			if (this.isInRange)
			{
				this.addX += (float) (-Util.getDirectionVector(playerInRange, this).x*this.speed);
				this.addY += (float) (-Util.getDirectionVector(playerInRange, this).y*this.speed);
			}
			else if (Math.abs(this.addX+this.addY) < 0.001)
			{
				moveCooldown -= 1;
				if (moveCooldown < 0)
				{
					randomMove();
				}
			}
			
			this.x += this.addX;
			if (this.isCollidingWithMap(map))
			{
				this.x -= this.addX;
			}
			this.addX += this.addX*-0.1;
			
			this.y += this.addY;
			if (this.isCollidingWithMap(map))
			{
				this.y -= this.addY;
			}
			this.addY += this.addY*-0.1;
			
			Vector hitVector = this.isCollidingWithProjectile(projectiles);
			if (hitVector != null)
			{
				this.addX = hitVector.x*3;
				this.addY = hitVector.y*3;
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

	//return closest player if close enough
	public Player checkPlayerDistance(Player player)
	{
		if (Util.getDistance(player, this) < 8*32)
		{
			this.isInRange = true;
			return player;
		}
		else
		{
			this.isInRange = false;
			return null;
		}
	}

	public boolean isCollidingWithMap(Map map)
	{
		for (int colX = (int) ((this.x/32) - 1); colX < (this.x/32) + 2; colX++)
		{
			for (int colY = (int) ((this.y/32) - 1); colY < (this.y/32) + 2; colY++)
			{
				if (colX > 0 && colX < map.width &&
						colY > 0 && colY < map.height)
				{
					for (int type : collisionList)
					{
						if ( map.layers[0].data[colX][colY] == type)
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
		}
		
		return false;
	}
	
	public Vector isCollidingWithProjectile(java.util.Map<Integer, Projectile> projectiles)
	{
		for (Projectile projectile : projectiles.values())
			{
				if (projectile.position.x < this.x + this.width &&
					projectile.position.x + projectile.size > this.x &&
					projectile.position.y < this.y + this.height &&
					projectile.size + projectile.position.y > this.y) 
						{
							projectile.time = 0;
							return projectile.direction;
						}
			}
				
		return null;
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
	
	public void knockBackPlayer(Player player)
	{
		Vector collisionVector = new Vector();
		float xDist = (int) (player.x - this.x); float yDist = (int) (player.y - this.y);
		float magnitude = (int) Math.sqrt( (xDist*xDist) + (yDist*yDist) );
		collisionVector.x = (xDist / magnitude);
		collisionVector.y = (yDist / magnitude);
		
		player.addX += collisionVector.x * 3;
		player.addY += collisionVector.y * 3;
		
		System.out.println(collisionVector.x);
		System.out.println(collisionVector.y);
		
		player.hitTimer = 50;
		
		player.health += -10;
	}
	
	
}
