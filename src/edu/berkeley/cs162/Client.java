package edu.berkeley.cs162;

import java.io.IOException;

public class Client {

	static KVClient<String, String> client = null;
	
	public static void main(String[] args) throws IOException, KVException {
		String key = "FOS";
		String value = "ROH";
		
		System.out.println("Binding Client:");
		client = new KVClient<String, String>("107.21.152.247",8080);
		System.out.println("Starting Client");
		client.put(key, value);
		client.get(key);
		client.del(key);
	}
}
