package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.Serializable;
import org.junit.Test;
import junit.framework.TestCase;
import java.util.*;

public class KeyServerTest extends TestCase {
    
    public static void putTest() {
	KeyServer test = new KeyServer(2);
	K key = new K("key");
	V value = new V("value");
	assertTrue(test.put(key, value) == false);
	assertTrue(test.put(key, value) == true);
    }

    public static void getTest() {
	KeyServer test = new KeyServer(1);
	K key = new K("key");
	V value = new V("value");
	test.put(key, value);
	assertTrue(test.get(key) == value);
    }

    public static void delTest() {
	KeyServer test = new KeyServer(1);
	K key = new K("key");
	V value = new V("value");
	test.put(key, value);
	test.del(key);
	assertTrue(test.put(key, value) == false);
    }

    public static void main(String[] args) {
	putTest();
	getTest();
	delTest();
    }
}
