package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Mob.*;
import MapCode.*;
import Projectile.*;
import Packet.*;

public class Main extends BasicGame
{
	static Map currentMap;
	
	static byte[] mapBytes = new byte[1000000];
	
	static int offsetX = 0;
	static int offsetY = 0;

	int mouseX;
	int mouseY;
	
	float relativeMouseX;
	float relativeMouseY;
	
	static double aimX;
	static double aimY;
	
	int mouseBlockX;
	int mouseBlockY;
	
	float xDist;
	float yDist;
	
	double lightX;
	double lightY;
	double lightDirectionX;
	double lightDirectionY;
	
	boolean hasInit = false;
	
	String moveDirection;
	
	Image tileset;
	Image spriteset;
	
	int spawnX;
	int spawnY;
	
	public static int projectileCount = 0;
	
	static Player player = new Player();
	
	static java.util.Map<Integer,Player> players = new HashMap<Integer,Player>(); 
	static java.util.Map<Integer,Mob> mobs = new HashMap<Integer,Mob>(); 
	static java.util.Map<Integer,Projectile> projectiles = new HashMap<Integer,Projectile>(); 
	
	static boolean mouseClick;
	static boolean mouseOne;
	
	static Network network = new Network();
	static Gui.Gui gui = new Gui.Gui();

	public static boolean mapLoaded = false;
	
	
	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException
	{
		gui.init();
		
		tileset = new Image("res/owlishmedia_pixel_tiles.png"); 
		spriteset = new Image("res/spriteset.png");
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException
	{
		
		if (mapLoaded == true)
		{
			currentMap = SerializationUtils.deserialize(mapBytes);
		}
		
		if (mapLoaded == true)
		{
			//spawn player
			if (player.x == 0 && player.y == 0) {player.x = currentMap.spawnPoint.x; player.y = currentMap.spawnPoint.y;}
	
			//get keyboard and mouse input
			getInput(gc, i);
			
			//update player position
			player.update();
			
			//update player sprite
			player.updateSprite();
			
			//update view offset
			updateViewOffset();
			
			//update gui
			gui.update(mouseX, mouseY, mouseClick, mouseOne);
			
			//add new projectile when mouse is pressed
			if(mouseOne == true && player.attackTimer == 0 && gui.mouseFocus == false)
			{
				Projectile newProjectile = new Projectile();
				
				newProjectile.position.x = (float) (player.x + 16 + (aimX*12) );
				newProjectile.position.y = (float) (player.y + 16 + (aimY*12) );
				
				newProjectile.direction.x = (float) aimX;
				newProjectile.direction.y = (float) aimY;
				newProjectile.time = 20;
				
				newProjectile.speed = 2;
				
				newProjectile.id = projectileCount;
				projectiles.put(projectileCount, newProjectile);
				
				PacketAddProjectile packet = new PacketAddProjectile();
				packet.projectile = newProjectile;
				network.client.sendTCP(packet);
				
				projectileCount++;
				player.attackTimer = 50;
				
				
			}
			
			//update projectiles
			updateProjectiles();
			for (Mob mob : mobs.values())
			{
					if (mob.isCollidingWithProjectile(projectiles) != null)
					{
						
					}
			}
			
			//calculate visible blocks
			//calculateVisibleBlocks();
		}
	    
	}

	private void updateProjectiles() {
		for (Projectile projectile : projectiles.values())
		{
			if (projectile.time == 0)
			{
				projectiles.remove(projectile.id);
			}
			projectile.update();
		}
		
	}


	/*
	private void calculateVisibleBlocks() {
		
		for (double lightI = 0; lightI < Math.PI*2; lightI += Math.PI/100)
		{
			lightX = (float) Math.cos(lightI);
			lightY = (float) Math.sin(lightI);
			lightDirectionX = (float) (lightX / Math.sqrt((lightX*lightX) + (lightY*lightY)));
			lightDirectionY = (float) (lightY / Math.sqrt((lightX*lightX) + (lightY*lightY)));
					
			for (int lightR = 0; lightR < 8; lightR++)
			{
				int lightBlockX =  player.tileX + Util.closestInteger((int) ((lightDirectionX*lightR*32)), 32);
				int lightBlockY =  player.tileY + Util.closestInteger((int) ((lightDirectionY*lightR*32)), 32);
				
				if ( (lightBlockX-offsetX)/32 > 0 && (lightBlockX-offsetX)/32 < currentMap.width-1 &&
					 (lightBlockY-offsetY)/32 > 0 && (lightBlockY-offsetY)/32 < currentMap.height-1)
				{
					if (currentMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isRoom == true)
					{
						currentMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isVisible = true;
					}
					else
					{
						currentMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isVisible = true;
						break;
					}
				}
						
			}
		}
		
	}
	*/

	private void updateViewOffset() {
		
		offsetX = (int) ((spawnX)*-1 - (int)player.x - player.addX + (640));
		offsetY = (int) ((spawnY)*-1 - (int)player.y - player.addY + (360));
		
		if (offsetX > 0) {offsetX = 0;}
		if (offsetY > 0) {offsetY = 0;}
	}

	private void getInput(GameContainer gc, int i) {
		if (gc.getInput().isKeyDown(Input.KEY_W))
		{
			player.addY = -1;
			offsetY += 0.5;
			
			player.spriteY = 3;
			player.isWalking = true;	
		}
		else if (gc.getInput().isKeyDown(Input.KEY_S))
		{
			player.addY = 1;
			offsetY += -0.5;
			
			player.spriteY = 0;
			player.isWalking = true;
		}
		
		if (gc.getInput().isKeyDown(Input.KEY_A))
		{
			player.addX = -1;
			
			player.spriteY = 1;
			player.isWalking = true;
		}
		else if (gc.getInput().isKeyDown(Input.KEY_D))
		{
			player.addX = 1;
			
			player.spriteY = 2;
			player.isWalking = true;
		}
		
		if (gc.getInput().isKeyDown(Input.KEY_W) == false && gc.getInput().isKeyDown(Input.KEY_A) == false &&
				gc.getInput().isKeyDown(Input.KEY_S) == false && gc.getInput().isKeyDown(Input.KEY_D) == false)
			{
				player.isWalking = false;
			}
			
		if (player.isWalking == true) {player.walkTimer++;}
		
		
		mouseClick = gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
		mouseOne = gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		
		//get mouse X and Y
		mouseX = Mouse.getX();
	    mouseY = 720 - Mouse.getY();
		
		//get mouse X and Y relative to center of player
		relativeMouseX = (mouseX - offsetX) - (player.x+16);
		relativeMouseY = (mouseY - offsetY) - (player.y+16);
				
		//get unit vector of player->mouse position
		aimX = (relativeMouseX / Math.sqrt( (relativeMouseX*relativeMouseX) + (relativeMouseY*relativeMouseY) ) );
		aimY = (relativeMouseY / Math.sqrt( (relativeMouseX*relativeMouseX) + (relativeMouseY*relativeMouseY) ) );
		
	    //get nearest block to mouse position
	    mouseBlockX = (int) Math.floor( (mouseX - offsetX) / 32);
	    mouseBlockY = (int) Math.floor( (mouseY - offsetY) / 32);
			
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		
		if (mapLoaded == true)
		{
			
		//load images when client first opens
		//if (hasInit == false) {tileset = new Image("res/owlishmedia_pixel_tiles.png"); spriteset = new Image("res/spriteset.png"); hasInit = true;}
		
		//draw map around player
		if (player.x > 0) {
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), currentMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), currentMap.width - 1); drawX++)
			{
				
				for (int layer=0; layer<1; layer++)
				{
					int tile = (currentMap.layers[layer].data[drawX][drawY]);
					int tileY = (int) (Math.floor(tile / 14));
					int tileX = (int) (tile - (tileY*14));
					
					
					g.drawImage(tileset, drawX*32 + offsetX, drawY*32 + offsetY, (drawX*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
						
				}
			}
		}
		
		
		g.drawImage(spriteset, (int)player.x + offsetX, (int)player.y + offsetY, (int)player.x+32 + offsetX, (int)player.y+32 + offsetY, (int)player.spriteX*32, (int)player.spriteY*32, ((int)player.spriteX*32) + 32, ((int)player.spriteY*32) + 32, new Color(255,255,255));
		
		//draw map around player
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), currentMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), currentMap.width - 1); drawX++)
			{
				
				for (int layer=1; layer<currentMap.layers.length; layer++)
				{
					int tile = (currentMap.layers[layer].data[drawX][drawY]);
					int tileY = (int) (Math.floor(tile / 14));
					int tileX = (int) (tile - (tileY*14));
					
					
					g.drawImage(tileset, drawX*32 + offsetX, drawY*32 + offsetY, (drawX*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
						
				}
			}
		}
		
		
		//draw other players
		for(Player mpPlayer : players.values())
		{
			g.drawImage(spriteset, (int)mpPlayer.x + offsetX, (int)mpPlayer.y + offsetY, (int)mpPlayer.x+32 + offsetX, (int)mpPlayer.y+32 + offsetY, (int)mpPlayer.spriteX*32, (int)mpPlayer.spriteY*32, ((int)mpPlayer.spriteX*32) + 32, ((int)mpPlayer.spriteY*32) + 32, new Color(255,255,255));
		}
		
		//draw mobs (new)
		for(Mob mob : mobs.values())
		{
			g.setColor(new Color(255, 255, 255));
			g.drawRect(mob.position.x + offsetX, mob.position.y + offsetY, 32, 32);
		}
		
		//draw mobs
		for(int mobI=0; mobI<currentMap.mobList.size(); mobI++)
		{
		
			if (currentMap.mobList.get(mobI).isHit)
			{
				g.drawImage(tileset, 
						currentMap.mobList.get(mobI).position.x + offsetX, currentMap.mobList.get(mobI).position.y + offsetY, currentMap.mobList.get(mobI).position.x + offsetX + 32, currentMap.mobList.get(mobI).position.y + offsetY + 32,
						(currentMap.mobList.get(mobI).spriteX)*32, (currentMap.mobList.get(mobI).spriteY)*32, ((currentMap.mobList.get(mobI).spriteX)*32) + 32, ((currentMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,0,0));
			}
			else
			{
				g.drawImage(tileset, 
						(int) currentMap.mobList.get(mobI).position.x + offsetX, (int) currentMap.mobList.get(mobI).position.y + offsetY, (int) currentMap.mobList.get(mobI).position.x + offsetX + 32, (int) currentMap.mobList.get(mobI).position.y + offsetY + 32,
						(currentMap.mobList.get(mobI).spriteX)*32, (currentMap.mobList.get(mobI).spriteY)*32, ((currentMap.mobList.get(mobI).spriteX)*32) + 32, ((currentMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,255,255));
			}
					
			//draw health bar if mob health is under maximum
			if (currentMap.mobList.get(mobI).health < currentMap.mobList.get(mobI).maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(currentMap.mobList.get(mobI).position.x + offsetX, currentMap.mobList.get(mobI).position.y + offsetY - 5, 32* ((float)currentMap.mobList.get(mobI).health / currentMap.mobList.get(mobI).maxHealth), 5);
			}
			
			//draw health bar if player health is under maximum
			if (player.health < player.maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(player.x + offsetX, player.y + offsetY - 5, 32 * ((float)player.health / (float)player.maxHealth), 5);
			}
		}
				
		
		//draw lighting
		/*
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY<(player.y/32) + 20; drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX<(player.x/32) + 20; drawX++)
			{
				xDist = Math.abs((float)drawX - (float)player.x/32); 
				yDist = Math.abs((float)drawY - (float)player.y/32);
				double blockDist = Math.sqrt((xDist*xDist) + (yDist*yDist));
				if (drawX >= 0 && drawX < currentMap.width-1 &&
					drawY >= 0 && drawY < currentMap.height-1)
				{
					if (currentMap.tileArray[drawX][drawY].isVisible)
					{
						int tileDarkness;
						if (blockDist < 4)
						{
							tileDarkness = (int) (blockDist * 25);
						}
						else
						{
							tileDarkness = (int) (blockDist * 30);
						}
						
						
						g.setColor(new Color(0,0,0, tileDarkness));
						g.fillRect(drawX*32 + offsetX, drawY*32 + offsetY, 32, 32);
					}
					else
					{
						g.setColor(new Color(0,0,0, 255));
						g.fillRect(drawX*32 + offsetX, drawY*32 + offsetY, 32, 32);
					}
				}
				
			}
		}
		*/
		
		gui.draw(gc, g);
		
		}
	
		for(Projectile projectile : projectiles.values())
		{
			g.setColor(new Color(180, 170, 180, projectile.time*20 ));
			g.fillRect(projectile.position.x - 2 + offsetX, projectile.position.y - 2 + offsetY, projectile.size, projectile.size);
		}
		
		}
		
	}
	
	public static void main(String[] args)
	{
		
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("Slick2d Window"));
			appgc.setDisplayMode(1280, 720, false);
			appgc.setTargetFrameRate(60);
			appgc.setShowFPS(false);
			appgc.setUpdateOnlyWhenVisible(false);
			appgc.setAlwaysRender(true);


			
			network.connect();
			
			
			
			appgc.start();

		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}