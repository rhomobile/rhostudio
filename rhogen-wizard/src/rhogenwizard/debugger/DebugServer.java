package rhogenwizard.debugger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class DebugServer extends Thread {
	static private PrintStream debugOutput = null;
	
	private int debugServerPort = 9000;
	IDebugCallback debugCallback;
	DebugProtocol debugProtocol = null;

	private ServerSocket serverSocket = null;
	private BufferedReader inFromClient = null;
	private OutputStreamWriter outToClient = null;

	public DebugServer(IDebugCallback callback) {
		this.debugCallback = callback;
		this.initialize();
	}

	public DebugServer(IDebugCallback callback, int port) {
		this.debugServerPort = port;
		this.debugCallback = callback;
		this.initialize();
	}

	public static void setDebugOutputStream(PrintStream stream) {
		debugOutput = stream;
	}
	
	public int getPort() {
		return this.debugServerPort;
	}

	private void initialize() {
		try {
			this.serverSocket = new java.net.ServerSocket(this.debugServerPort);
			assert this.serverSocket.isBound();
			if ((debugOutput != null) && this.serverSocket.isBound()) {
				debugOutput.println("Debug server port "
					+ this.serverSocket.getLocalPort()
					+ " is ready and waiting for Rhodes application to connect...");
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {
		try {
			Socket clientSocket = serverSocket.accept();
			inFromClient = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
			outToClient = new OutputStreamWriter(new BufferedOutputStream(
				clientSocket.getOutputStream()), "US-ASCII");
			debugProtocol = new DebugProtocol(this, this.debugCallback);
			try {
				String data;
				while ((data = inFromClient.readLine()) != null) {
					if (debugOutput != null)
						debugOutput.println("Received: " + data);
					debugProtocol.processCommand(data);
				}
			} catch (EOFException e) {
			} catch (IOException e) {
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
				}
			}
		} catch (IOException ioe) {
		}
	}

	public void shutdown() {
		this.interrupt();
		try {
			if (this.inFromClient != null)
				this.inFromClient.close();
			if (this.outToClient != null)
				this.outToClient.close();
			if (this.serverSocket != null)
				this.serverSocket.close();
		} catch (IOException ioe) {
		}
		this.inFromClient = null;
		this.outToClient = null;
		this.serverSocket = null;
		if (debugOutput != null)
			debugOutput.println("Debug server stopped.");
	}

	protected void send(String cmd) {
		try {
			if (!cmd.endsWith("\n"))
				cmd += "\n";
			outToClient.write(cmd);
			outToClient.flush();
		} catch (IOException ioe) {
		}
	}

	public DebugState debugGetState() {
		return this.debugProtocol.getState();
	}

	public void debugStep() {
		this.debugProtocol.step();
	}
	
	public void debugResume() {
		this.debugProtocol.resume();
	}
	
	public void debugBreakpoint(String file, int line) {
		this.debugProtocol.setBreakpoint(file, line);
	}

	public void debugEvaluate(String expression) {
		this.debugProtocol.evaluate(expression);
	}

}
