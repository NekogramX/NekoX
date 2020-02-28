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

    public static void toggleDisableChatAction() {

        preferences.edit().putBoolean("disable_chat_action", disableChatAction = !disableChatAction).apply();

    }

    static {

        disableChatAction = preferences.getBoolean("disable_chat_action",false);

    }

}