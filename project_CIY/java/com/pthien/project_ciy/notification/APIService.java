package com.pthien.project_ciy.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAYihKrjI:APA91bF_v33z3LDbU8sX8BPZ07T3QJkr1dlhBAcSuHchjOTryG8l9alyphOgUPklDDgGabgWpjtTUit1X4-rtdYpZmuUCOlIkbNSXYPmPkM_YRPEDw8giZpFS_FKQ_MWEQaKkCw3lyjj"
    })

    @POST("fcm/send")
    Call<Response> senNotification(@Body Sender body);
}
