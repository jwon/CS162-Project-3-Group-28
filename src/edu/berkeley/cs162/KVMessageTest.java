package edu.berkeley.cs162;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import javax.xml.bind.DatatypeConverter;

import org.junit.Test;



public class KVMessageTest {

	@Test
	public void marshallUnmarshalTest() {
		String s = "blah blah blah";
		String m = KVMessage.marshal(s);
		String s2 = "";
		try {
			s2 = (String)KVMessage.unmarshal(m);
		} catch (IOException e) {
			fail();
		} catch (ClassNotFoundException e) {
			fail();
		}
		assertEquals(s,s2);
	}
	
	@Test
	public void initTest() {
		Serializable key = "blah blah key";
		Serializable value = "blah blah value";
		KVMessage m = new KVMessage("blah blah type", key, value, false, "blah blah message");
	}
	
	@Test
	public void toXMLTest() {
		Serializable key = "blah blah key";
		Serializable value = "blah  value";
		KVMessage m = new KVMessage("blah blah type", key, value, false, "blah blah message");
		System.out.println("toXML results:");
//		try {
//			System.out.println(m.toXML());
//		} catch (KVException e) {
//			fail();
//		}
		
		
	}
	
	@Test
	public void streamInitTest() {
//		Serializable key = "blah blah key";
//		Serializable value = "blah  value";
//		KVMessage m = new KVMessage("blah blah type", key, value, false, "blah blah message");
//		
//		String xmlStr;
//		try {
//			xmlStr = m.toXML();
//		} catch (KVException e) {
//			fail();
//			return;
//		}
//		System.out.println("For streamInitTest: \nthe XML is: \n" + xmlStr);
//		
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><KVMessage type=\"resp\"><Message>Error Message</Message></KVMessage>";
		byte[] xmlBytes = null;
		try {
			xmlBytes = xmlStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		KVMessage m2;
		ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
		try {
			m2 = new KVMessage(bais);
		} catch (KVException e) {
			System.out.println(e.getMsg());
			fail();
			return;
		}
		assertEquals(m2.getMsgType(), "resp");
		// The following tests invalidated because missing elements will cause
		// associated KVMessage instance variables to == null
//		try {
//			assertEquals(KVMessage.unmarshal(m2.getKey()), null);
//			assertEquals(KVMessage.unmarshal(m2.getValue()), null);
//		} catch (IOException e) {
//			fail();
//		} catch (ClassNotFoundException e) {
//			fail();
//		}
		assertFalse(m2.getStatus());
		assertEquals(m2.getMessage(), "Error Message");
		
		
	}
	
	@Test
	public void encodingTest() {
		Serializable key = "blah blah key";
		Serializable value = "blah blah value";
		KVMessage m = new KVMessage("resp", key, value, false, "blah blah message");
		
		String xmlStr;
		try {
			xmlStr = m.toXML();
		} catch (KVException e) {
			fail();
			return;
		}
		System.out.println("For streamInitTest: \nthe XML is: \n" + xmlStr);
		
		byte[] xmlBytes = null;
		try {
			xmlBytes = xmlStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		KVMessage m2;
		ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
		try {
			m2 = new KVMessage(bais);
		} catch (KVException e) {
			System.out.println(e.getMsg());
			fail();
			return;
		}
		assertEquals(m2.getMsgType(), "resp");
		
		try {
			assertEquals(KVMessage.unmarshal(m2.getKey()), "blah blah key");
			assertEquals(KVMessage.unmarshal(m2.getValue()), "blah blah value");
		} catch (IOException e) {
			fail();
		} catch (ClassNotFoundException e) {
			fail();
		}
		assertFalse(m2.getStatus());
		assertEquals(m2.getMessage(), "blah blah message");
	}

}
