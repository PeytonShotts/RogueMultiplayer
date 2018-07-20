package ParticleCode;

import Client.Main;
import Vector.Vector;

public class CircleExplosion {
	
	Vector position;
	
	int count;
	int speed;
	int time;
	
	public CircleExplosion(Vector position, int count, int speed, int time)
	{
		this.count = count;
		double interval = (Math.PI*2)/count; 
		for (double i=0; i<(2*Math.PI); i+=interval)
		{
			ParticleCode.particles.add(new Particle(position, new Vector((float) Math.cos(i), (float) Math.sin(i)), speed, time));
			System.out.println((2*Math.PI));
		}
	}

}
