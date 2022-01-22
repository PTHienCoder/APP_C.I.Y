package com.pthien.project_ciy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;


public class Load_Data {
       public static boolean isNetWorkAcess(Context context){
        if (context == null){
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Network network = connectivityManager.getActiveNetwork();
            if (network == null){
                return false;

            }
//            Log.e("jhhvhjfkjgkjgl", "hgjhgjhg");
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))||
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));


        }else {
//            Log.e("jhhvhjfkjgkjgl", "pppppppppppp");
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
