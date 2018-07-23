package MapGen;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Map.Map;

public class MapGenTester extends BasicGame
{
	int mapWidth = 250;
	int mapHeight = 250;
	Map newMap = MapGen.create(mapWidth, mapHeight, 1);
	int r = 2;
	
	public MapGenTester(String gamename)
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
		//mouseX = Mouse.getX();
	    //mouseY = 720 - Mouse.getY();
		//mouseClick = gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		for (int drawY=0; drawY<mapHeight; drawY++)
		{
			for (int drawX=0; drawX<mapWidth; drawX++)
			{
				g.setColor(new Color(250, 250, 250));
				if (newMap.layers[0].data[drawX][drawY] == 18)
				{
					g.fillRect(drawX*r, drawY*r, r, r);
				}
				else
				{	g.setColor(new Color(0, 0, 0));
					g.fillRect(drawX*r, drawY*r, r, r);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new MapGenTester("Slick2d Window"));
			appgc.setDisplayMode(1280, 720, false);
			appgc.setTargetFrameRate(60);
			appgc.setShowFPS(false);
			appgc.setUpdateOnlyWhenVisible(false);
			appgc.setAlwaysRender(true);

			appgc.start();

		}
		catch (SlickException ex)
		{
			Logger.getLogger(MapGenTester.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
