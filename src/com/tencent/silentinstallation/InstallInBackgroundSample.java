package com.tencent.silentinstallation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.silentinstallation.apihelper.ApplicationManager;

public class InstallInBackgroundSample extends Activity {

    public static final String TAG = "InstallInBackgroundSample";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            final ApplicationManagerExecutor am = new ApplicationManagerExecutor(InstallInBackgroundSample.this);
            am.setOnInstalledPackage(new OnInstalledPackaged() {

                public void packageInstalled(String packageName, final int returnCode) {
                    if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                        InstallInBackgroundSample.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(InstallInBackgroundSample.this, "Install succeeded" + returnCode,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG, "Install succeeded" + packageName + "  return code " + returnCode);
                    } else {
                        InstallInBackgroundSample.this.runOnUiThread(new Runnable() {
                            
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                
                            }
                        });
                        Log.d(TAG, "Install failed: " + packageName + "  return code " + returnCode);
                    }
                }
            });

            am.setOnDeletedPackage(new OnDeletedPackaged() {

                @Override
                public void packageDeleted(String packageName, int returnCode) {
                    // TODO Auto-generated method stub
                    if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                        // Toast.makeText(InstallInBackgroundSample.this, "Install succeeded" + returnCode,
                        // Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Delete succeeded: " + packageName + "  return code " + returnCode);
                    } else {
                        // Toast.makeText(InstallInBackgroundSample.this, "Install failed" + returnCode,
                        // Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Delete failed: " + packageName + "  return code " + returnCode);
                    }
                }
            });

            final TextView txtApkFilePath = (TextView) findViewById(R.id.txtApkFilePath);

            Button btnInstall = (Button) findViewById(R.id.btnInstall);
            btnInstall.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        // am.installPackage(txtApkFilePath.getText().toString());
                        am.installByReflection("/sdcard/Music/demo.apk");
                    } catch (Exception e) {
                        logError(e);
                    }
                }
            });
            Button btnInstall2 = (Button) findViewById(R.id.btnInstall2);
            btnInstall2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        // am.installPackage(txtApkFilePath.getText().toString());
                        am.installByReflection("/sdcard/Music/demo1.apk");
                    } catch (Exception e) {
                        logError(e);
                    }
                }
            });
            Button btnUninstall = (Button) findViewById(R.id.btnUninstall);
            btnUninstall.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        // am.installPackage(txtApkFilePath.getText().toString());
                        am.uninstallByReflection("com.tencent.tws.systemui");
                    } catch (Exception e) {
                        logError(e);
                    }
                }
            });
            Button btnUninstall2 = (Button) findViewById(R.id.btnUninstall2);
            btnUninstall2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        am.uninstallByReflection("com.tencent.tws.heartrate");
                    } catch (Exception e) {
                        logError(e);
                    }
                }
            });

        } catch (Exception e) {
            logError(e);
        }
    }

    private void logError(Exception e) {
        e.printStackTrace();
        Toast.makeText(InstallInBackgroundSample.this, R.string.error, Toast.LENGTH_LONG).show();
    }
}
