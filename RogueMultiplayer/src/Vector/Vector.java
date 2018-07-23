package Vector;

public class Vector implements java.io.Serializable{
	
	public float x;
	public float y;
	
	public Vector()
	{
		
	}
	
	public Vector(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float total()
	{
		float total = Math.abs(this.x) + Math.abs(this.y);
		return total;
	}

}
