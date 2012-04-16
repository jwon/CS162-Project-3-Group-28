package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.Serializable;
import org.junit.Test;

public class KVMessageTest2 {
    
    public static void runTest() {
	Key key = new Key("key");
	Value value = new Value("value");
	String type = "putreq";
	KVMessage msg = new KVMessage(type, KVMessage.marshal(key), KVMessage.marshal(value));
	System.out.println("type: " + msg.getMsgType());
	System.out.println("key: " + msg.getKey());
	System.out.println("value: " + msg.getValue());
	System.out.println("status: " + msg.getStatus());
	System.out.println("message: " + msg.getMessage());
	try{
		System.out.println("XML CONVERSION: ");
		System.out.println(msg.toXML());
    	} catch (KVException e){
		System.out.println("Fail");
	}
    }

    public static void main(String args[]) {
	runTest();
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
