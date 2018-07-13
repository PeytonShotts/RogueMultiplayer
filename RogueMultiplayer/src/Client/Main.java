package Client;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Client.Mobs.*;

public class Main extends BasicGame
{
	static Map currentMap;
	
	static byte[] mapData = new byte[1000000];
	
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
	
	static Player player = new Player();
	static java.util.Map<Integer,Player> players = new HashMap<Integer,Player>(); 
	
	static boolean leftClick;
	
	static Network network = new Network();

	public static boolean mapLoaded = false;
	
	
	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException
	{
		
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException
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
		
		//add new projectile when mouse is pressed
		if(gc.getInput().isMouseButtonDown(0) && player.attackTimer == 0)
		{
			Projectile newProjectile = new Projectile();
			newProjectile.x = (float) (player.x + 16 + (aimX*12) );
			newProjectile.y = (float) (player.y + 16 + (aimY*12) );
			newProjectile.directionX = (float) aimX;
			newProjectile.directionY = (float) aimY;
			currentMap.projectileList.add(newProjectile);
			
			player.attackTimer = 50;
		}
		
		//update projectiles
		updateProjectiles();
		
		//update mobs
		updateMobs();
		
		//calculate visible blocks
		//calculateVisibleBlocks();
	    
	    
	}

	private void updateProjectiles() {
		
		for(int projectileI=0; projectileI<currentMap.projectileList.size(); projectileI++)
		{
			currentMap.projectileList.get(projectileI).x += currentMap.projectileList.get(projectileI).directionX*2;
			currentMap.projectileList.get(projectileI).y += currentMap.projectileList.get(projectileI).directionY*2;
			
			currentMap.projectileList.get(projectileI).time++;
			
			if (currentMap.projectileList.get(projectileI).time > 20)
			{
				currentMap.projectileList.remove(projectileI);
			}
			
		}
		
	}

	private void updateMobs() {
		
		for(int mobI=0; mobI<currentMap.mobList.size(); mobI++)
		{
			currentMap.mobList.get(mobI).update();
			
			currentMap.mobList.get(mobI).isHit = false;
			for(int projectileI=0; projectileI<currentMap.projectileList.size(); projectileI++)
			{
				if (currentMap.mobList.get(mobI).x < currentMap.projectileList.get(projectileI).x + 8 &&
					currentMap.mobList.get(mobI).x + currentMap.mobList.get(mobI).width > currentMap.projectileList.get(projectileI).x &&
					currentMap.mobList.get(mobI).y < currentMap.projectileList.get(projectileI).y + 8 &&
					currentMap.mobList.get(mobI).height + currentMap.mobList.get(mobI).y > currentMap.projectileList.get(projectileI).y) 
						{
							currentMap.mobList.get(mobI).isHit = true;
							currentMap.mobList.get(mobI).health += -1;
							
							currentMap.mobList.get(mobI).addX = currentMap.projectileList.get(projectileI).directionX*3;
							currentMap.mobList.get(mobI).addY = currentMap.projectileList.get(projectileI).directionY*3;
							
							currentMap.projectileList.remove(projectileI);
							
							if (currentMap.mobList.get(mobI).health <= 0)
							{
								currentMap.mobList.remove(mobI);
							}
						}
			}
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
		
		
		if (gc.getInput().isMouseButtonDown(0))
		{
			leftClick = true;
		}
		else
		{
			leftClick = false;
		}
		
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
		//load images when client first opens
		if (hasInit == false) {tileset = new Image("res/tileset.png"); spriteset = new Image("res/spriteset.png"); hasInit = true;}
		
		//draw map around player
		if (player.x > 0) {
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), currentMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), currentMap.width - 1); drawX++)
			{
				int tile = (currentMap.layers[0].data[drawX][drawY]);
				int tileX = (int) (tile - Math.floor(currentMap.width / 100));
				int tileY = (int) (Math.floor(currentMap.width / 100) * 100);
				
				g.drawImage(tileset, drawX*32 + offsetX, drawY*32 + offsetY, (drawX*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, tileX*32, tileY*32, (tileX*32) + 32, (tileY*32) + 32);
			}
		}
		
		//draw player
		g.drawImage(spriteset, (int)player.x + offsetX, (int)player.y + offsetY, (int)player.x+32 + offsetX, (int)player.y+32 + offsetY, (int)player.spriteX*32, (int)player.spriteY*32, ((int)player.spriteX*32) + 32, ((int)player.spriteY*32) + 32, new Color(255,255,255));
		
		//draw other players
		for(Player mpPlayer : players.values())
		{
		g.drawImage(spriteset, (int)mpPlayer.x + offsetX, (int)mpPlayer.y + offsetY, (int)mpPlayer.x+32 + offsetX, (int)mpPlayer.y+32 + offsetY, (int)mpPlayer.spriteX*32, (int)mpPlayer.spriteY*32, ((int)mpPlayer.spriteX*32) + 32, ((int)mpPlayer.spriteY*32) + 32, new Color(255,255,255));
		}
		
		//draw mobs
		for(int mobI=0; mobI<currentMap.mobList.size(); mobI++)
		{
		
			if (currentMap.mobList.get(mobI).isHit)
			{
				g.drawImage(tileset, 
						currentMap.mobList.get(mobI).x + offsetX, currentMap.mobList.get(mobI).y + offsetY, currentMap.mobList.get(mobI).x + offsetX + 32, currentMap.mobList.get(mobI).y + offsetY + 32,
						(currentMap.mobList.get(mobI).spriteX)*32, (currentMap.mobList.get(mobI).spriteY)*32, ((currentMap.mobList.get(mobI).spriteX)*32) + 32, ((currentMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,0,0));
			}
			else
			{
				g.drawImage(tileset, 
						(int) currentMap.mobList.get(mobI).x + offsetX, (int) currentMap.mobList.get(mobI).y + offsetY, (int) currentMap.mobList.get(mobI).x + offsetX + 32, (int) currentMap.mobList.get(mobI).y + offsetY + 32,
						(currentMap.mobList.get(mobI).spriteX)*32, (currentMap.mobList.get(mobI).spriteY)*32, ((currentMap.mobList.get(mobI).spriteX)*32) + 32, ((currentMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,255,255));
			}
					
			//draw health bar if mob health is under maximum
			if (currentMap.mobList.get(mobI).health < currentMap.mobList.get(mobI).maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(currentMap.mobList.get(mobI).x + offsetX, currentMap.mobList.get(mobI).y + offsetY - 5, 32* ((float)currentMap.mobList.get(mobI).health / currentMap.mobList.get(mobI).maxHealth), 5);
			}
			
			//draw health bar if player health is under maximum
			if (player.health < player.maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(player.x + offsetX, player.y + offsetY - 5, 32 * ((float)player.health / (float)player.maxHealth), 5);
				System.out.println(player.health);
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
		
		}
		
		for(int projectileI=0; projectileI<currentMap.projectileList.size(); projectileI++)
		{
			g.setColor(new Color(180, 170, 180, 550 - (currentMap.projectileList.get(projectileI).time)*28 ));
			g.fillRect(currentMap.projectileList.get(projectileI).x - 2 + offsetX, currentMap.projectileList.get(projectileI).y - 2 + offsetY, 8, 8);
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
			
			System.out.println("Waiting to receive map data from server...");
			
			while (mapLoaded == false)
			{
				//System.out.println(mapLoaded);
			}
			
			appgc.start();
			

		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}