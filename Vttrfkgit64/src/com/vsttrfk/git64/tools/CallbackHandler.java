package com.vsttrfk.git64.tools;

import com.vsttrfk.git64.cards.RKFCard;


public interface CallbackHandler {

	public void updateStatus(String status);
	public void readComplete(RKFCard card);
	public void enableGUI(boolean val);
	
}
