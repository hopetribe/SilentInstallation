package com.hopetribe.silentinstallation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * @ClassName: ApplicationManagerExecutor
 * @Description: 应用安装类，包括交互安装和静默安装； 静默安装需要获得系统权限。
 * @author ericczhuang
 * @date 2014-8-8 下午3:29:56
 * 
 */
public class ApplicationManagerExecutor {

    private static String TAG = "ApplicationManagerExecutor";

    public static final int INSTALL_BY_NORMAL = 3;

    public static final int INSTALL_REPLACE_EXISTING = 2;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} on success.
     * 
     * @hide
     */
    public static final int INSTALL_SUCCEEDED = 1;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the package is already installed.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the package archive file is invalid.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_INVALID_APK = -2;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the URI passed in is invalid.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_INVALID_URI = -3;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the package manager service found that
     * the device didn't have enough storage space to install the app.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if a package is already installed with the
     * same name.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the requested shared user does not
     * exist.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if a previously installed package of the
     * same name has a different signature than the new package (and the old package's data was not removed).
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package is requested a shared
     * user which is already installed on the device and does not have matching signature.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package uses a shared library
     * that is not available.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package uses a shared library
     * that is not available.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package failed while optimizing
     * and validating its dex files, either because there was not enough storage or the validation failed.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_DEXOPT = -11;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package failed because the
     * current SDK version is older than that required by the package.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_OLDER_SDK = -12;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package failed because it
     * contains a content provider with the same authority as a provider already installed in the system.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package failed because the
     * current SDK version is newer than that required by the package.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_NEWER_SDK = -14;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package failed because it has
     * specified that it is a test-only package and the caller has not supplied the {@link #INSTALL_ALLOW_TEST} flag.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_TEST_ONLY = -15;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the package being installed contains
     * native code, but none that is compatible with the the device's CPU_ABI.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package uses a feature that is
     * not available.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;

    // ------ Errors related to sdcard
    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if a secure container mount point couldn't
     * be accessed on external media.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package couldn't be installed
     * in the specified install location.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;

    /**
     * Installation return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the new package couldn't be installed
     * in the specified install location because the media is not available.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser was given a path that is not
     * a file, or does not end with the expected '.apk' extension.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser was unable to retrieve the
     * AndroidManifest.xml file.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser encountered an unexpected
     * exception.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser did not find any
     * certificates in the .apk.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser found inconsistent
     * certificates on the files in the .apk.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser encountered a
     * CertificateEncodingException in one of the files in the .apk.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser encountered a bad or missing
     * package name in the manifest.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser encountered a bad shared
     * user id name in the manifest.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser encountered some structural
     * problem in the manifest.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;

    /**
     * Installation parse return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the parser did not find any actionable
     * tags (instrumentation or application) in the manifest.
     * 
     * @hide
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;

    /**
     * Installation failed return code: this is passed to the {@link IPackageInstallObserver} by
     * {@link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if the system failed to install the
     * package because of system issues.
     * 
     * @hide
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;

    /**
     * Flag parameter for {@link #deletePackage} to indicate that you don't want to delete the package's data directory.
     * 
     * @hide
     */
    public static final int DONT_DELETE_DATA = 0x00000001;

    /**
     * Return code for when package deletion succeeds. This is passed to the {@link IPackageDeleteObserver} by
     * {@link #deletePackage()} if the system succeeded in deleting the package.
     * 
     * @hide
     */
    public static final int DELETE_SUCCEEDED = 1;

    /**
     * Deletion failed return code: this is passed to the {@link IPackageDeleteObserver} by {@link #deletePackage()} if
     * the system failed to delete the package for an unspecified reason.
     * 
     * @hide
     */
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;

    /**
     * Deletion failed return code: this is passed to the {@link IPackageDeleteObserver} by {@link #deletePackage()} if
     * the system failed to delete the package because it is the active DevicePolicy manager.
     * 
     * @hide
     */
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;

    private static final int DELETE_FAILED_INVALID_PACKAGE = -3;

    private static final int DELETE_FAILED_PERMISSION_DENIED = -4;

    /**
     * App installation location settings values, same to {@link #PackageHelper}
     */
    public static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;

    private PackageInstallObserver observer;
    private PackageDeleteObserver deleteObserver;

    private PackageManager pm;
    private Method method, uninstallMethod;

    private OnInstalledPackaged onInstalledPackaged;
    private OnDeletedPackaged onPackageDeleted;
    private Context mContext;

    /**
     * @ClassName: PackageInstallObserver
     * @Description: 安装APP的观察者接口
     * @author ericczhuang
     * @date 2014-8-8 下午3:00:47
     * 
     */
    class PackageInstallObserver extends IPackageInstallObserver.Stub {

        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            Log.d(TAG, "packageInstalled");
            if (onInstalledPackaged != null) {
                Log.d(TAG, "packageInstalled callback -->:" + returnCode);
                onInstalledPackaged.packageInstalled(packageName, returnCode);
            }
        }
    }

    /**
     * @ClassName: PackageDeleteObserver
     * @Description: 卸载APP的观察者接口
     * @author ericczhuang
     * @date 2014-8-8 下午3:01:03
     * 
     */
    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {

        public void packageDeleted(String packageName, int returnCode) throws RemoteException {
            Log.d(TAG, "packageDeleted callback -->:" + returnCode);
            if (onPackageDeleted != null) {
                onPackageDeleted.packageDeleted(packageName, returnCode);
            }
        }
    }

    /**
     * 创建一个新的实例 ApplicationManagerExecutor.
     * <p>
     * Method:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param context
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public ApplicationManagerExecutor(Context context) throws SecurityException, NoSuchMethodException {
        mContext = context;
        observer = new PackageInstallObserver();
        deleteObserver = new PackageDeleteObserver();

        pm = context.getPackageManager();

        Class<?>[] types = new Class[] { Uri.class, IPackageInstallObserver.class, int.class, String.class };
        method = pm.getClass().getMethod("installPackage", types);

        Class<?>[] uninstalltypes = new Class[] { String.class, IPackageDeleteObserver.class, int.class };
        uninstallMethod = pm.getClass().getMethod("deletePackage", uninstalltypes);
    }

    /**
     * @Method: setOnInstalledPackaged
     * @Description: 设置安装回调接口
     * @param onInstalledPackaged 返回类型：void
     */
    public void setOnInstalledPackage(OnInstalledPackaged onInstalledPackaged) {
        this.onInstalledPackaged = onInstalledPackaged;
    }

    /**
     * @param onPackageDeleted
     * @Method: setOnPackageDeleted
     * @Description: 设置卸载回调接口
     * @param onPackageDeleted 返回类型：void
     */
    public void setOnDeletedPackage(OnDeletedPackaged onPackageDeleted) {
        this.onPackageDeleted = onPackageDeleted;
    }

    /**
     * @Method: install
     * @Description: 根据路径安装APK
     * @param filePath APK绝对路径
     * @return 返回执行结果
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * 
     */
    public final int install(String filePath) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        if (isSystemApplication() || ShellUtils.checkRootPermission()) {
            return installByReflection(filePath);
        }
        return installNormal(filePath) ? INSTALL_BY_NORMAL : INSTALL_FAILED_INVALID_URI;
    }

    /**
     * @Method: installNormal
     * @Description: TODO
     * @param filePath
     * @return 返回类型：boolean
     */
    private boolean installNormal(String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
        return true;
    }

    /**
     * @Method: installByReflection
     * @Description: 安装APK
     * @param apkFile，apk绝对路径
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException 返回类型：int
     */
    public int installByReflection(String apkFile) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return installByReflection(new File(apkFile));
    }

    /**
     * @Method: installPackage
     * @Description: 安装APK
     * @param apkFile，File对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException 返回类型：void
     */
    public int installByReflection(File apkFile) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        if (!apkFile.exists())
            throw new IllegalArgumentException();
        Uri packageURI = Uri.fromFile(apkFile);
        return installByReflection(packageURI);
    }

    /**
     * @Method: installPackage
     * @Description: 反射方法安装APK
     * @param apkFile，apk对应的Url对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException 返回类型：void
     */
    public int installByReflection(Uri apkFile) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        method.invoke(pm, new Object[] { apkFile, observer, INSTALL_REPLACE_EXISTING, null });
        return 0;
    }

    /**
     * @Method: installByCmd
     * @Description: 调用命令pm静默安装app，需要系统权限：android.permission.INSTALL_PACKAGES。 请注意避免在UI主线程中调用。 pm install默认参数 -r（替换安装）
     * @param filePath APK绝对路径
     * @return 返回类型：int
     */
    public int installByCmd(String filePath) {
        return installByCmd(filePath, " -r " + getInstallLocationParams());
    }

    /**
     * @Method: installByCmd
     * @Description: 调用命令pm静默安装app，需要系统权限：android.permission.INSTALL_PACKAGES。 请注意避免在UI主线程中调用。 pm install默认参数 -r（替换安装）
     * @param filePath APK绝对路径
     * @param pmParams 安装路径，通过pm的命令参数 -f 和 -s选择安装在内部存储器或者外部存储器
     * @return 返回类型：int
     */
    public int installByCmd(String filePath, String pmParams) {

        if (filePath == null || filePath.length() == 0) {
            return INSTALL_FAILED_INVALID_URI;
        }

        File file = new File(filePath);
        if (file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return INSTALL_FAILED_INVALID_URI;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
         **/
        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                .append(pmParams == null ? "" : pmParams).append(" ").append(filePath.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(),
                true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return INSTALL_SUCCEEDED;
        } else {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }

    }

    /**
     * @Method: installByCmd2
     * @Description: pm命令静默安装APK，需要系统权限：android.permission.INSTALL_PACKAGES
     * @param filePath
     * @return 返回类型：int ，0 成功，-1 失败
     */
    public int installByCmd2(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0 || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {
            return 1;
        }

        String[] args = { "pm", "install", "-r", filePath };
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result = 0;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s = null;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = -1;
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * @Method: getInstallLocationParams
     * @Description: 获取pm安装位置
     * @return 返回类型：String
     */
    private String getInstallLocationParams() {
        int location = getInstallLocation();
        switch (location) {
            case APP_INSTALL_INTERNAL:
                return "-f";
            case APP_INSTALL_EXTERNAL:
                return "-s";
        }
        return "";
    }

    /**
     * @Method: getInstallLocation
     * @Description: 获取系统安装位置
     * @return 返回类型：int
     */
    public int getInstallLocation() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
        if (commandResult.result == 0 && commandResult.successMsg != null && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e(TAG, "pm get-install-location error");
            }
        }
        return APP_INSTALL_AUTO;
    }

    /**
     * @Method: uninstall
     * @Description: 卸载App
     * @param packageName 返回类型：void
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public int uninstall(String packageName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (isSystemApplication() || ShellUtils.checkRootPermission()) {
            uninstallByReflection(packageName);
            return 1;
        } else {
            return uninstallNormal(packageName) ? 1 : 0;
        }
    }

    /**
     * @Method: uninstallPackage
     * @Description: 反射方法静默卸载APK，需要权限android.permission.DELETE_PACKAGES
     * @param packagename， 需要卸载应用的包名
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException 返回类型：void
     */
    public void uninstallByReflection(String packagename) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        uninstallMethod.invoke(pm, new Object[] { packagename, deleteObserver, 0 });
    }

    /**
     * @Method: uninstallNormal
     * @Description: TODO
     * @param context
     * @param packageName
     * @return 返回类型：boolean
     */
    public boolean uninstallNormal(String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder(32).append("package:")
                .append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
        return true;
    }

    /**
     * @Method: uninstallByCmd
     * @Description: 静默卸载应用以及清楚应用数据
     * @param packageName
     * @return 返回类型：int
     */
    public int uninstallByCmd(String packageName) {
        return uninstallByCmd(packageName, true);
    }

    /**
     * @Method: uninstallSilent，因为需要时间去卸载，请不要在UI主线程中调用此方法，以避免ANR错误
     * @Description: 静默卸载，需要权限android.permission.DELETE_PACKAGES
     * @param packageName，卸载应用的包名
     * @param isKeepData，是否保存应用数据
     * @return 返回类型：int <li>{@link #DELETE_SUCCEEDED} 卸载成功</li> <li>{@link #DELETE_FAILED_INTERNAL_ERROR} 内部错误</li> <li>
     * {@link #DELETE_FAILED_INVALID_PACKAGE} 包名错误</li> <li>{@link #DELETE_FAILED_PERMISSION_DENIED} 权限拒绝</li>
     */
    public int uninstallByCmd(String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return DELETE_FAILED_INVALID_PACKAGE;
        }

        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                .append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(),
                true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return DELETE_SUCCEEDED;
        }
        Log.e(TAG,
                new StringBuilder().append("uninstallSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        if (commandResult.errorMsg == null) {
            return DELETE_FAILED_INTERNAL_ERROR;
        }
        if (commandResult.errorMsg.contains("Permission denied")) {
            return DELETE_FAILED_PERMISSION_DENIED;
        }
        return DELETE_FAILED_INTERNAL_ERROR;
    }

    /**
     * @Method: uninstallByCmd2
     * @Description: pm命里行静默卸载APK，需要权限android.permission.DELETE_PACKAGES
     * @param packagename
     * @return 返回类型：int ，0 成功，-1 失败
     */
    public int uninstallByCmd2(String packagename) {
        if (packagename == null) {
            return 1;
        }

        String[] args = { "pm", "uninstall", packagename };
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result = 0;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s = null;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = -1;
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * @Method: isSystemApplication
     * @Description: 判断是否是系统app
     * @return 返回类型：boolean
     */
    private boolean isSystemApplication() {
        return isSystemApplication(mContext.getPackageName());
    }

    /**
     * @Method: isSystemApplication
     * @Description: 判断是否是系统app
     * @param packageName
     * @return 返回类型：boolean
     */
    private boolean isSystemApplication(String packageName) {
        return isSystemApplication(mContext.getPackageManager(), packageName);
    }

    /**
     * @Method: isSystemApplication
     * @Description: 判断是否是系统app
     * @param packageManager
     * @param packageName
     * @return 返回类型：boolean
     */
    private boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
