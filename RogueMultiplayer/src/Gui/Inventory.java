package Gui;

import org.newdawn.slick.Color;
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
	
	int selectedItem;
	public boolean itemSelected;
	
	
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
		for (int inventoryY=0; inventoryY<6; inventoryY++)
		{
			for (int inventoryX=0; inventoryX<4; inventoryX++)
			{
				g.setColor(new Color(250, 250, 250, 200));
				g.drawString("Test drawing string.",20.0f,20.0f);
				int drawX = x+27 + inventoryX*36;
				int drawY = y+77 +inventoryY*36 + 5;
				g.fillRect(drawX, drawY, 32, 32);
				if ( mouseX > drawX-x && mouseX < drawX-x + 32
				  && mouseY > drawY-y && mouseY < drawY-y + 32)
				{
					g.setColor(new Color(250, 0, 0, 250));
					g.fillRect(drawX, drawY, 32, 32);
				}
				//System.out.println("mouse " + mouseX + " " + mouseY);
				System.out.println((drawX-x) + " " + (drawY-y));
				
				//g.drawImage(Client.Main.items[inventoryY*4 + inventoryX].sprite, x+27 + inventoryX*36, y+77 +inventoryY*36 + 5);
			}
		}
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
