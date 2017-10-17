package de.zpid.datawiz.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner {

	static public Object deepCopy(Object oldObj) throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(oldObj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			return ois.readObject();
		} catch (IOException e) {
			throw (e);
		} finally {
			oos.close();
			ois.close();
		}
	}

}