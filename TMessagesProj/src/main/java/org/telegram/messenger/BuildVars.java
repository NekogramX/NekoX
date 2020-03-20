/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class BuildVars {

    public static boolean DEBUG_VERSION = true;
    public static boolean DEBUG_PRIVATE_VERSION = true;
    public static boolean LOGS_ENABLED = true;
    public static boolean USE_CLOUD_STRINGS = true;

    public static int BUILD_VERSION;
    public static String BUILD_VERSION_STRING;

    static {

        try {
            PackageInfo info = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            BUILD_VERSION = info.versionCode;
            BUILD_VERSION_STRING = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            BUILD_VERSION = BuildConfig.VERSION_CODE;
            BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;
        }

    }

    static {
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }
}
