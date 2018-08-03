package ParticleCode;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import Vector.Vector;

public class Particle {
	
	public Vector position;
	public Vector direction;
	
	public int time;
	public double speed;
	
	public Particle(Vector position, Vector direction, int speed, int time)
	{
		this.position = new Vector(position.x, position.y);
		this.direction = new Vector(direction.x, direction.y);
		this.speed = speed;
		this.time = time;
	}
	
	
	
	public void draw(Graphics g)
	{
		g.setColor(new Color(180,90,50,time*5));
		g.drawRect(position.x + Client.GameClient.offsetX, position.y + Client.GameClient.offsetY, 1, 1);
	}

	public void update() {
		position.x += direction.x*speed;
		position.y += direction.y*speed;
		
		time += -1;
	}

}
