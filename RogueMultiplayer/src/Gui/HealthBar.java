package Gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class HealthBar {
	
	static Image healthbarImage;
	static int x, y;
	
	public void init() throws SlickException
	{
		healthbarImage = new Image("res/healthbar.png");
		
		x = 640 - (healthbarImage.getWidth()/2);
		y = 720 - (healthbarImage.getHeight()) - 20;
	}
	
	public void draw(GameContainer gc, Graphics g)
	{
		healthbarImage.draw(x, y);
	}

}
