package rhogenwizard.debugger;

public class DebugProtocol {
	private DebugServer debugServer;
	private IDebugCallback debugCallback;
	private DebugState state;
	private String filePosition = "";
	private int linePosition = 0;
	private String classPosition = "";
	private String methodPosition = "";
	
	public DebugProtocol (DebugServer server, IDebugCallback callback) {
		this.debugServer = server;
		this.debugCallback = callback;
		this.state = DebugState.NOTCONNECTED;
	}

	public DebugState getState() {
		return this.state;
	}

	public String getCurrentFile() {
		return this.filePosition;
	}

	public int getCurrentLine() {
		return this.linePosition;
	}
	
	public String getCurrentClass() {
		return this.classPosition;
	}

	public String getCurrentMethod() {
		return this.methodPosition;
	}
	
	protected void processCommand(String cmd) {
		boolean bp;
		if(cmd.endsWith("\n"))
			cmd = cmd.substring(0, cmd.length()-1);
		if (cmd.compareTo("CONNECT")==0) {
			this.state = DebugState.CONNECTED;
			debugServer.send("CONNECTED");
			debugCallback.connected();
		} else if (cmd.compareTo("RESUMED")==0) {
			this.state = DebugState.RUNNING;
			debugCallback.resumed();
		} else if (cmd.compareTo("QUIT")==0) {
			this.state = DebugState.EXITED;
			debugCallback.exited();
		} else if ((bp=cmd.startsWith("BP:")) || cmd.startsWith("STEP:")) {
			this.state = bp ? DebugState.BREAKPOINT : DebugState.STEP;
			String[] brp = cmd.split(":");
			this.filePosition = brp[1].replace('|', ':').replace('\\', '/');
			this.linePosition = Integer.parseInt(brp[2]);
			this.classPosition = brp[3].replace('#', ':');
			this.methodPosition = brp[4];
			if (bp)
				debugCallback.breakpoint(this.filePosition, this.linePosition, this.classPosition, this.methodPosition);
			else
				debugCallback.step(this.filePosition, this.linePosition, this.classPosition, this.methodPosition);
		} else if (cmd.startsWith("EV:")) {
			debugCallback.evaluation(cmd.substring(3).replace("\\n", "\n"));
		} else if (cmd.startsWith("EVL:")) {
			String var = cmd.substring(4);
			String val = "";
			int val_idx = var.indexOf(':');
			if (val_idx>=0) {
				val = var.substring(val_idx+1).replace("\\n", "\n");
				var = var.substring(0,val_idx).replace("\\n", "\n").replace("#", ":");
			}
			debugCallback.evaluation(var, val);
		} else if (cmd.startsWith("V:")) {
			DebugVariableType vt;
			switch (cmd.charAt(2)) {
			case 'G': vt = DebugVariableType.GLOBAL; break;
			case 'C': vt = DebugVariableType.CLASS; break;
			case 'I': vt = DebugVariableType.INSTANCE; break;
			default: vt = DebugVariableType.LOCAL;
			}
			String var = cmd.substring(4);
			String val = "";
			int val_idx = var.indexOf(':');
			if (val_idx>=0) {
				val = var.substring(val_idx+1).replace("\\n", "\n");
				var = var.substring(0,val_idx);
			}
			debugCallback.watch(vt, var, val);
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

	public void stepReturn() {
		this.state = DebugState.RUNNING;
		debugServer.send("STEPRET");
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
		evaluate(expression, false);
	}

	public void evaluate(String expression, boolean includeName) {
		debugServer.send((includeName?"EVL:":"EV:")+expression);
	}
	
	public void getVariables(DebugVariableType[] types) {
		for (DebugVariableType t: types) {
			switch (t) {
			case GLOBAL:
				debugServer.send("GVARS"); break;
			case CLASS:
				debugServer.send("CVARS"); break;
			case INSTANCE:
				debugServer.send("IVARS"); break;
			default:
				debugServer.send("LVARS");
			}
		}
	}

	public void suspend() {
		debugServer.send("SUSP");
	}

	public void terminate() {
		debugServer.send("KILL");
	}
}
