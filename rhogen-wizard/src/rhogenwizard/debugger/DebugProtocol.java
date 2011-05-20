package rhogenwizard.debugger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;

public class DebugProtocol {
	private DebugServer debugServer;
	private IDebugCallback debugCallback;
	private DebugState state;
	private String filePosition = "";
	private int linePosition = 0;
	private String classPosition = "";
	private String methodPosition = "";
	private boolean watchProcessing = false;
	private DebugVariableType waitForEOL;
	private Thread waitingThread;
	private Vector<DebugVariable> watchList = null;
	private DebugVariableType lastWatchEOL;
	private boolean wasWatchEOL = false;
	private DebugEvaluation evaluationResult = null;
	
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
		boolean bp=false, stInto=false, stOver=false, stRet=false;
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
		} else if (
			(bp=cmd.startsWith("BP:")) ||
			(stInto=cmd.startsWith("STEP:")) ||
			(stOver=cmd.startsWith("STOVER:")) ||
			(stRet=cmd.startsWith("STRET:")) ||
			cmd.startsWith("SUSP:"))
		{
			this.state = bp ? DebugState.BREAKPOINT
					: (stInto ? DebugState.STOPPED_INTO
							: (stOver ? DebugState.STOPPED_OVER
									: (stRet ? DebugState.STOPPED_RETURN
											: DebugState.SUSPENDED)));
			String[] brp = cmd.split(":");
			this.filePosition = brp[1].replace('|', ':').replace('\\', '/');
			this.linePosition = Integer.parseInt(brp[2]);
			this.classPosition = brp.length > 3 ? brp[3].replace('#', ':') : "";
			this.methodPosition = brp.length > 4 ? brp[4] : "";
			debugCallback.stopped(this.state, this.filePosition, this.linePosition, this.classPosition, this.methodPosition);
		} else if (cmd.startsWith("EVL:")) {
			boolean valid = cmd.charAt(4)=='0';
			String var = cmd.substring(6);
			String val = "";
			int val_idx = var.indexOf(':');
			if (val_idx>=0) {
				val = var.substring(val_idx+1);
				try {
					var = URLDecoder.decode(var.substring(0,val_idx), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					var = var.substring(0,val_idx);
				}
			}
			if (this.watchProcessing)
				evaluationPrivate(valid, var, val);
			else
				debugCallback.evaluation(valid, var, val);
		} else if (cmd.startsWith("V:")) {
			DebugVariableType vt = DebugVariableType.variableTypeById(cmd.charAt(2));
			String var = cmd.substring(4);
			String val = "";
			int val_idx = var.indexOf(':');
			if (val_idx>=0) {
				val = var.substring(val_idx+1);
				var = var.substring(0,val_idx);
			}
			if (this.watchProcessing)
				watchPrivate(vt, var, val);
			else
				debugCallback.watch(vt, var, val);
		} else if (cmd.startsWith("VSTART:")) {
			DebugVariableType type = DebugVariableType.variableTypeById(cmd.charAt(7));
			if (this.watchProcessing)
				watchBOLPrivate(type);
			else
				debugCallback.watchBOL(type);
		} else if (cmd.startsWith("VEND:")) {
			DebugVariableType type = DebugVariableType.variableTypeById(cmd.charAt(5));
			if (this.watchProcessing)
				watchEOLPrivate(type);
			else
				debugCallback.watchEOL(type);
		} else {
			debugCallback.unknown(cmd);
		}
	}

	public void stepOver() throws DebugServerException {
		checkDebugState();
		this.state = DebugState.RESUMING;
		debugServer.send("STEPOVER");
	}

	public void stepInto() throws DebugServerException {
		checkDebugState();
		this.state = DebugState.RESUMING;
		debugServer.send("STEPINTO");
	}

	public void stepReturn() throws DebugServerException {
		checkDebugState();
		this.state = DebugState.RESUMING;
		debugServer.send("STEPRET");
	}
	
	public void resume() throws DebugServerException {
		checkDebugState();
		this.state = DebugState.RESUMING;
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
	
	public void evaluate(String expression) throws DebugServerException {
		checkDebugState();
		evaluatePrivate(expression);
	}

	private void evaluatePrivate(String expression) {
		try {
			expression = URLEncoder.encode(expression, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		debugServer.send("EVL:"+expression);
	}

	public void getVariables(DebugVariableType[] types) throws DebugServerException {
		checkDebugState();
		for (DebugVariableType t: types) {
			getVariablesPrivate(t);
		}
	}

	private void getVariablesPrivate(DebugVariableType type) {
		switch (type) {
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

	public Vector<DebugVariable> getWatchList() {
		if (DebugState.paused(this.state)) {
			this.waitForEOL = DebugVariableType.LOCAL;
			this.watchProcessing = true;
			this.watchList = new Vector<DebugVariable>();
			this.waitingThread = Thread.currentThread();
			this.wasWatchEOL = false;
			// by default get the global and local variables only
			getVariablesPrivate(DebugVariableType.GLOBAL);
			getVariablesPrivate(DebugVariableType.LOCAL);
			// if class is defined, get the class and instance variables as well
			if (this.classPosition.length() > 0) {
				this.waitForEOL = DebugVariableType.INSTANCE;
				getVariablesPrivate(DebugVariableType.CLASS);
				getVariablesPrivate(DebugVariableType.INSTANCE);
			}
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { }
			} while (!(this.wasWatchEOL && (this.lastWatchEOL==this.waitForEOL)));
			this.watchProcessing = false;
			return this.watchList;
		}
		return null;
	}
	
	public DebugEvaluation instantEvaluate(String expression) {
		if (DebugState.paused(this.state)) {
			this.watchProcessing = true;
			this.evaluationResult = null;
			this.waitingThread = Thread.currentThread();
			evaluatePrivate(expression);
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { }
			} while (this.evaluationResult==null);
			this.watchProcessing = false;
			return this.evaluationResult;
		}
		return null;
	}

	private void watchBOLPrivate(DebugVariableType type) { 
		// nothing to do 
	}

	private void watchEOLPrivate(DebugVariableType type) {
		this.wasWatchEOL = true;
		this.lastWatchEOL = type;
		if (this.waitForEOL==type)
			this.waitingThread.interrupt();
	}

	private void watchPrivate(DebugVariableType type, String variable, String value) {
		this.watchList.add(new DebugVariable(type, variable, value));
	}
	
	private void evaluationPrivate(boolean valid, String code, String value) {
		this.evaluationResult = new DebugEvaluation(valid, code, value);
		this.waitingThread.interrupt();
	}

	public void suspend() throws DebugServerException {
		checkDebugState();
		debugServer.send("SUSP");
	}

	public void terminate() throws DebugServerException {
		checkDebugState();
		debugServer.send("KILL");
	}
	
	private void checkDebugState() throws DebugServerException {
		if (this.watchProcessing)
			throw new DebugServerException("Can't interrupt the watch list processing");
	}

	public boolean isProcessing() {
		return this.watchProcessing;
	}
}
