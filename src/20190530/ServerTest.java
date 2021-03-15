import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetAddress;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;


public class ServerTest {

	public static void main(String[] args) {
		
		boolean flag = false;
		
		ServerProgram s = new ServerProgram();
		
		
		try {
			System.out.println("Server launched");
			ServerSocket server = new ServerSocket(10000);
			
		
		while(true){ 		
				Socket socket = server.accept();
				new Client(socket);
				Client.start();
			
				
				
			}
			
		} catch (Exception e) {
			System.err.println("Socket error " + e);
		}





	}

}
