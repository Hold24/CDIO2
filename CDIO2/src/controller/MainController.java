package controller;

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
					weight = Double.parseDouble(message.getMessage());
					socketHandler.sendMessage(new SocketOutMessage("Input: " + weight + ", has been accepted."));
				}
				else
					System.out.println("Invalid input. Try again.");
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
				socketHandler.sendMessage(new SocketOutMessage("Type in the weight: "));
				weight = 
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
				System.out.println("Weight has been tared");
				weightController.showMessagePrimaryDisplay("0.0000 kg");
				weight = 0.0;
			}
			else
				System.out.println("ES");
			break;
		case DW:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {
				weightController.showMessagePrimaryDisplay("0.0000 kg");
				weightController.showMessageSecondaryDisplay("");
				weight = 0.0;
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
					System.out.println("Invalid input. Try again.");
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
			
			break;
		case TARA:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3) ){
				socketHandler.sendMessage(new SocketOutMessage("Button inactive\n\r"));
			}
			else if (keyState.equals(KeyState.K1) || keyState.equals(KeyState.K2)) {

			}
			break;
		case TEXT:
			
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
			break;
		case EXIT:
			System.exit(0);
			break;
		case SEND:
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3)) {	
				if (weight > 0) {
					socketHandler.sendMessage(new SocketOutMessage(weight.toString() + "\n\r"));
					weightController.showMessagePrimaryDisplay("0.0000 kg");
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

	}
	boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
