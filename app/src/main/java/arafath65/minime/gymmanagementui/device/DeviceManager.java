package arafath65.minime.gymmanagementui.device;

import android.content.Context;
import android.provider.Settings;

public class DeviceManager {

    private final Context context;

    public DeviceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getDeviceUuid() {

        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }
}
