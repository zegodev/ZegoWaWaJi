package com.zego.zegowawaji_server.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.PrefUtil;
import com.zego.base.utils.SignatureUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 23/03/2018.
 */

public class UpgradeUtil {
    private OnDownloadListener mListener;
    private int apkVersionCode;
    private String apkVersionName;
    private String exceptedMD5;

    private boolean isDownloading = false;

    public UpgradeUtil() {
    }

    final static public void installApkSilent(String localApkPath, Context context) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction("ACTION_UPDATE_START");
            intent.putExtra("path", localApkPath);
            context.sendBroadcast(intent,null);
        }
    }

    static private boolean needDownload(PackageManager pm, String packageName, int versionCode) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return versionCode > packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static private boolean deleteOldDownload(DownloadManager downloadManager) {
        try {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(PrefUtil.getInstance().getDownloadId());
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                File rootDir = new File(localPath).getParentFile();
                deleteFolder(rootDir);
            } else {
                AppLogger.getInstance().writeLog("can't found any downloaded apk(s)");
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    static private boolean deleteFolder(File rootDir) {
        if (rootDir == null) return false;

        if (!rootDir.exists()) return false;

        if (rootDir.isFile()) {
            rootDir.delete();
        } else if (rootDir.isDirectory()) {
            for (File single : rootDir.listFiles()) {
                deleteFolder(single);
            }
        } else {
            AppLogger.getInstance().writeWarning("can't delete the target(neither is a file nor is directory: %s", rootDir);
        }
        return true;
    }

    synchronized public boolean downloadApk(String downloadUrl, int versionCode, String versionName, String md5, Context context, OnDownloadListener listener) {
        if (isDownloading) return false;

        isDownloading = true;

        apkVersionCode = versionCode;
        apkVersionName = versionName;
        exceptedMD5 = md5;

        mListener = listener;

        return _downloadInner(downloadUrl, context);
    }

    private boolean _downloadInner(String downloadUrl, Context context) {
        try {
            if (!needDownload(context.getPackageManager(), context.getPackageName(), apkVersionCode)) {
                AppLogger.getInstance().writeWarning("upgrade failed: special the apk's versionCode equal or less than current versionCode");
                return false;
            }

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            boolean deleteSuccess = deleteOldDownload(downloadManager);
            AppLogger.getInstance().writeLog("delete old upgrade file success ? %s", deleteSuccess);

            IntentFilter downloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(new ApkDownloadReceiver(), downloadFilter);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir("zego_upgrade", String.format("upgrade_v%d.apk", apkVersionCode));
            long taskId = downloadManager.enqueue(request);
            PrefUtil.getInstance().setDownloadId(taskId);

            AppLogger.getInstance().writeLog("add download task to DownloadManager's queue, taskId: %d", taskId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void onDownloadFinish(final String localUrl, int errorCode, final Context context) {
        if (errorCode != 0) {
            if (mListener != null) {
                mListener.onDownloadComplete(null, errorCode);
            }
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                File apkFile = new File(localUrl);
                String apkMD5 = getFileMD5(apkFile);
                if (!TextUtils.equals(apkMD5, exceptedMD5)) {
                    AppLogger.getInstance().writeError("upgrade failed: real md5(%s) not equal the excepted md5(%s)", apkMD5, exceptedMD5);
                    if (mListener != null) {
                        mListener.onDownloadComplete(null, 3);
                    }
                    return null;
                }

                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo apkInfo = pm.getPackageArchiveInfo(localUrl, PackageManager.GET_SIGNATURES);

                    if (apkInfo.versionCode != apkVersionCode) {
                        AppLogger.getInstance().writeWarning("upgrade failed, the apk's versionCode(%d) not the special versionCode(%d)", apkInfo.versionCode, apkVersionCode);
                        if (mListener != null) {
                            mListener.onDownloadComplete(null, 4);
                        }
                        return null;
                    }

                    PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                    if (packageInfo.versionCode >= apkInfo.versionCode) {
                        AppLogger.getInstance().writeWarning("upgrade failed, the apk's versionCode less than current versionCode");
                        if (mListener != null) {
                            mListener.onDownloadComplete(null, 5);
                        }
                        return null;
                    }

                    String apkSignKey = SignatureUtil.getSignFingerprintForApk(localUrl, context);
                    String packageSignKey = SignatureUtil.getSignFingerprintForPackage(context.getPackageName(), context);
                    if (!TextUtils.equals(apkSignKey, packageSignKey)) {
                        AppLogger.getInstance().writeWarning("upgrade failed, the sign fingerprints not equal");
                        if (mListener != null) {
                            mListener.onDownloadComplete(null, 6);
                        }
                        return null;
                    }

                    if (mListener != null) {
                        mListener.onDownloadComplete(localUrl, 0);
                    }
                } catch (Exception e) {
                    AppLogger.getInstance().writeWarning("upgrade failed, exception: %s", e);
                    if (mListener != null) {
                        mListener.onDownloadComplete(null, 7);
                    }
                }

                return null;
            }
        }.execute();
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }

        MessageDigest digest;
        FileInputStream in = null;
        byte buffer[] = new byte[8196];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 8196)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public interface OnDownloadListener {
        void onDownloadComplete(String localPath, int errorCode);
    }

    private class ApkDownloadReceiver extends BroadcastReceiver {

        public ApkDownloadReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                long currentDownloadId = PrefUtil.getInstance().getDownloadId();
                AppLogger.getInstance().writeLog("download task finish for taskId: %d, except id: %d", id, currentDownloadId);
                if (currentDownloadId != id) return;

                context.unregisterReceiver(this);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if(cursor.moveToFirst()) {
                    //获取文件下载路径
                    String localPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    AppLogger.getInstance().writeLog("downloaded apk's local path is: %s", localPath);
                    //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                    if (localPath != null && (new File(localPath)).exists()) {
                        onDownloadFinish(localPath, 0, context);
                    } else {
                        AppLogger.getInstance().writeWarning("download apk failed: local file not exist");
                        onDownloadFinish(localPath, 1, context);
                    }
                } else {
                    AppLogger.getInstance().writeWarning("download apk failed: local record not found");
                    onDownloadFinish(null, 2, context);
                }
                cursor.close();
            }
        }
    }
}
