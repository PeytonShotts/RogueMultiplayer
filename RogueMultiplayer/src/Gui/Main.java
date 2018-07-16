package Gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame
{	
	int mouseX;
	int mouseY;
	static boolean mouseClick;
	static boolean mouseOne;
	
	Gui gui = new Gui();
	
	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException 
	{
		gui.init();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException 
	{
		Input input = gc.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		mouseClick = gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
		mouseOne = gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		
		
		System.out.println(mouseX + " " + mouseY);
		
		gui.update(mouseX, mouseY, mouseClick, mouseOne);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{	
		gui.draw(gc, g);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("Pong"));
			appgc.setTargetFrameRate(60);
			appgc.setVSync(true);
			appgc.setDisplayMode(1280, 720, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}