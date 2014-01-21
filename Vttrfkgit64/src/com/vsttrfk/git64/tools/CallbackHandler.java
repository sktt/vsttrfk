package com.vsttrfk.git64.tools;

public interface CallbackHandler<T> {

	public void updateStatus(String status);
	public void handleResult(T result);
	public void enableGUI(boolean val);
	
}
