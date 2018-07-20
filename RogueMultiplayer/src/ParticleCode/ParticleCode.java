package ParticleCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Vector.Vector;


public class ParticleCode extends BasicGame
{
	
	static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	public boolean mouseClick;
	public int mouseX;
	public int mouseY;
	
	public ParticleCode(String gamename)
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
		for (Particle particle : particles)
		{
			particle.update();
		}
		
		
		mouseX = Mouse.getX();
	    mouseY = 720 - Mouse.getY();
		mouseClick = gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
		
		if (mouseClick == true)
		{
			CircleExplosion a = new CircleExplosion(new Vector(mouseX, mouseY), 500, 2, 100);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		int i = 0;
		for (Particle particle : particles)
		{
			particle.draw(g);
			if (particle.time == 0)
			{
				particles.remove(i);
			}
			i++;
		}
	}
	
	public static void main(String[] args)
	{
		
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new ParticleCode("Slick2d Window"));
			appgc.setDisplayMode(1280, 720, false);
			appgc.setTargetFrameRate(60);
			appgc.setShowFPS(true);
			appgc.setUpdateOnlyWhenVisible(false);
			appgc.setAlwaysRender(true);

			appgc.start();

		}
		catch (SlickException ex)
		{
			Logger.getLogger(ParticleCode.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}