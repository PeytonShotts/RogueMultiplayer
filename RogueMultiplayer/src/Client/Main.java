package Client;

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

import Client.Mobs.Snake;

public class Main extends BasicGame
{
	static Map testMap = MapGen.create(100, 100);
	
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
	
	float rotate;
	
	boolean hasInit = false;
	
	String moveDirection;
	
	Image tileset;
	Image spriteset;
	
	int spawnX;
	int spawnY;
	
	static Player player = new Player();
	
	static boolean leftClick;
	
	
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
		if (player.x == 0 && player.y == 0) {player.x = testMap.spawnPoint.x; player.y = testMap.spawnPoint.y;}

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
			testMap.projectileList.add(newProjectile);
			
			player.attackTimer = 50;
		}
		
		//update projectiles
		updateProjectiles();
		
		//update mobs
		updateMobs();
		
		//calculate visible blocks
		calculateVisibleBlocks();
	    
	    
	}

	private void updateProjectiles() {
		
		for(int projectileI=0; projectileI<testMap.projectileList.size(); projectileI++)
		{
			testMap.projectileList.get(projectileI).x += testMap.projectileList.get(projectileI).directionX*2;
			testMap.projectileList.get(projectileI).y += testMap.projectileList.get(projectileI).directionY*2;
			
			testMap.projectileList.get(projectileI).time++;
			
			if (testMap.projectileList.get(projectileI).time > 20)
			{
				testMap.projectileList.remove(projectileI);
			}
			
		}
		
	}

	private void updateMobs() {
		
		for(int mobI=0; mobI<testMap.mobList.size(); mobI++)
		{
			testMap.mobList.get(mobI).update();
			
			testMap.mobList.get(mobI).isHit = false;
			for(int projectileI=0; projectileI<testMap.projectileList.size(); projectileI++)
			{
				if (testMap.mobList.get(mobI).x < testMap.projectileList.get(projectileI).x + 8 &&
					testMap.mobList.get(mobI).x + testMap.mobList.get(mobI).width > testMap.projectileList.get(projectileI).x &&
					testMap.mobList.get(mobI).y < testMap.projectileList.get(projectileI).y + 8 &&
					testMap.mobList.get(mobI).height + testMap.mobList.get(mobI).y > testMap.projectileList.get(projectileI).y) 
						{
							testMap.mobList.get(mobI).isHit = true;
							testMap.mobList.get(mobI).health += -1;
							
							testMap.mobList.get(mobI).addX = testMap.projectileList.get(projectileI).directionX*3;
							testMap.mobList.get(mobI).addY = testMap.projectileList.get(projectileI).directionY*3;
							
							testMap.projectileList.remove(projectileI);
							
							if (testMap.mobList.get(mobI).health <= 0)
							{
								testMap.mobList.remove(mobI);
							}
						}
			}
		}
		
	}

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
				
				if ( (lightBlockX-offsetX)/32 > 0 && (lightBlockX-offsetX)/32 < testMap.width-1 &&
					 (lightBlockY-offsetY)/32 > 0 && (lightBlockY-offsetY)/32 < testMap.height-1)
				{
					if (testMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isRoom == true)
					{
						testMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isVisible = true;
					}
					else
					{
						testMap.tileArray[ (lightBlockX-offsetX)/32][ (lightBlockY-offsetY)/32].isVisible = true;
						break;
					}
				}
						
			}
		}
		
	}

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
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY < Math.min( ((player.y/32) + 20), testMap.height - 1); drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX < Math.min( ((player.x/32) + 20), testMap.width - 1); drawX++)
			{
				Tile t = (testMap.tileArray[drawX][drawY]);
				int roomX;
				int roomY;
				if (t.side == 1) {roomX = 1; roomY = 0;}
				else if (t.side == 3) {roomX = 1; roomY = 1;}
				else if (t.side == 5) {roomX = 0; roomY = 1;}
				else if (t.side == 7) {roomX = 4; roomY = 1;}
				else {roomX = 0; roomY = 4;}
				if (testMap.tileArray[drawX][drawY].isRoom == true)
				{
					g.drawImage(tileset, drawX*32 + offsetX, drawY*32 + offsetY, (drawX*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
					
					boolean leftWall = false;
					boolean rightWall = false;
					boolean topWall = false;
					
					//nothing above
					if (drawY-1 >= 0)
					{
						if (testMap.tileArray[drawX][drawY-1].isRoom == false)
						{
							roomX = 1; roomY = 0;
							g.drawImage(tileset, drawX*32 + offsetX, (drawY-1)*32 + offsetY, (drawX*32) + 32 + offsetX, ((drawY-1)*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
							topWall = true;
						}
					}
					//nothing below
					if (drawY+1 < testMap.height)
					{
						if (testMap.tileArray[drawX][drawY+1].isRoom == false)
						{
							roomX = 4; roomY = 1;
							g.drawImage(tileset, drawX*32 + offsetX, (drawY+1)*32 + offsetY, (drawX*32) + 32 + offsetX, ((drawY+1)*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
						}
					}
					//nothing to left
					if (drawX-1 >= 0)
					{
						if (testMap.tileArray[drawX-1][drawY].isRoom == false)
						{
							roomX = 1; roomY = 1;
							g.drawImage(tileset, (drawX-1)*32 + offsetX, drawY*32 + offsetY, ((drawX-1)*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
							leftWall = true;
						}
					}
					//nothing to right
					if (drawX+1 < testMap.width)
					{
						if (testMap.tileArray[drawX+1][drawY].isRoom == false)
						{
							roomX = 0; roomY = 1;
							g.drawImage(tileset, (drawX+1)*32 + offsetX, drawY*32 + offsetY, ((drawX+1)*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
							rightWall = true;
						}
					}
					
					if (leftWall == true && topWall == true)
					{
						roomX = 1; roomY = 1;
						g.drawImage(tileset, (drawX-1)*32 + offsetX, (drawY-1)*32 + offsetY, ((drawX-1)*32) + 32 + offsetX, ((drawY-1)*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
					}
					if (rightWall == true && topWall == true)
					{
						roomX = 0; roomY = 1;
						g.drawImage(tileset, (drawX+1)*32 + offsetX, (drawY-1)*32 + offsetY, ((drawX+1)*32) + 32 + offsetX, ((drawY-1)*32) + 32 + offsetY, roomX*32, roomY*32, (roomX*32) + 32, (roomY*32) + 32);
					}

				}
				/*
				else
				{
					g.drawImage(tileset, drawX*32 + offsetX, drawY*32 + offsetY, (drawX*32) + 32 + offsetX, (drawY*32) + 32 + offsetY, 22*32, 13*32, (22*32) + 32, (13*32) + 32, new Color(200,200,200));
				}
				*/
				
				g.setColor(new Color (0,0,0, 20));
				g.drawRect(drawX*32 + offsetX, drawY*32 + offsetY, 32, 32);
			}
		}
		
		//draw player
		g.drawImage(spriteset, (int)player.x + offsetX, (int)player.y + offsetY, (int)player.x+32 + offsetX, (int)player.y+32 + offsetY, (int)player.spriteX*32, (int)player.spriteY*32, ((int)player.spriteX*32) + 32, ((int)player.spriteY*32) + 32, new Color(255,255,255));
		
		//draw mobs
		for(int mobI=0; mobI<testMap.mobList.size(); mobI++)
		{
		
			if (testMap.mobList.get(mobI).isHit)
			{
				g.drawImage(tileset, 
						testMap.mobList.get(mobI).x + offsetX, testMap.mobList.get(mobI).y + offsetY, testMap.mobList.get(mobI).x + offsetX + 32, testMap.mobList.get(mobI).y + offsetY + 32,
						(testMap.mobList.get(mobI).spriteX)*32, (testMap.mobList.get(mobI).spriteY)*32, ((testMap.mobList.get(mobI).spriteX)*32) + 32, ((testMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,0,0));
			}
			else
			{
				g.drawImage(tileset, 
						(int) testMap.mobList.get(mobI).x + offsetX, (int) testMap.mobList.get(mobI).y + offsetY, (int) testMap.mobList.get(mobI).x + offsetX + 32, (int) testMap.mobList.get(mobI).y + offsetY + 32,
						(testMap.mobList.get(mobI).spriteX)*32, (testMap.mobList.get(mobI).spriteY)*32, ((testMap.mobList.get(mobI).spriteX)*32) + 32, ((testMap.mobList.get(mobI).spriteY)*32) + 32, 
						new Color(255,255,255));
			}
					
			//draw health bar if mob health is under maximum
			if (testMap.mobList.get(mobI).health < testMap.mobList.get(mobI).maxHealth)
			{
				g.setColor(new Color(50,250,50,180));
				g.fillRect(testMap.mobList.get(mobI).x + offsetX, testMap.mobList.get(mobI).y + offsetY - 5, 32* ((float)testMap.mobList.get(mobI).health / testMap.mobList.get(mobI).maxHealth), 5);
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
		for (int drawY = (int) Math.max( ((player.y/32) - 20), 0) ; drawY<(player.y/32) + 20; drawY++)
		{
			for (int drawX = (int) Math.max( ((player.x/32) - 20), 0); drawX<(player.x/32) + 20; drawX++)
			{
				xDist = Math.abs((float)drawX - (float)player.x/32); 
				yDist = Math.abs((float)drawY - (float)player.y/32);
				double blockDist = Math.sqrt((xDist*xDist) + (yDist*yDist));
				if (drawX >= 0 && drawX < testMap.width-1 &&
					drawY >= 0 && drawY < testMap.height-1)
				{
					if (testMap.tileArray[drawX][drawY].isVisible)
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
		
				
				/*
				if (blockDist > 10)
				{
					if (drawX > 0 && drawX < testMap.width-1 && 
							drawY > 0 && drawY < testMap.height-1)
					{
						testMap.tileArray[drawX][drawY].isVisible = false;
					}	
				}
				*/
			}
		}
		
		}
		
		//draw minimap
		int minimapSize = 3;
		int minimapOpacity = 150;
		
		g.setColor(new Color(250,250,250, 5));
		g.drawRect(0, 0, testMap.width*minimapSize, testMap.height*minimapSize);
		
		for (int drawY=0;drawY<testMap.height;drawY++)
		{
			for (int drawX=0;drawX<testMap.width;drawX++)
			{
				if (testMap.tileArray[drawX][drawY].isRoom == true && testMap.tileArray[drawX][drawY].isVisible == true)
				{
					g.setColor(new Color(80,250,155, minimapOpacity));
					g.fillRect(drawX*minimapSize, drawY*minimapSize, minimapSize, minimapSize);
				}
				else if (testMap.tileArray[drawX][drawY].isRoom == false && testMap.tileArray[drawX][drawY].isVisible == true)
				{
					g.setColor(new Color(80,80,120, minimapOpacity));
					g.fillRect(drawX*minimapSize, drawY*minimapSize, minimapSize, minimapSize);
				}
				else
				{
					g.setColor(new Color(0,0,0, 30));
					g.fillRect(drawX*minimapSize, drawY*minimapSize, minimapSize, minimapSize);
				}
			}
		}
		
		//draw minimap player icon
		g.setColor(new Color(238,231,34));
		g.fillRect( ((float)player.x/32)*minimapSize, ((float)player.y/32)*minimapSize, minimapSize, minimapSize);
		g.setColor(new Color(50,50,50));
		g.drawRect(((float)player.x/32)*minimapSize, ((float)player.y/32)*minimapSize, minimapSize, minimapSize);
		
		
		//draw projectiles
		for(int projectileI=0; projectileI<testMap.projectileList.size(); projectileI++)
		{
			g.setColor(new Color(180, 170, 180, 550 - (testMap.projectileList.get(projectileI).time)*28 ));
			g.fillRect(testMap.projectileList.get(projectileI).x - 2 + offsetX, testMap.projectileList.get(projectileI).y - 2 + offsetY, 8, 8);
		}
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("RogueMan"));
			appgc.setDisplayMode(1280, 720, false);
			appgc.setTargetFrameRate(60);
			appgc.setShowFPS(false);
			appgc.start();

		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}