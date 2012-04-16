package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.Serializable;
import org.junit.Test;

public class KVMessageTest2 {
    
    public static void runTest() {

	    Key key = new Key("key");
	    Value value = new Value("value");
	    KVMessage msg = new KVMessage("putreq", KVMessage.marshal(key), KVMessage.marshal(value));

	    System.out.println(msg.toXML());
    }

    public static void main(String args[]) {
	runTest();
    }
}
