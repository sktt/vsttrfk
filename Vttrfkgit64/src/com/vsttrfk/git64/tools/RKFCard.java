package com.vsttrfk.git64.tools;

public interface RKFCard {

	public byte[][] getData();
	public void setData(byte[][] data);
	public byte[] getId();
	public byte[][] getKeysA();
	public byte[][] getKeysB();
	public double getBalance();
	public double getOldBalance();
	public boolean anonymousExploit();
	public String getProvider();
}
