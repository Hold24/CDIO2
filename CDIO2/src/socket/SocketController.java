package socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.SocketHandler;

import socket.SocketInMessage.SocketMessageType;

public class SocketController implements ISocketController {
	Set<ISocketObserver> observers = new HashSet<ISocketObserver>();
	//TODO Maybe add some way to keep track of multiple connections?
	private BufferedReader inStream;
	private DataOutputStream outStream;
	Scanner keyb = new Scanner(System.in);

	@Override
	public void registerObserver(ISocketObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unRegisterObserver(ISocketObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void sendMessage(SocketOutMessage message) {
		if (outStream!=null){
			//			//TODO send something over the socket! 
			//
			try {
				outStream.writeBytes(message.getMessage()); //Vi har tidligere brugt writeChars
			} catch (IOException e) {

				e.printStackTrace();
			}


		} else {
			//TODO maybe tell someone that connection is closed?

		}
	}

	@Override
	public void run() {
		//TODO some logic for listening to a socket //(Using try with resources for auto-close of socket)
		notifyObservers(new SocketInMessage(SocketMessageType.E, "Do you want to change the Port nr.? Default Port nr. is 8000."));
		createSocketTest();
	}

	private void createSocketTest() {
		
		String ans;
		boolean ans2 = true;
		do {
			ans = keyb.nextLine().toLowerCase();
			if(!ans.equals("yes") && !ans.equals("no")) notifyObservers(new SocketInMessage(SocketMessageType.E, "Invalid input. Do you want to change the Port nr.? yes/no?"));
			else if(ans.equals("yes") || ans.equals("no")) break;
		} while(true);
		if(ans.equals("yes")){
			ans2 = true;
			notifyObservers(new SocketInMessage(SocketMessageType.E, "Choose a Port nr. between 1024-49151."));
			do {
				ans = keyb.nextLine();
				if (Integer.parseInt(ans) < 1024 || Integer.parseInt(ans) > 49151) notifyObservers(new SocketInMessage(SocketMessageType.E, "Invalid port number. Try Again."));;
			} while (Integer.parseInt(ans) < 1024 || Integer.parseInt(ans) > 49151);
			notifyObservers(new SocketInMessage(SocketMessageType.E, "You chose the Port nr: " + Integer.parseInt(ans) + "\n\rPort nr. has been recieved. Creating Socket..."));
		}
		else if(ans.equals("no"))
			notifyObservers(new SocketInMessage(SocketMessageType.E, "You chose the default Port nr: " + Port + "\n\rPort nr. has been recieved. Creating Socket..."));
			ans2 = false;
		keyb.close();
		try (ServerSocket listeningSocket = new ServerSocket(ans2 ? Integer.parseInt(ans) : Port)){ 
			while (true){
				waitForConnections(listeningSocket); 	
			}		
		} catch (IOException e1) {
			notifyObservers(new SocketInMessage(SocketMessageType.E, "Couldnt connect socket... recommending to restart program..."));
			e1.printStackTrace();
		}
	}

	/**
	 * @author Niklas Thielemann
	 * @desc This method is used to manipulate the Port nr.
	 */
	private void createSocket() {
		Scanner keyb = new Scanner(System.in);
		do {
			String ans = keyb.nextLine().toLowerCase();
			if (ans.equals("yes")) {
				notifyObservers(new SocketInMessage(SocketMessageType.E, "Choose a Port nr. between 1024-49151."));
				String str;
				int a = 0;
				do {
					str = keyb.nextLine();
					try{
						a = Integer.parseInt(str);
					} catch (NumberFormatException ne) {
					}
					if (a < 1024 || a > 49151)
						notifyObservers(new SocketInMessage(SocketMessageType.E, "Invalid Port nr. Try again. (Port nr. minimum 1024, and maximum 49151)"));
				} while (a < 1024 || a > 49151);
				notifyObservers(new SocketInMessage(SocketMessageType.E, "You chose the Port nr: " + a + "\n\rPort nr. has been recieved. Creating Socket..."));
				try (ServerSocket listeningSocket = new ServerSocket(a)){ 
					while (true){
						waitForConnections(listeningSocket); 	
					}		
				} catch (IOException e1) {
					notifyObservers(new SocketInMessage(SocketMessageType.E, "Couldnt connect socket... recommending to restart program..."));
					e1.printStackTrace();
				} 
				break;
			}
			else if (ans.equals("no")) {
				try (ServerSocket listeningSocket = new ServerSocket(Port)){ 
					while (true){
						waitForConnections(listeningSocket); 	
					}		
				} catch (IOException e1) {
					notifyObservers(new SocketInMessage(SocketMessageType.E, "Couldnt connect socket... recommending to restart program..."));
					e1.printStackTrace();
				}
				break;
			}
			else
				notifyObservers(new SocketInMessage(SocketMessageType.E, "Invalid input. Do you want to change the Port nr.? yes/no?"));
		} while(true);
		keyb.close();
	}

	/**
	 * @author Niklas Broch Thielemann
	 * @desc used to create Socket based on Port nr. The parameter a, is the port number used for the socket.
	 * @param a
	 */
	private void createSocket(int a) {
		try (ServerSocket listeningSocket = new ServerSocket(a)){ 
			while (true){
				waitForConnections(listeningSocket); 	
			}		
		} catch (IOException e1) {
			// TODO Maybe notify MainController?
			notifyObservers(new SocketInMessage(SocketMessageType.E, "Couldnt connect socket... recommending to restart program..."));
			e1.printStackTrace();
		}
	}

	private void waitForConnections(ServerSocket listeningSocket) {
		try {
			Socket activeSocket = listeningSocket.accept(); //Blocking call
			inStream = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));
			outStream = new DataOutputStream(activeSocket.getOutputStream());
			String inLine;
			//.readLine is a blocking call 
			//TODO How do you handle simultaneous input and output on socket?

			//TODO this only allows for one open connection - how would you handle multiple connections?

			while (true){
				inLine = inStream.readLine();
				//System.out.println(inLine);
				if (inLine != null && inLine.length() < 1) break;
				if (inLine==null) break;
				switch (inLine.split(" ")[0]) {
				case "RM208": // Display a message in the secondary display and wait for response

					//						notifyObservers(new SocketInMessage(SocketMessageType.RM208, inLine.split("RM208 ") [1]));
					notifyObservers(new SocketInMessage(SocketMessageType.RM208, "RM208..."));
					//outStream.writeChars("Invalid input. Try again.");
					break;
				case "D":// Display a message in the primary display
					//TODO Refactor to make sure that faulty messages doesn't break the system
					notifyObservers(new SocketInMessage(SocketMessageType.D, inLine.split("D ")[1])); 			
					break;
				case "DW": //Clear primary display
					String regex = "([\\W])*\\w";
					notifyObservers(new SocketInMessage(SocketMessageType.DW, inLine.replaceAll(regex, "")));
					break;
				case "P111": //Show something in secondary display
					notifyObservers(new SocketInMessage(SocketMessageType.P111, inLine.split("P111 ")[1]));
					break;
				case "T": // Tare the weight
					notifyObservers(new SocketInMessage(SocketMessageType.T, "Taring weight..."));
					break;
				case "S": // Request the current load
					notifyObservers(new SocketInMessage(SocketMessageType.S, "Sending data..."));
					break;
				case "K":
					if (inLine.split(" ").length>1){
						notifyObservers(new SocketInMessage(SocketMessageType.K, inLine.split(" ")[1]));
					}
					break;
				case "B": // Set the load
					//TODO implementS
					notifyObservers(new SocketInMessage(SocketMessageType.B, inLine.split(" ")[1])); 
					break;
				case "Q": // Quit
					notifyObservers(new SocketInMessage(SocketMessageType.Q, "Closing..."));
					break;
				default: 
					notifyObservers(new SocketInMessage(SocketMessageType.E, "Wrong command..."));
					break;
				}
			}
		} catch (IOException e) {
			notifyObservers(new SocketInMessage(SocketMessageType.E, "Error when waiting for connection..."));
			e.printStackTrace();
		}
	}

	private void notifyObservers(SocketInMessage message) {
		for (ISocketObserver socketObserver : observers) {
			socketObserver.notify(message);
		}
	}

	public static int getPort() {
		return Port;
	}

}

