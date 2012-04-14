package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.Serializable;
import org.junit.Test;

public class KeyServerTest{

    public static void putTest() {
	KeyServer test = new KeyServer(2);
	try {
	    assertTrue(test.put("key", "value") == false);
	} catch (KVException e) {
	    System.out.println(e);
	}
	try {
	    assertTrue(test.put("key", "value") == true);
	} catch (KVException e) {
	    System.out.println(e);
	}
    }

    public static void getTest() {
	KeyServer test = new KeyServer(1);
	try {
	    test.put("key", "value");
	} catch (KVException e) {
	    System.out.println(e);
	}
	try {
	    assertTrue(test.get("key") == "value");
	} catch (KVException e) {
	    System.out.println(e);
	}
    }

    public static void delTest() {
	KeyServer test = new KeyServer(1);
	try {
	    test.put("key", "value");
	} catch (KVException e) {
	    System.out.println(e);
	}
	try {
	    test.del("key");
	} catch (KVException e) {
	    System.out.println(e);
	}
	try {
	    assertTrue(test.put("key", "value") == false);
	} catch(KVException e) {
	    System.out.println(e);
	}
    }

    public static void main(String[] args) {
	putTest();
	getTest();
	delTest();
    }
}
