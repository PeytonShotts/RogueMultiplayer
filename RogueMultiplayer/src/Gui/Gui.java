package Gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Gui {
	
	HealthBar healthBar = new HealthBar();
	Minimap minimap = new Minimap();
	static Inventory inventory = new Inventory();
	
	public void init() throws SlickException
	{
		inventory.init();
		healthBar.init();
		minimap.init();
	}
	
	public void update(int mouseX, int mouseY, boolean mouseClick, boolean mouseOne)
	{
		if (mouseX > 1240 && mouseX < 1280 && mouseY > 133 && mouseY < 170 && mouseClick == true)
		{
			inventory.visible ^= true;
		}
		
		if (inventory.visible == true)
		{
			Inventory.update(mouseX, mouseY, mouseClick, mouseOne);
		}
		
	}
	
	public void draw(GameContainer gc, Graphics g)
	{
		if (inventory.visible == true)
		{
			inventory.draw(gc, g);
		}
		
		healthBar.draw(gc, g);
		minimap.draw(gc, g);
	}

}
