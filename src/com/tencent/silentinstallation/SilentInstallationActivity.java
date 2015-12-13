package com.tencent.silentinstallation;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SilentInstallationActivity extends Activity implements
		OnClickListener {

	public static final String TAG = "SilentInstallationActivity";

	ApplicationManagerExecutor mApplicationManagerExecutor = null;

	Button btnInstall, btnUninstall;
	TextView hintTextView;
	EditText mInputterEditText;
	OnInstalledPackaged mOnInstalledPackaged;
	OnDeletedPackaged mOnDeletedPackaged;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
			mApplicationManagerExecutor = new ApplicationManagerExecutor(
					getApplicationContext());
			mOnInstalledPackaged = new OnInstalledPackaged() {

				@Override
				public void packageInstalled(String packageName,
						final int returnCode) {
					if (returnCode == ApplicationManagerExecutor.INSTALL_SUCCEEDED) {
						Log.d(TAG, "Install succeeded" + packageName
								+ "  return code " + returnCode);
					} else {
						Log.d(TAG, "Install failed: " + packageName
								+ "  return code " + returnCode);
					}
				}
			};
			mOnDeletedPackaged = new OnDeletedPackaged() {
				@Override
				public void packageDeleted(String packageName, int returnCode) {
					if (returnCode == ApplicationManagerExecutor.INSTALL_SUCCEEDED) {
						Log.d(TAG, "Delete succeeded: " + packageName
								+ "  return code " + returnCode);
					} else {
						Log.d(TAG, "Delete failed: " + packageName
								+ "  return code " + returnCode);
					}
				}
			};
			mApplicationManagerExecutor
					.setOnInstalledPackage(mOnInstalledPackaged);
			mApplicationManagerExecutor.setOnDeletedPackage(mOnDeletedPackaged);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		String inputString = mInputterEditText.getText().toString();
		switch (v.getId()) {
		case R.id.btnInstall:
			install(inputString);
			break;
		case R.id.btnUninstall:
			uninstagll(inputString);
			break;
		default:
			break;
		}
	}

	private void install(final String path) {
		if (TextUtils.isEmpty(path)) {
			Log.e(TAG, "Illegal path.");
			return;
		}
		try {
			mApplicationManagerExecutor.install(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void uninstagll(final String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			Log.e(TAG, "Illegal packageName.");
			return;
		}
		try {
			mApplicationManagerExecutor.uninstall(packageName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
