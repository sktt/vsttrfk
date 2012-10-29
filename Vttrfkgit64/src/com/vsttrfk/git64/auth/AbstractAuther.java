package com.vsttrfk.git64.auth;

import java.io.IOException;

public abstract class AbstractAuther implements IRKFAuthable {
	
	public boolean authToSector(int sector, byte[][] keys) {
		int i = 0;
		boolean success = false;
		try {
			while (i < keys.length) {
				if (auth(sector,keys[i++])) {
					success = true;
					break;
				}
			}
		} catch (IOException e) {
			success = false;
		}
		return success;
	}
	
	public abstract boolean auth(int sector, byte[] tryKey) throws IOException;

}
