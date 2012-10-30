package com.vsttrfk.git64.cards;

public interface RKFCard {

	public byte[][] getData();
	public byte[] getId();
	public byte[][] getKeysA();
	public byte[][] getKeysB();
	public double getBalance();
	public double getOldBalance();
	public boolean anonymousExploit();
	public String getProvider();
}
