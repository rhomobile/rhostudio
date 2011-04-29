package rhogenwizard.debugger;

public class DebugProtocol {
	private DebugServer debugServer;
	private IDebugCallback debugCallback;
	private DebugState state;
	
	public DebugProtocol (DebugServer server, IDebugCallback callback) {
		this.debugServer = server;
		this.debugCallback = callback;
		this.state = DebugState.NOTCONNECTED;
	}

	public DebugState getState() {
		return this.state;
	}
	
	protected void processCommand(String cmd) {
		if(cmd.endsWith("\n"))
			cmd = cmd.substring(0, cmd.length()-1);
		if (cmd.compareTo("CONNECT")==0) {
			this.state = DebugState.CONNECTED;
			debugServer.send("CONNECTED");
			debugCallback.connected();
		// not yet implemented in debugger.rb:
		//} else if (cmd.compareTo("QUIT")==0) {
		//	this.state = DebugState.EXITED;
		//	debugServer.stop();
		//	debugCallback.exited();
		} else if (cmd.startsWith("BP:")) {
			this.state = DebugState.STOPPED;
			String[] bp = cmd.split(":");
			debugCallback.breakpoint(bp[1], Integer.parseInt(bp[2]));
		} else if (cmd.startsWith("EV:")) {
			String[] bp = cmd.split(":");
			debugCallback.evaluation(bp[1].replace("\\n", "\n"));
		} else {
			debugCallback.unknown(cmd);
		}
	}
	
    public void step() {
		this.state = DebugState.RUNNING;
		debugServer.send("STEP");
    }
    
    public void resume() {
		this.state = DebugState.RUNNING;
		debugServer.send("CONT");
    }
    
    public void setBreakpoint(String file, int line) {
		debugServer.send("BP:"+file+":"+line);
    }

    public void evaluate(String expression) {
		debugServer.send("EV:"+expression);
    }
}
