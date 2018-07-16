package Gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Minimap {

	Image minimapImage;
	
	public void init() throws SlickException {
		
		minimapImage = new Image("res/mapwithbuttons.png");
		
	}
	
	public void draw(GameContainer gc, Graphics g)
	{
		minimapImage.draw(1280 - minimapImage.getWidth(), 0);
	}

}
