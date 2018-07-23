package Packet;

import Vector.Vector;

public class PacketPlayerHit {
	
	public int damage;
	public Vector hitVector = new Vector();
	
	public PacketPlayerHit(int damage, Vector hitVector)
	{
		this.damage = damage;
		this.hitVector.x = hitVector.x;
		this.hitVector.y = hitVector.y;
	}
	
	public PacketPlayerHit()
	{
		
	}

}
