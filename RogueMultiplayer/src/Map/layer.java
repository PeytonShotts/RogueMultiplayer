package Map;


public class layer implements java.io.Serializable{
	
	public int[][] data;
	
	public layer(int width, int height)
	{
		data = new int[width][height];
		
	}

}