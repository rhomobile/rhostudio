package rhogenwizard.debugger;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DebugListener extends Thread {
	private BufferedReader inFromClient;
	private Socket clientSocket;
	private DebugProtocol protocol;

	public DebugListener(Socket aClientSocket, DebugProtocol protocol) {
		try {
			this.clientSocket = aClientSocket;
			this.protocol = protocol;
			this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.start();
		} catch (IOException e) {
			System.out.println("Debug listener: " + e.getMessage());
		}
	}

	public void run() {
		try {
			String data;
			while ((data = inFromClient.readLine()) != null) { // read a line of data from the stream
	            System.out.println("Received: " + data);
	            this.protocol.processCommand(data);
			}
		} catch (EOFException e) {
			//System.out.println("EOF: " + e.getMessage());
		} catch (IOException e) {
			//System.out.println("readLine: " + e.getMessage());
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {/* close failed */
			}
		}
	}

}
