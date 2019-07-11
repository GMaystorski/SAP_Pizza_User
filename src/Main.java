import java.io.IOException;
import java.net.Socket;

public class Main {
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("192.168.0.103",2000);
			new User(socket);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
