package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;

public class NekoXConfig {

    public static int[] DEVELOPER_IDS = {896711046};

    private static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekox_config", Context.MODE_PRIVATE);

    public static boolean disableChatAction;

    public static boolean developerModeEntrance;
    public static boolean developerMode;

    public static boolean disableFlagSecure;
    public static boolean disableScreenshotDetection;

    public static void toggleDisableChatAction() {

        preferences.edit().putBoolean("disable_chat_action", disableChatAction = !disableChatAction).apply();

    }

    public static void toggleDeveloperMode() {

        preferences.edit().putBoolean("develoepr_mode", developerMode = !developerMode).apply();

    }

    public static void toggleDisableFlagSecure() {

        preferences.edit().putBoolean("disable_flag_secure", disableFlagSecure = !disableFlagSecure).apply();

    }

    public static void toggleDisableScreenshotDetection() {

        preferences.edit().putBoolean("disable_screenshot_detection", disableScreenshotDetection = !disableScreenshotDetection).apply();

    }

    static {

        disableChatAction = preferences.getBoolean("disable_chat_action", false);

        developerMode = preferences.getBoolean("develoepr_mode",false);

        disableFlagSecure = preferences.getBoolean("disable_flag_secure", false);
        disableScreenshotDetection = preferences.getBoolean("disable_screenshot_detection",false);

    }

}