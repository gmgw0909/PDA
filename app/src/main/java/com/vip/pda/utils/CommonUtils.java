package com.vip.pda.utils;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.vip.pda.file.SPUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class CommonUtils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 判断某个手机号是否存在
     */
    public static boolean isThePhoneExist(Context context, String phoneNum) {
        //uri=  content://com.android.contacts/data/phones/filter/#
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phoneNum);
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(uri, new String[]{ContactsContract.Data.DISPLAY_NAME},
                    null, null, null); //从raw_contact表中返回display_name
            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 判断设备 是否使用代理上网
     */
    public static boolean isWifiProxy() {
        String proxyAddress = System.getProperty("http.proxyHost");
        String portStr = System.getProperty("http.proxyPort");
        int proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 获取发布渠道ID
     *
     * @return 1000[官方] 1001[小米] 1002[华为] 1003[360] 1004[应用宝]、 阿里[1005]、vivo[1006]、百度[1007]、oppo[1008]、魅族[1009]、google[1010]
     */
    public static String getChannelId(Context context) {
        ApplicationInfo appInfo = null;
        String channelIdStr = "";
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object channelId = appInfo.metaData.get("DM_CHANNEL_ID");
            channelIdStr = TextUtils.isEmpty(channelId.toString()) ? "1000" : channelId.toString();
        } catch (PackageManager.NameNotFoundException e) {
            channelIdStr = "1000";
            e.printStackTrace();
        }
        return channelIdStr;
    }

    /**
     * 检测权限
     *
     * @param context    Context
     * @param permission 权限名称
     * @return true:已允许该权限; false:没有允许该权限
     */
    public static boolean checkHasPermission(Context context, String permission) {
        try {
            Class<?> contextCompat = null;
            try {
                contextCompat = Class.forName("android.support.v4.content.ContextCompat");
            } catch (Exception e) {
                //ignored
            }

            if (contextCompat == null) {
                try {
                    contextCompat = Class.forName("androidx.core.content.ContextCompat");
                } catch (Exception e) {
                    //ignored
                }
            }

            if (contextCompat == null) {
                return true;
            }

            Method checkSelfPermissionMethod = contextCompat.getMethod("checkSelfPermission", Context.class, String.class);
            int result = (int) checkSelfPermissionMethod.invoke(null, new Object[]{context, permission});
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission ", "You can fix this by adding the following to your AndroidManifest.xml file:\n"
                        + "<uses-permission android:name=\"" + permission + "\" />");
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.i("Permission ", e.toString());
            return true;
        }
    }

    /**
     * 是否调试进程
     *
     * @return
     */
    public static boolean isUnderTraced() {
        String processStatusFilePath = String.format(Locale.US, "/proc/%d/status", android.os.Process.myPid());
        File procInfoFile = new File(processStatusFilePath);
        try {
            BufferedReader b = new BufferedReader(new FileReader(procInfoFile));
            String readLine;
            while ((readLine = b.readLine()) != null) {
                if (readLine.contains("TracerPid")) {
                    String[] arrays = readLine.split(":");
                    if (arrays.length == 2) {
                        int tracerPid = Integer.parseInt(arrays[1].trim());
                        if (tracerPid != 0) {
                            return true;
                        }
                    }
                }
            }

            b.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }

    public static boolean isLogin() {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString("User"))) {
            return false;
        }
        return true;
    }
}