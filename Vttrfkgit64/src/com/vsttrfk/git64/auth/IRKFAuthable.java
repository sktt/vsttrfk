package com.vsttrfk.git64.auth;

public interface IRKFAuthable {
	public boolean authToSector(int sector, byte[][] keys);
}
