package com.adopshun.render.retrofit;

import androidx.annotation.Nullable;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    /**
     * @return
     */
    @Nullable
    @Override
    public String getMessage() {
        return "No Internet Connection";
    }
}
