package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

public class KVClientTest {

	@Test
	public static void testConversion() throws IOException, ClassNotFoundException{
		Key k1 = new Key("key1");
		Value v1 = new Value("value1");
		
		// Test toString
		String k1s = KVMessage.marshal(k1);
		System.out.println("k1s is " + k1s);

		String v1s = KVMessage.marshal(v1);
		System.out.println("v1s is " + v1s);
		
		
		// Test fromString
		Key k2 = (Key) KVMessage.unmarshal(k1s);
		Value v2 = (Value) KVMessage.unmarshal(v1s);
		
		// key and value should be the same after transformation
		if(k2.equals(k1)){
			System.out.println("Yay! key unmarshalling worked!");
		}
		else{
			System.out.println("No, key unmarshalling failed...");
		}
		
		
		if(v2.equals(v1)){
			System.out.println("Yay! value unmarshalling worked!");
		}
		else{
			System.out.println("No, value unmarshalling failed...");
		}
	}
	
	@Test
	public static void testPutGetDel() throws KVException, IOException{

		KVClient<Key, Value> kvc = new KVClient<Key, Value>("localhost", 8080);
		Key k1 = new Key("key1");
		Value v1 = new Value("value1");

		/** Not sure about behavior of put */
		//Test put
		
		if(kvc.put(k1, v1)){
			System.out.println("Yay! put worked!");
		}
		else{
			System.out.println("No, put failed...");
		}

		//Test get
		if(v1.equals(kvc.get(k1))){
			System.out.println("Yay! get worked!");
		}
		else{
			System.out.println("No, get failed...");
		}

		//Test del, get
		kvc.del(k1);
		boolean test = true;
		try{
			Value exception = kvc.get(k1);
		}catch(KVException kve){
			test = false;
		}
		if(test){
			System.out.println("Yay! del worked!");
		}
		else{
			System.out.println("No, del failed...");
		}

	}
	
	public static void main(String args[]) {
		try{
			testConversion();
		} catch(IOException e){
			System.out.println("IO Exception");
		} catch(ClassNotFoundException e2){
			System.out.println("ClassNotFound Exception");
		}
		
		try{
			testPutGetDel();
		} catch(IOException e){
			System.out.println("IO Exception");
		} catch(KVException e2){
			System.out.println(e2.getMsg());
		}
    }
	
}
	
  	class Key implements Serializable{
		public Key(String key){
			this.key = key;
		}
		public String toString(){
			return key;     
		}
		
		public boolean equals(Key key2){
			return key2.toString().equals(this.toString());
		}
		public String key = null;
	}
	
	class Value implements Serializable{
		public Value(String value){
			this.value = value;
		}
		public String toString(){
			return value;
		}
		public boolean equals(Value value2){
			return value2.toString().equals(this.toString());
		}
		public String value = null;
	}
	
