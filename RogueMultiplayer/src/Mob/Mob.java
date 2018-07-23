package Mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import Client.Main;
import Client.Util;
import Map.Map;
import Packet.PacketUpdateMobHealth;
import Player.Player;
import Projectile.Projectile;
import Vector.Vector;

public class Mob implements java.io.Serializable{
	
	public Vector position = new Vector();
	public Vector networkPosition = new Vector();
	
	public int width = 32;
	public int height = 32;
	
	public float addX;
	public float addY;
	public float targetX;
	public float targetY;
	
	Vector momentum = new Vector();
	
	int moveCooldown;
	
	boolean isMoving;
	boolean isHit;
	
	boolean isInRange;
	Player playerInRange;
	
	public static Image spriteSheet;
	public int spriteCount;
	public int spriteX;
	public int spriteY;
	public int walkTimer;
	
	public int health;
	public int networkHealth;
	public int maxHealth;
	
	public float speed = (float) 0.9;
	
	List<Integer> collisionList = new ArrayList<>(Arrays.asList(16, 226));
	
	Random rand = new Random();
	
	public Mob()
	{
		
	}
	
	public void draw(GameContainer gc, Graphics g, float offsetX, float offsetY)
	{
		float drawX = position.x + offsetX;
		float drawY = position.y + offsetY;
		
		spriteSheet.draw(drawX, drawY, drawX+32, drawY+32, spriteX*32, spriteY*32, (spriteX*32)+32, (spriteY*32)+32);
	}
	
	public void update(Map map, java.util.Map<Integer, Projectile> projectiles)
	{
			
		
			if (Math.abs(momentum.x+momentum.y) < 0.01)
			{
				if (isHit == true)
				{
					isHit = false;
				}
				
				moveCooldown -= 1;
				if (moveCooldown < 0)
				{
					randomMove();
				}
			}
			else if (Math.abs(momentum.x + momentum.y) > 0.1 && isHit == false)
			{	//up right down left
				walkTimer++;
				if (walkTimer == 10)
				{
					walkTimer = 0;
					spriteX++;
					
					if (spriteX == spriteCount)
					{
						spriteX = 0;
					}
				}
				
				if (Math.abs(momentum.x) > Math.abs(momentum.y))
				{
					if (momentum.x > 0)
					{
						spriteY = 1;
					}
					else
					{
						spriteY = 3;
					}
				}
				else
				{
					if (momentum.y > 0)
					{
						spriteY = 2;
					}
					else
					{
						spriteY = 0;
					}
				}
			}
			else
			{
				spriteX = 1;
			}
			
			this.position.x += this.addX + this.targetX;
			if (this.isCollidingWithMap(map))
			{
				this.position.x -= this.addX + this.targetX;
			}
			for (Mob mob : map.mobs.values())
			{
				if (mob != this && this.isCollidingWithMob(mob))
				{
					this.position.x -= this.addX + this.targetX;
				}
			}
			for (Player player : map.players.values())
			{
				if (this.isCollidingWithPlayer(player))
				{
					this.position.x -= this.addX + this.targetX;
				}
			}
			
			this.addX += this.addX*-0.1;
			
			this.position.y += this.addY + this.targetY;
			if (this.isCollidingWithMap(map))
			{
				this.position.y -= this.addY + this.targetY;
			}
			for (Mob mob : map.mobs.values())
			{
				if (mob != this && this.isCollidingWithMob(mob))
				{
					this.position.y -= this.addY + this.targetY;
				}
			}
			for (Player player : map.players.values())
			{
				if (this.isCollidingWithPlayer(player))
				{
					this.position.y -= this.addY + this.targetY;
				}
			}
			
			this.addY += this.addY*-0.1;
			
			
			
			for (Player player : map.players.values())
			{
				playerInRange = checkPlayerDistance(player);
			}
			
			if (playerInRange != null && this.isInRange)
			{
				this.targetX = (float) (-Util.getDirectionVector(playerInRange, this).x*this.speed);
				this.targetY = (float) (-Util.getDirectionVector(playerInRange, this).y*this.speed);
			}
			
			
			Vector hitVector = this.isCollidingWithProjectile(projectiles);
			if (hitVector != null)
			{
				this.addX += hitVector.x;
				this.addY += hitVector.y;
				this.isHit = true;
			}
			
			momentum.x = addX+targetX;
			momentum.y = addY+targetY;
		
	}
	
	public void randomMove()
	{
		int moveDirection = rand.nextInt(8);
		
		switch (moveDirection)
		{
			
			case 0:
				addX = -2;
				addY =  0;
				spriteY = 3;
				break;
			case 1:
				addX =  2;
				addY =  0;
				spriteY = 1;
				break;
			case 2:
				addY =  2;
				addX =  0;
				spriteY = 2;
				break;
			case 3:
				addY = -2;
				addX =  0;
				spriteY = 0;
				break;
		}
		
		if (moveDirection > 3)
		{
			addX = 0;
			addY = 0;
		}
		
		moveCooldown = 50;
	}


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
		for (int colX = (int) ((this.position.x/32) - 1); colX < (this.position.x/32) + 2; colX++)
		{
			for (int colY = (int) ((this.position.y/32) - 1); colY < (this.position.y/32) + 2; colY++)
			{
				if (colX > 0 && colX < map.width &&
						colY > 0 && colY < map.height)
				{
					for (int type : collisionList)
					{
						if ( map.layers[0].data[colX][colY] == type)
						{
							if (this.position.x < colX*32 + 32 &&
								this.position.x + this.width > colX*32 &&
								this.position.y < colY*32 + 32 &&
							    this.height + this.position.y > colY*32) 
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
				if (projectile.position.x < this.position.x + this.width &&
					projectile.position.x + projectile.size > this.position.x &&
					projectile.position.y < this.position.y + this.height &&
					projectile.size + projectile.position.y > this.position.y) 
						{
							projectile.time = 0;
							health += -projectile.damage;
							
							
							Vector hitVector = new Vector(projectile.direction.x*projectile.knockback, 
															projectile.direction.y*projectile.knockback);
							return hitVector;
						}
			}
				
		return null;
	}

	public boolean isCollidingWithPlayer(Player player)
	{
		if (this.position.x < player.x + player.width &&
				this.position.x + this.width > player.x &&
				this.position.y < player.y + player.height &&
				this.height + this.position.y > player.y) {
			
				    return true;
				}
		return false;
	}
	
	public boolean isCollidingWithMob(Mob mob)
	{
		if (this.position.x < mob.position.x + mob.width &&
				this.position.x + this.width > mob.position.x &&
				this.position.y < mob.position.y + mob.height &&
				this.height + this.position.y > mob.position.y) {
			
				    return true;
				}
		return false;
	}
	
	public void knockBackPlayer(Player player)
	{
		Vector collisionVector = new Vector();
		float xDist = (int) (player.x - this.position.x); float yDist = (int) (player.y - this.position.y);
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
