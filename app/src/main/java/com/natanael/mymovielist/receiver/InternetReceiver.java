package com.natanael.mymovielist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.natanael.mymovielist.utils.NetworkUtils;

public class InternetReceiver extends BroadcastReceiver {

    public static final int INTERNET_CONNECTED = 1;
    public static final int INTERNET_DISCONNECTED = 2;

    private static Handler mCallBackHandler;

    public InternetReceiver(){
    }

    public InternetReceiver(Handler callBackHandler){
        mCallBackHandler = callBackHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(NetworkUtils.isInternetConnected(context)) {
            Message msg = new Message();
            msg.what = INTERNET_CONNECTED;
            mCallBackHandler.dispatchMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = INTERNET_CONNECTED;
            mCallBackHandler.dispatchMessage(msg);
        }
    }
}
