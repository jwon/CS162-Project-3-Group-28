package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.zip.DataFormatException;

import org.junit.Test;

public class KVMessageTest {

	@Test
	public void marshallTest() {
		Serializable s = null;
		String m = KVMessage.marshall(s);
		System.out.println(m);
	}
	
	@Test
	public void initTest() {
		Serializable key = "blah blah key";
		Serializable value = "blah blah value";
		try { 
			KVMessage m = new KVMessage("blah blah type", key, value, false, "blah blah message");
		} catch(DataFormatException e) {
			fail();
		}
	}

}
