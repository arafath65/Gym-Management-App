package arafath65.minime.gymmanagementui;

import android.app.Application;

import arafath65.minime.gymmanagementui.Network.ApiClient;

public class GymManagementApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.initialize(this);
    }
}
