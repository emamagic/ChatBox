package com.emamagic.chatbox;

import android.app.Application;
import androidx.annotation.NonNull;

public class App extends Application {

    private static volatile App INSTANCE;

    @NonNull
    public static App getInstance() {
        return INSTANCE;
    }
}
