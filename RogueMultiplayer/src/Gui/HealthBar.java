package Gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import Client.Main;

public class HealthBar {
	
	static Image healthbarImage;
	static Image healthbarFillImage;
	static int x, y;
	
	public int maxWidth;
	public int maxHeight;
	
	public float health = 100;
	
	public void init() throws SlickException
	{
		healthbarImage = new Image("res/healthbar.png");
		healthbarFillImage = new Image("res/healthbarFill.png");
		
		x = 640 - (healthbarImage.getWidth()/2);
		y = 720 - (healthbarImage.getHeight()) - 20;
		
		maxWidth = healthbarFillImage.getWidth();
		maxHeight = healthbarFillImage.getHeight();
	}
	
	public void draw(GameContainer gc, Graphics g)
	{
		healthbarImage.draw(x, y);
		healthbarFillImage.draw(x + 18, y + 6, (x+18) + maxWidth * (Main.player.health/100), (y+6) + maxHeight, 0, 0, maxWidth, maxHeight);
	}

}
