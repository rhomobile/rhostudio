package rhogenwizard.debugger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class DebugServer extends Thread {

    private int debugServerPort = 9000;
    IDebugCallback debugCallback;
    DebugProtocol debugProtocol = null;

    private ServerSocket serverSocket = null;
    private DebugListener listener = null;
    private OutputStreamWriter outToClient = null;

    public DebugServer(IDebugCallback callback)
    {
    	this.debugCallback = callback;
    	this.initialize();
    }
    
    public DebugServer(IDebugCallback callback, int port)
    {
    	this.debugServerPort = port;
    	this.debugCallback = callback;
    	this.initialize();
    }
    
    public int getPort() {
    	return this.debugServerPort;
    }

    private void initialize()
    {
        try
        {
            this.serverSocket = new java.net.ServerSocket(this.debugServerPort);
            assert this.serverSocket.isBound();
            if (this.serverSocket.isBound())
            {
                System.out.println("Debug server port " + this.serverSocket.getLocalPort() +
                	" is ready and waiting for Rhodes application to connect...");
            }
        }
        catch (SocketException se)
        {
            System.err.println("Unable to create socket.");
            System.err.println(se.toString());
            // System.exit(1);
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to read data from an open socket.");
            System.err.println(ioe.toString());
            // System.exit(1);
        }
    }

    public void run()
    {
        try
        {
        	Socket clientSocket = serverSocket.accept();
        	outToClient = new OutputStreamWriter(new BufferedOutputStream(clientSocket.getOutputStream()), "US-ASCII");
        	debugProtocol = new DebugProtocol(this, this.debugCallback);
        	listener = new DebugListener(clientSocket, debugProtocol);
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to accept a client connection thru socket.");
            System.err.println(ioe.toString());
            // System.exit(1);
        }
    }

    public void shutdown() {
    	if (this.listener!=null) {
    		DebugListener listenerToInterrupt = this.listener;
    		this.listener = null;
    		listenerToInterrupt.interrupt();
    	}
        try
        {
        	if (this.outToClient!=null) this.outToClient.close();
            if (this.serverSocket!=null) this.serverSocket.close();
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to close an open socket.");
            System.err.println(ioe.toString());
            // System.exit(1);
        }
    	this.outToClient = null;
    	this.serverSocket = null;
        System.out.println("Debug server stopped.");
    }

    protected void send(String cmd) {
    	try {
    		if (!cmd.endsWith("\n")) cmd += "\n"; 
    		outToClient.write(cmd);
    		outToClient.flush();
    	}
        catch (IOException ioe)
        {
            System.err.println("Unable to send data to an open socket.");
            System.err.println(ioe.toString());
            // System.exit(1);
        }
    }

    public DebugProtocol getDebugProtocol() {
    	return debugProtocol;
    }
}
