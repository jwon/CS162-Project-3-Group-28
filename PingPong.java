import java.net.*;
import java.lang.*;
import java.io.*;

public class PingPong {

	
	private ServerSocket s;
	private int port = 8081;
	
	public PingPong(){
		try {
			s = new ServerSocket(port);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleConnection(){
		while(true){
			try{
				Socket socket = s.accept();
				new ConnectionHandler(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args){
		PingPong test = new PingPong();
		test.handleConnection();
	}

	public class ConnectionHandler implements Runnable {
		
		private Socket s1;
		
		public ConnectionHandler(Socket socket){
			this.s1 = socket;
			Thread t = new Thread(this);
			t.start();
		}
		
		public void run(){
			try{
				PrintWriter writer = new PrintWriter(s1.getOutputStream());
				String pong = "pong"; 
				writer.println(pong);
				writer.close();
				System.out.println(pong);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
