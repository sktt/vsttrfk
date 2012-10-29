package com.vsttrfk.git64.tools;

public enum BlockId {
	PURSE((byte) 0x85);

	private byte id;

	private BlockId(byte id) {
		this.id = id;
	}
	public byte getId(){
		return id;
	}
};
