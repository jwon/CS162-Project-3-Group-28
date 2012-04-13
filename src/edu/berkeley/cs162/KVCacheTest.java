package edu.berkeley.cs162;

import java.io.Serializable;
import static org.junit.Assert.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

public class KVCacheTest {

	static void cacheTest(){

		KVCache<Integer, Integer> testCache = new KVCache<Integer, Integer>(100);

		for (int i=0; i<100;i++){
			testCache.put(i, i);
		}
		
		//testing that least recently used entry was deleted, after exceeding cache capacity
		testCache.put(100, 100);
		assertTrue(testCache.get(0)==null && testCache.filledEntries() == 100);
		
		//testing that delete gets rid of the proper entry
		testCache.del(50);
		assertTrue(testCache.get(50)==null && testCache.filledEntries()== 99);
		
		//refilling
		testCache.put(101, 101);
		assertTrue(testCache.filledEntries()==100);
		
		//testing that accessing a KV pair makes it the MRU pair
		testCache.get(1);
		testCache.put(102, 102);
		assertTrue(testCache.get(1)==1 && testCache.get(2)==null && testCache.filledEntries()==100);
		
	}
	
	public static void main(String[] args){
		cacheTest();
	}
}
