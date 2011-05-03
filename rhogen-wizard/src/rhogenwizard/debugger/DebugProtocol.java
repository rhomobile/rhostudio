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
		boolean bp;
		if(cmd.endsWith("\n"))
			cmd = cmd.substring(0, cmd.length()-1);
		if (cmd.compareTo("CONNECT")==0) {
			this.state = DebugState.CONNECTED;
			debugServer.send("CONNECTED");
			debugCallback.connected();
		} else if (cmd.compareTo("QUIT")==0) {
			this.state = DebugState.EXITED;
			debugCallback.exited();
		} else if ((bp=cmd.startsWith("BP:")) || cmd.startsWith("STEP:")) {
			this.state = bp ? DebugState.BREAKPOINT : DebugState.STEP;
			String[] brp = cmd.split(":");
			String file = brp[1].replace('|', ':').replace('\\', '/');
			int line = Integer.parseInt(brp[2]);
			if (bp)
				debugCallback.breakpoint(file, line);
			else
				debugCallback.step(file, line);
		} else if (cmd.startsWith("EV:")) {
			debugCallback.evaluation(cmd.substring(3).replace("\\n", "\n"));
		} else if (cmd.startsWith("AW:")) {
			String var = cmd.substring(3);
			String val = "";
			int val_idx = var.indexOf(':');
			if (val_idx>=0) {
				val = var.substring(val_idx+1).replace("\\n", "\n");
				var = var.substring(0,val_idx);
			}
			debugCallback.autoWatch(var, val);
		} else {
			debugCallback.unknown(cmd);
		}
	}
	
	public void stepOver() {
		this.state = DebugState.RUNNING;
		debugServer.send("STEPOVER");
	}

	public void stepInto() {
		this.state = DebugState.RUNNING;
		debugServer.send("STEPINTO");
	}
	
	public void resume() {
		this.state = DebugState.RUNNING;
		debugServer.send("CONT");
	}
	
	public void addBreakpoint(String file, int line) {
		debugServer.send("BP:"+file+":"+line);
	}

	public void removeBreakpoint(String file, int line) {
		debugServer.send("RM:"+file+":"+line);
	}

	public void removeAllBreakpoints() {
		debugServer.send("RMALL");
	}
	
	public void skipBreakpoints(boolean skip) {
		debugServer.send(skip?"DISABLE":"ENABLE");
	}
	
	public void evaluate(String expression) {
		debugServer.send("EV:"+expression);
	}
}
