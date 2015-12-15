package com.hopetribe.silentinstallation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hopetribe.silentinstallation.R;

@SuppressLint("HandlerLeak")
public class SilentInstallationActivity extends Activity implements OnClickListener {

    public static final String TAG = "SilentInstallationActivity";

    ApplicationManagerExecutor mApplicationManagerExecutor = null;

    Button btnInstall, btnUninstall;
    TextView hintTextView;
    EditText mInputterEditText;
    OnInstalledPackaged mOnInstalledPackaged;
    OnDeletedPackaged mOnDeletedPackaged;

    Handler mHandler;
    private final static int MSG_SHOW_TOAST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initHandler();
        initApplicationManagerExecutor();

        hintTextView = (TextView) findViewById(R.id.hint);
        btnInstall = (Button) findViewById(R.id.btnInstall);
        btnInstall.setOnClickListener(this);
        btnUninstall = (Button) findViewById(R.id.btnUninstall);
        btnUninstall.setOnClickListener(this);
        mInputterEditText = (EditText) findViewById(R.id.input);

    }

    private void initApplicationManagerExecutor() {
        try {
            mApplicationManagerExecutor = new ApplicationManagerExecutor(getApplicationContext());
            mOnInstalledPackaged = new OnInstalledPackaged() {

                @Override
                public void packageInstalled(String packageName, final int returnCode) {
                    String hintString;
                    if (returnCode == ApplicationManagerExecutor.INSTALL_SUCCEEDED) {
                        hintString = String.format(Locale.getDefault(),
                                "Install succeeded: packageName = %s, returnCode = %d", packageName, returnCode);
                    } else {
                        hintString = String.format(Locale.getDefault(),
                                "Install failed: packageName = %s, returnCode = %d", packageName, returnCode);
                    }
                    Log.d(TAG, hintString);
                    Message msg = mHandler.obtainMessage(MSG_SHOW_TOAST);
                    msg.obj = hintString;
                    msg.sendToTarget();
                }
            };
            mOnDeletedPackaged = new OnDeletedPackaged() {
                @Override
                public void packageDeleted(String packageName, int returnCode) {
                    String hintString;
                    if (returnCode == ApplicationManagerExecutor.DELETE_SUCCEEDED) {
                        hintString = String.format(Locale.getDefault(),
                                "Delete succeeded: packageName = %s, returnCode = %d", packageName, returnCode);
                    } else {
                        hintString = String.format(Locale.getDefault(),
                                "Delete failed: packageName = %s, returnCode = %d", packageName, returnCode);
                    }
                    Log.d(TAG, hintString);
                    Message msg = mHandler.obtainMessage(MSG_SHOW_TOAST);
                    msg.obj = hintString;
                    msg.sendToTarget();
                }
            };
            mApplicationManagerExecutor.setOnInstalledPackage(mOnInstalledPackaged);
            mApplicationManagerExecutor.setOnDeletedPackage(mOnDeletedPackaged);
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }

    }

    protected void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SHOW_TOAST:
                        String hintString = (String) msg.obj;
                        hintTextView.setText(hintString);
                        Toast.makeText(SilentInstallationActivity.this, hintString, Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        String inputString = mInputterEditText.getText().toString();
        Log.d(TAG, inputString);
        switch (v.getId()) {
            case R.id.btnInstall:
                install(Environment.getExternalStorageDirectory().getPath() + File.separator + inputString);
                break;
            case R.id.btnUninstall:
                uninstagll(inputString);
                break;
            default:
                break;
        }
    }

    private void install(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "Illegal path.");
            path = "/sdcard/demo.apk";
        }
        try {
            if (mApplicationManagerExecutor != null) {
                mApplicationManagerExecutor.installByReflection(path);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void uninstagll(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Illegal packageName.");
            packageName = "com.tencent.tws.heartrate";
            // packageName = "com.example.demo";
        }
        try {
            if (mApplicationManagerExecutor != null) {
                mApplicationManagerExecutor.uninstallByReflection(packageName);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
