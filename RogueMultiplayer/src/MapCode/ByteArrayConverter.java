package MapCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import TiledMap.Map;

public class ByteArrayConverter {
	
	public static byte[] convert(Map mapToConvert) throws IOException
	{
		byte[] byteArray;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(mapToConvert);
		  out.flush();
		  byteArray = bos.toByteArray();
		} finally {
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
		return byteArray;
	}

}
