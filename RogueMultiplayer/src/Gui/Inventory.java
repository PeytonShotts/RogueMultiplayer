package Gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Inventory extends GuiElement{
	
	static Image inventoryImage;
	
	public static int x;
	public static int y;
	
	public static int startX;
	public static int startY;
	
	//local mouse variables
	public static int mouseX;
	public static int mouseY;
	
	public void init() throws SlickException
	{
		inventoryImage = new Image("res/inventory.png");
		
		x = 800;
		y = 200;
		
		visible = false;
	}
	
	public void draw(GameContainer gc, Graphics g)
	{
		inventoryImage.draw(x, y);
	}
	
	public static void update(int screenMouseX, int screenMouseY, boolean mouseClick, boolean mouseOne)
	{
		if (screenMouseX > x && screenMouseX < x + inventoryImage.getWidth() &&
			screenMouseY > y && screenMouseY < y + inventoryImage.getHeight() )
		{
			mouseX = screenMouseX - x;
			mouseY = screenMouseY - y;
			
			if (mouseY < 60)
			{
				if (mouseClick)
				{
					startX = mouseX;
					startY = mouseY;
				}
				
				if (mouseOne)
				{
					x += mouseX - startX;
					y += mouseY - startY;
				}
			}

		}
		else
		{
			mouseX = 0;
			mouseY = 0;
		}

	}

}
