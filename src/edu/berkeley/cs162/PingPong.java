import java.net.*;
import java.io.*;

public class PingPong {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket s = new ServerSocket(8081, 20);
		Socket s1 = s.accept();
		OutputStream s1out = s1.getOutputStream();
		DataOutputStream dos = new DataOutputStream(s1out);
		dos.writeUTF("pong");
		dos.close();
		s1out.close();
		s1.close();

	}

}
