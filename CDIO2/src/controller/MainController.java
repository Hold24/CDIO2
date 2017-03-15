package controller;

import java.util.regex.Pattern;

import socket.ISocketController;
import socket.ISocketObserver;
import socket.SocketInMessage;
import socket.SocketOutMessage;
import weight.IWeightInterfaceController;
import weight.IWeightInterfaceObserver;
import weight.KeyPress;
/**
 * MainController - integrating input from socket and ui. Implements ISocketObserver and IUIObserver to handle this.
 * @author Christian Budtz
 * @version 0.1 2017-01-24
 *
 */
public class MainController implements IMainController, ISocketObserver, IWeightInterfaceObserver {

	private ISocketController socketHandler;
	private IWeightInterfaceController weightController;
	private KeyState keyState = KeyState.K1;
	private Double weight = 0.0;
	private String currentDisplay = "";
	private boolean sent = false;
	//	private String regex = "([0-9]+[,]?[0-9]+)"; 
	//"([0-9]+[,]?[0-9]+)";


	public MainController(ISocketController socketHandler, IWeightInterfaceController weightInterfaceController) {
		this.init(socketHandler, weightInterfaceController);
	}

	@Override
	public void init(ISocketController socketHandler, IWeightInterfaceController weightInterfaceController) {
		this.socketHandler = socketHandler;
		this.weightController=weightInterfaceController;
	}

	@Override
	public void start() {
		if (socketHandler!=null && weightController!=null){
			//Makes this controller interested in messages from the socket
			socketHandler.registerObserver(this);
			//Starts socketHandler in own thread
			new Thread(socketHandler).start();
			//TODO set up weightController - Look above for inspiration (Keep it simple ;))
			weightController.registerObserver(this);
			new Thread(weightController).start();


		} 
		else {
			System.err.println("No controllers injected!");
		}
	}

	//Listening for socket input
	@Override
	public void notify(SocketInMessage message) {
		switch (message.getType()) {
		case B:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {

			}
			else
				System.out.println("ES");
			break;
		case D:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				if (isDouble(message.getMessage())){
					weightController.showMessagePrimaryDisplay(message.getMessage() + "kg");
					weightController.showMessageSecondaryDisplay(message.getMessage() + "kg");
					//weight = Double.parseDouble(message.getMessage());
					this.notifyWeightChange(Double.parseDouble(message.getMessage()));
					socketHandler.sendMessage(new SocketOutMessage("Input: " + weight + ", has been accepted. \n\r"));
				}
				else
					System.out.println("Invalid input. Try again. \n\r");
			}
			else
				System.out.println("ES");
			break;
		case Q:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				System.exit(0);
			}
			else
				System.out.println("ES");
			break;
		case RM204:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {

			}
			else
				System.out.println("ES");
			break;
		case RM208:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				weightController.showMessageSecondaryDisplay("Type in the weight \n\r");
				sent = false;
				socketHandler.sendMessage(new SocketOutMessage("The weight you typed was: " + weight + " and it has been recieved.\n\r"));
			}
			else
				System.out.println("ES");
			break;
		case S:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {	
				if (weight > 0)
					socketHandler.sendMessage(new SocketOutMessage(weight.toString() + "\n\r"));
				else
					socketHandler.sendMessage(new SocketOutMessage("Weight is not greater than 0 kg. \n\r"));
			}
			else
				System.out.println("ES");
			break;
		case T:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				socketHandler.sendMessage(new SocketOutMessage("Weight has been tared \n\r"));
				weightController.showMessagePrimaryDisplay("0.0000 kg");
				//weight = 0.0;
				this.notifyWeightChange(0.0);
			}
			else
				System.out.println("ES");
			break;
		case DW:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				weightController.showMessagePrimaryDisplay("0.0000 kg");
				weightController.showMessageSecondaryDisplay("");
				currentDisplay = "";
				//weight = 0.0;
				this.notifyWeightChange(0.0);
			}
			else
				System.out.println("ES");
			break;
		case K:
			handleKMessage(message);
			break;
		case P111:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				if (isDouble(message.getMessage())){
					weightController.showMessageSecondaryDisplay(message.getMessage() + " kg");
				}
				else
					socketHandler.sendMessage(new SocketOutMessage("Invalid input. Try again. \n\r"));
			}
			else
				System.out.println("ES");
			break;
		default:
			System.out.println("Wrong Command");
			break;
		}
	}

	private void handleKMessage(SocketInMessage message) {
		switch (message.getMessage()) {
		case "1" :
			this.keyState = KeyState.K1;
			break;
		case "2" :
			this.keyState = KeyState.K2;
			break;
		case "3" :
			this.keyState = KeyState.K3;
			break;
		case "4" :
			this.keyState = KeyState.K4;
			break;
		default:
			socketHandler.sendMessage(new SocketOutMessage("ES \n\r"));
			break;
		}
	}
	//Listening for UI input
	@Override
	public void notifyKeyPress(KeyPress keyPress) {

		//TODO implement logic for handling input from ui
		//		System.out.println(keyPress.getCharacter() + " +- " + keyPress.getKeyNumber());
		switch (keyPress.getType()) {
		case SOFTBUTTON:
			//			System.out.println(keyPress.getKeyNumber() + " - Number//Character - " + keyPress.getCharacter());

			break;
		case TARA:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3) ){
				socketHandler.sendMessage(new SocketOutMessage("Button inactive\n\r"));
			}
			else if (keyState.equals(KeyState.K1) || keyState.equals(KeyState.K2)) {

			}
			break;
		case TEXT:
			int c = keyPress.getCharacter();
			if (c >= 48 && c <= 57) {
				if (currentDisplay == "") 
					currentDisplay = String.valueOf(c-48);
				else if (Pattern.matches("[a-zA-Z.?]+", currentDisplay))
					currentDisplay = String.valueOf(c-48);
				else
					currentDisplay += String.valueOf(c-48);
				//weight = Double.parseDouble(currentDisplay);
				this.notifyWeightChange(Double.parseDouble(currentDisplay));
				weightController.showMessagePrimaryDisplay(currentDisplay);
			}
			else if (c > 57 || c == 46) {
				if (Pattern.matches("[0-9.]+", currentDisplay) && c != 46) 
					currentDisplay = Character.toString((char) c); 
				else 
					currentDisplay += Character.toString((char) c);
				weightController.showMessagePrimaryDisplay(currentDisplay);
				//weight = 0.0;
				this.notifyWeightChange(0.0);
				
			}
			else
				System.out.println("Not a number.");
			break;
		case ZERO:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3) ){
				socketHandler.sendMessage(new SocketOutMessage("Button inactive \n\r"));
			}
			else if (keyState.equals(KeyState.K1) || keyState.equals(KeyState.K2)) {

			}
			break;
		case C:
			weightController.showMessagePrimaryDisplay("0.0000 kg");
			weightController.showMessageSecondaryDisplay("");
			currentDisplay = "";
			break;
		case EXIT:
			socketHandler.sendMessage(new SocketOutMessage("Terminating weight..."));
			System.exit(0);
			break;
		case SEND:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {	
				if (weight > 0) {
					socketHandler.sendMessage(new SocketOutMessage("The weight has been recived...\n\r" + weight.toString() + " Kg" + "\n\r"));
					weightController.showMessagePrimaryDisplay("0.0000 kg");
					currentDisplay = "";
					sent = true;
				}
				else
					socketHandler.sendMessage(new SocketOutMessage("Weight is not greater than 0 kg.\n\r"));
			}
			else
				System.out.println("ES");
			break;

		}

	}

	@Override
	public void notifyWeightChange(double newWeight) {
		// TODO Auto-generated method stub
		weight = newWeight;
	}
	/**
	 * @author Sammy Masoule
	 * 
	 * @desc converts a string to a double and returns true or false whether the string has been converted or not.
	 * 
	 */
		public boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
