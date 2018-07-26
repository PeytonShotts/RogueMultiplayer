package Mob;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Chicken extends Mob{
	
	public static void init() throws SlickException
	{
		spriteSheet = new Image("res/chicken.png");
	}
	
	public Chicken()
	{
		maxHealth = 50;
		health = 50;
		spriteCount = 3;
		spriteX = 0;
		spriteY = 0;
	}

}
