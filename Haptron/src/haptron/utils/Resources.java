package haptron.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resources {

	public static InputStream getResource(final String resource) throws NullPointerException {
		InputStream stream = Resources.class.getClassLoader().getResourceAsStream(resource.replace(".", "/"));
		if(stream == null) throw new NullPointerException("Couldn't find resource: " + resource);
		return stream;
	}
	
	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024];

		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		
		return buffer.toByteArray();
	}
}
