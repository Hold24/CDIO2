package ap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final int PORT = 8000;
		final String address = "localhost";
		BufferedReader inFromServer = null;
		DataOutputStream outToServer = null;
		String outLine = "";
		String inLine = "";
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			Socket socket = new Socket(address, PORT);
			
			if (socket.isConnected())
				System.out.println("Connected");
			outToServer = new DataOutputStream(socket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outLine = inFromUser.readLine();
			
			outToServer.writeBytes(outLine);
			

			
			
			socket.close();			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void readFromServer() {
//		System.out.println();
	}

}
