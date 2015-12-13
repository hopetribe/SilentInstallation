package com.tencent.silentinstallation;

public interface OnInstalledPackaged {
	
	public void packageInstalled(String packageName, int returnCode);

}
