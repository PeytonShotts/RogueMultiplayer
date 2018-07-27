package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
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

import Item.*;
import Map.*;
import Mob.*;
import Projectile.*;
import Vector.Vector;
import Packet.*;
import ParticleCode.CircleExplosion;
import ParticleCode.Particle;
import Player.*;

public class GameClient extends BasicGame
{
	public static Map currentMap;
	public static Image currentTileset;
	
	static byte[] mapBytes = new byte[10000000];
	static int[][] visibleTiles = new int[1000][1000];
	
	public static float offsetX = 0;
	public static float offsetY = 0;

	int mouseX;
	int mouseY;
	
	float relativeMouseX;
	float relativeMouseY;
	
	public static double aimX;
	public static double aimY;
	
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
	
	static Image tileset_castle;
	static Image tileset_owlish;
	
	
	Image spriteset;
	
	int spawnX;
	int spawnY;
	
	public static int projectileCount = 0;
	
	public static Player player = new Player();
	
	static boolean mouseClick;
	public static boolean mouseOne;
	
	public static Network network = new Network();
	static Gui.Gui gui = new Gui.Gui();

	public static boolean mapLoaded = false;

	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	double loadAnim = 0;
	
	public static Item[] items = new Item[24];
	
	
	public GameClient(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException
	{
		gui.init();
		
		tileset_castle = new Image("res/castle.png");
		tileset_owlish = new Image("res/owlishmedia_pixel_tiles.png");
		
		spriteset = new Image("res/spriteset.png");
		
		Chicken.init();
		
		for (int i=0; i<24; i++)
		{
			items[i] = new TestItem();
		}
		items[20] = new Apple();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException
	{
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
				spawnProjectile();
			}
			
			//update particles
			for (int p=0; p<particles.size(); p++)
			{
				particles.get(p).update();
				if (particles.get(p).time == 0)
				{
					particles.remove(p);
				}
			}
			
			//update projectiles
			updateProjectiles();
			
			//get visible tiles
			getVisibleTiles();
			
		}
	    
	}

	private void spawnProjectile()
	{
		Projectile newProjectile = new Projectile();
		
		newProjectile.position.x = (float) (player.x + 16 + (aimX*12) );
		newProjectile.position.y = (float) (player.y + 16 + (aimY*12) );
		
		newProjectile.direction.x = (float) aimX;
		newProjectile.direction.y = (float) aimY;
		newProjectile.time = 80;
		
		newProjectile.speed = 4;
		
		PacketAddProjectile packet = new PacketAddProjectile();
		packet.projectile = newProjectile;
		network.client.sendUDP(packet);
		
		player.attackTimer = 50;
	}
	
	private void updateProjectiles() {
		
		for (Projectile projectile : currentMap.projectiles.values())
		{
			if (projectile.time == 0)
			{
				currentMap.projectiles.remove(projectile.id);
			}
			projectile.update();
		}
		
	}

	private void updateViewOffset() {
		
		offsetX = ((spawnX)*-1 - player.x + (640));
		offsetY = ((spawnY)*-1 - player.y + (360));
		
		
		if (offsetX > 0) {offsetX = 0;}
		if (offsetY > 0) {offsetY = 0;}
		if (offsetX < -currentMap.width*32 + 1280+32) {offsetX = -currentMap.width*32 + 1280+32;}
		if (offsetY < -currentMap.height*32 + 720+32) {offsetY = -currentMap.height*32 + 720+32;}
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
		else if (gc.getInput().isKeyPressed(Input.KEY_R))
		{
			if (player.mapID == 0)
			{
				requestMap(1);
			}
			else
			{
				requestMap(0);
			}
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

	private void requestMap(int mapID) {
		mapLoaded = false;
		player.mapID = mapID;
		
		PacketMapRequest packet = new PacketMapRequest();
		packet.mapID = mapID;
		
		network.client.sendTCP(packet);
		
		
		
		
		
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		
		if (mapLoaded == true)
		{
		
		//draw map first layer around player
		if (player.x > 0) {
		int tile, tileX, tileY;
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), currentMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), currentMap.width - 1); drawX++)
			{
				
				for (int layer=0; layer<2; layer++)
				{
					
					if (visibleTiles[drawX][drawY] == 1 | currentMap.type == 0)
					{
						tile = (currentMap.layers[layer].data[drawX][drawY]);
						tileY = (int) (Math.floor(tile / 16));
						tileX = (int) (tile - (tileY*16));
						if (tile >= 0)
						{
							g.drawImage(currentTileset, drawX*32 + Math.round(offsetX), drawY*32 + Math.round(offsetY), (drawX*32) + 32 + Math.round(offsetX), (drawY*32) + 32 + Math.round(offsetY), tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
						}
						
					}
						
				}
			}
		}
		
		//draw player
		g.drawImage(spriteset, player.x + offsetX, player.y + offsetY, player.x+32 + offsetX, player.y+32 + offsetY, (int)player.spriteX*32, (int)player.spriteY*32, ((int)player.spriteX*32) + 32, ((int)player.spriteY*32) + 32, new Color(255,255,255));
		
		//draw other players
		for(Player mpPlayer : currentMap.players.values())
		{
			g.drawImage(spriteset, (int)mpPlayer.x + offsetX, (int)mpPlayer.y + offsetY, (int)mpPlayer.x+32 + offsetX, (int)mpPlayer.y+32 + offsetY, (int)mpPlayer.spriteX*32, (int)mpPlayer.spriteY*32, ((int)mpPlayer.spriteX*32) + 32, ((int)mpPlayer.spriteY*32) + 32, new Color(255,255,255));
		}
		
		//draw mobs (new)
		for(Mob mob : currentMap.mobs.values())
		{
			mob.draw(gc, g, offsetX, offsetY);
			
			//draw mob health bars (new)
			if (mob.health < mob.maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(mob.position.x + offsetX, mob.position.y + offsetY - 5, 32* ((float)mob.health / (float)mob.maxHealth), 5);
			}
		}
		
		
		//draw map around player (other layers)
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), currentMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), currentMap.width - 1); drawX++)
			{
				
				for (int layer=2; layer<currentMap.layerCount-1; layer++)
				{
					tile = (currentMap.layers[layer].data[drawX][drawY]);
					tileY = (int) (Math.floor(tile / 16));
					tileX = (int) (tile - (tileY*16));
					
					if (tile != -1)
					{
						g.drawImage(currentTileset, drawX*32 + Math.round(offsetX), drawY*32 + Math.round(offsetY), (drawX*32) + 32 + Math.round(offsetX), (drawY*32) + 32 + Math.round(offsetY), tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
					}
					
					if (layer == 2 && currentMap.type == 1)
					{	
						//g.drawImage(tileset, drawX*32 + Math.round(offsetX), drawY*32 + Math.round(offsetY), (drawX*32) + 32 + Math.round(offsetX), (drawY*32) + 32 + Math.round(offsetY), tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
						
						int distance = (int) Math.hypot((player.x) - (drawX*32), (player.y) - (drawY*32));
						g.setColor(new Color(0, 0, 0, (int)(distance/1.2)));
						g.fillRect(drawX*32 + Math.round(offsetX), drawY*32 + Math.round(offsetY), 32, 32);
					}
						
				}
			}
		}
		
		
		
		}
	
		for(Projectile projectile : currentMap.projectiles.values())
		{
			g.setColor(new Color(80, 80, 80, projectile.time*20 ));
			g.fillOval(projectile.position.x - 2 + offsetX, projectile.position.y - 2 + offsetY, projectile.size, projectile.size);
		}
		
		}
		
		g.setColor(new Color(50, 50, 50, 200));
		g.fillRect( (int)((player.x+16)/32)*32 + Math.round(offsetX), (int)((player.y+16)/32)*32 + Math.round(offsetY), 32, 32);
		
		
		gui.draw(gc, g);

		
	}
	
	public void getVisibleTiles()
	{
		int rayLength = 8;
		int rayCount = 50;
		for (double lightI=0; lightI<2*Math.PI; lightI+= (2*Math.PI)/rayCount)
		{
			for (int rayI=0; rayI<rayLength; rayI++)
			{
				int tileX = (int) ((player.x+16)/32);
				int tileY = (int) ((player.y+16)/32);
				int addX = (int) (Math.cos(lightI)*rayI);
				int addY = (int) (Math.sin(lightI)*rayI);
				if (currentMap.layers[0].data[(tileX)+addX][(tileY)+addY] == 16)
				{
					visibleTiles[(tileX)+addX][(tileY)+addY] = 1;
					break;
				}
				else
				{
					visibleTiles[(tileX)+addX][(tileY)+addY] = 1;
					visibleTiles[(tileX)+addX-1][(tileY)+addY] = 1;
					visibleTiles[(tileX)+addX+1][(tileY)+addY] = 1;
					visibleTiles[(tileX)+addX][(tileY)+addY-1] = 1;
					visibleTiles[(tileX)+addX][(tileY)+addY+1] = 1;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new GameClient("Slick2d Window"));
			appgc.setDisplayMode(1280, 720, false);
			appgc.setTargetFrameRate(60);
			appgc.setVSync(true);
			appgc.setMaximumLogicUpdateInterval(60);
			appgc.setShowFPS(true);
			appgc.setAlwaysRender(true);

			network.connect();
			
			appgc.start();

		}
		catch (SlickException ex)
		{
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}