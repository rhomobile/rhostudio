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

/**
 * Rhodes Debug Server implementation.
 * @author Albert R. Timashev
 */
public class DebugServer extends Thread {
	static private PrintStream debugOutput = null;
	private static int debugServerPort0 = 9000;
	private static int debugServerPort1 = 9999;
	
	private int debugServerPort = 0;
	private IDebugCallback debugCallback;
	private DebugProtocol debugProtocol = null;

	private ServerSocket serverSocket = null;
	private BufferedReader inFromClient = null;
	private OutputStreamWriter outToClient = null;

	/**
	 * Create a debug server.
	 * @param callback - object to receive events from the debug target (Rhodes application).
	 */
	public DebugServer(IDebugCallback callback) {
		this.debugCallback = callback;
		this.initialize();
	}

	/**
	 * Set port range to bind Debug Server to (default 9000-9999).
	 * @param port0 - starting port number.
	 * @param port1 - ending port number.
	 */
	public static void setDebugPortRange(int port0, int port1) {
		debugServerPort0 = port0;
		debugServerPort1 = port1;
	}

	/**
	 * Set an output stream for a detailed debug information.
	 * @param stream - output stream (if null, no debug information will be passed anywhere). 
	 */
	public static void setDebugOutputStream(PrintStream stream) {
		debugOutput = stream;
	}
	
	/**
	 * Get the debug server port.
	 * @return Port number the debug server is bound/listening to. 
	 */
	public int getPort() {
		return this.debugServerPort;
	}

	private void initialize() {
		try {
			// find & bind free local port
			this.serverSocket = null;
			this.debugServerPort = 0;
			for (int i=debugServerPort0; i<=debugServerPort1; i++) {
				try {
					ServerSocket s = new ServerSocket(i);
					this.serverSocket = s;
					break;
				} catch( IOException ioe ) { }
			}
			if (this.serverSocket == null) {
				throw new IOException(String.format("Unable to open server in port range (%d..%d)",
					debugServerPort0,debugServerPort1));
			}
			assert this.serverSocket.isBound();
			if (this.serverSocket.isBound()) {
				this.debugServerPort = this.serverSocket.getLocalPort();
				if ((debugOutput != null)) {
					debugOutput.println("Debug server port " + this.debugServerPort +
						" is ready and waiting for Rhodes application to connect...");
				}
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

	/**
	 * Shutdown the debug server (close all connections and a server socket).
	 */
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

	/**
	 * Get current state of the connected Rhodes application.
	 * @return Returns a {@link DebugState}.
	 */
	public DebugState debugGetState() {
		return this.debugProtocol!=null ? this.debugProtocol.getState() : DebugState.NOTCONNECTED;
	}

	/**
	 * Step over the next method call (without entering it) at the currently executing line of Ruby code.
	 */
	public void debugStepOver() {
		if (this.debugProtocol!=null)
			this.debugProtocol.stepOver();
	}
	
	/**
	 * Step into the next method call at the currently executing line of Ruby code.
	 */
	public void debugStepInto() {
		if (this.debugProtocol!=null)
			this.debugProtocol.stepInto();
	}
	
	/**
	 * Resume a normal execution of the Rhodes application (after the stop at breakpoint or after {@link #debugStep()} method call). 
	 */
	public void debugResume() {
		if (this.debugProtocol!=null)
			this.debugProtocol.resume();
	}
	
	/**
	 * Add a breakpoint.
	 * @param file - file path within <code>app</code> folder of the Rhodes application,
	 * e.g. <code>"application.rb"</code>
	 * (always use <code>'/'</code> as a folder/file name separator).
	 * @param line - effective line number (starting with 1). Must point to non-empty line of code.
	 */
	public void debugBreakpoint(String file, int line) {
		if (this.debugProtocol!=null)
			this.debugProtocol.addBreakpoint(file, line);
	}

	/**
	 * Remove a breakpoint.
	 * @param file - file path within <code>app</code> folder of the Rhodes application,
	 * e.g. <code>"application.rb"</code>
	 * (always use <code>'/'</code> as a folder/file name separator).
	 * @param line - effective line number (starting with 1). Must point to non-empty line of code.
	 */
	public void debugRemoveBreakpoint(String file, int line) {
		if (this.debugProtocol!=null)
			this.debugProtocol.removeBreakpoint(file, line);
	}

	/**
	 * Remove all breakpoints.
	 */
	public void debugRemoveAllBreakpoints() {
		if (this.debugProtocol!=null)
			this.debugProtocol.removeAllBreakpoints();
	}

	/**
	 * Toggle breakpoints skip mode.
	 * @param skip - if <code>true</code>, skip all breakpoints; if <code>false</code>, stop at breakpoints.
	 */
	public void debugSkipBreakpoints(boolean skip) {
		if (this.debugProtocol!=null)
			this.debugProtocol.skipBreakpoints(skip);
	}

	/**
	 * Evaluate Ruby expression or execute arbitrary Ruby code. 
	 * @param expression - expression to evaluate or Ruby code to execute.
	 * Result of evaluation/execution is returned by the
	 * {@link IDebugCallback#evaluation(String)} method call.  
	 */
	public void debugEvaluate(String expression) {
		if (this.debugProtocol!=null)
			this.debugProtocol.evaluate(expression);
	}

}
