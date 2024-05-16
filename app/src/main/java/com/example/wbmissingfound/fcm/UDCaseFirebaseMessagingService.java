package com.example.wbmissingfound.fcm;


import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.wbmissingfound.R;
import com.example.wbmissingfound.custom.DebugLog;
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by Joy Mondal on 18/01/2024.
 */


public class UDCaseFirebaseMessagingService extends FirebaseMessagingService {

    String user_type;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        DebugLog.printD(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            DebugLog.printD(TAG, "Message data payload: " + remoteMessage.getData());
            /*    String gcmMessage = ""+remoteMessage.getData();
                sendNotification("",gcmMessage);
*/

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());
            SharedPreferenceStorage.setValue(getApplicationContext(),
                    "JsonObject",
                    object.toString());
            try {
                String gcmMessage = object.toString();
                JSONObject jsonObject = new JSONObject(gcmMessage);
               // user_type = SharedPreferenceStorage.getValue(getBaseContext(), SharedPreferenceStorage.Login.USER_TYPE, "");
               // {"infoId":"65bb6c3d53e9c23f16fba881","msg":"Michil","title":"Joy Mondal","senderId":"65ae49238243d202291cb41d"}
            /*    user_type=SharedPreferenceStorage.getValue(getApplicationContext(), SharedPreferenceStorage.USERID,"");
                if (user_type != null && user_type.length() > 0)
                   sendNotification(jsonObject.getString("title"), jsonObject.getString("msg"), jsonObject);*/

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            DebugLog.printD(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }


    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        DebugLog.print("onNewToken: " + refreshedToken);
        SharedPreferenceStorage.setValue(getApplicationContext(),
                SharedPreferenceStorage.Login.FCM_TOKEN,
                refreshedToken);
        SharedPreferenceStorage.setValue(getApplicationContext(),
                SharedPreferenceStorage.Login.FCM_TOKEN_SEND_TO_SERVER,
                false);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification(String title, String message, JSONObject object) {
        // Create an explicit intent for an Activity in your app


      //  Intent intent = new Intent(this, InformationDetailsActivity.class);

        try {
            String gcmMessage = object.toString();
            JSONObject jsonObject = new JSONObject(gcmMessage);


           // intent = new Intent(this, InformationDescription.class);
          /*  intent.putExtra(Constants.PUSH_NOTIFICATION.INSTANCE.getCALL(), Constants.PUSH_NOTIFICATION.INSTANCE.getFROM_PUSH());
            intent.putExtra(Constants.PUSH_NOTIFICATION.INSTANCE.INSTANCE.getINFO_ID(), jsonObject.getString("infoId"));*/

            String noti_type ="";// jsonObject.getString("noti_type");
            // if(noti_type!=null && (noti_type.equals("1") ||  noti_type.equals("2")) )
           // if (noti_type != null) {

                /*
                if (noti_type.equals("1")) {
                    if (user_type.equals("" + Constant.USER_TYPE.NODAL.ordinal()))
                        intent = new Intent(this, SubmitInformationActivity.class);
                    else {
                        intent = new Intent(this, InformationDescription.class);
                        intent.putExtra(Constant.CALL, Constant.FROM_PUSH);
                        intent.putExtra(Constant.INFO_ID, jsonObject.getString("info_id"));
                    }

                } else if (noti_type.equals("3")) {
                   // intent = new Intent(this, ASCheckDescription.class);

                } else if (noti_type.equals("4")) {


                  /*  if (user_type.equals("" + Constant.USER_TYPE.NODAL.ordinal()))

                        intent = new Intent(this, LocalNewsDescription.class);
                    else*/
     /*               intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(Constant.CHAT_WITH, jsonObject.getString("user_type"));
                    intent.putExtra(Constant.RECEIVER_ID, jsonObject.getString("sender_id"));
                    intent.putExtra(Constant.SENDER_NAME, jsonObject.getString("sender_name"));

                } else if (noti_type.equals("5")) {
                    intent = new Intent(this, DisposalReportActivity.class);
                    intent.putExtra(Constant.DISPOSAL_ID, jsonObject.getString("info_id"));

                }else if (noti_type.equals("6")) {
                    intent = new Intent(this, DisposalReportActivity.class);
                    intent.putExtra(Constant.DISPOSAL_ID, jsonObject.getString("info_id"));

                }  else {
                    intent = new Intent(this, InformationDescription.class);
                }
                intent.putExtra(Constant.CALL, Constant.FROM_PUSH);
                intent.putExtra(Constant.INFO_ID, jsonObject.getString("info_id"));

                */
         //   }
        } catch (JSONException e) {
            e.printStackTrace();

        }

       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Uri.parse("android.resource://com.example.andtip/"+R.raw.bomb_sound))
       // Uri defaultSoundUri = Uri.parse("android.resource://in.gov.cidwestbengal.bdds/" + R.raw.bomb_sound);
        // Uri defaultSoundUri = Uri.parse(getResources().getResourceName(R.raw.bomb_sound));
          Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "UDCASE");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setColor(getResources().getColor(R.color.text_blue));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.channel_name);
            String CHANNEL_ID = getResources().getString(R.string.channel_id);
            String channelDescription = getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setLightColor(Color.GREEN); //Set if it is necesssary
            channel.enableVibration(true); //Set if it is necesssary
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            channel.setSound(defaultSoundUri,attributes);


            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //   NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(CHANNEL_ID);

        }
        notificationBuilder.setContentIntent(pendingIntent);*/
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notificationBuilder.setSound(defaultSoundUri);
        //Vibration                                                             <uses-permission android:name="android.permission.VIBRATE" />
        notificationBuilder.setVibrate(new long[]{250, 250, 250, 250, 250}); // { delay, vibrate, sleep, vibrate, sleep }
        //LED
        notificationBuilder.setLights(Color.BLUE, 3000, 3000);

        if (notificationManager != null) {
          /*  Random rand = new Random();
            int notiId = rand.nextInt(50);
            notificationManager.notify(notiId, notificationBuilder.build());
            int unreadNotifications = SharedPreferenceStorage.getValue(getBaseContext(), SharedPreferenceStorage.NOTIFICATION_COUNT, 0);
            SharedPreferenceStorage.setValue(getBaseContext(), SharedPreferenceStorage.NOTIFICATION_COUNT, String.valueOf(++unreadNotifications));*/

           /* if (BaseActivity.activity != null && BaseActivity.activity instanceof HomeActivity) {
                ((HomeActivity) BaseActivity.activity).increase_noti_count();
            }*/
/*
           var Activity=BaseActivity.PresentActivityContext.Companion.getActivity()
            if (BaseActivity.PresentActivityContext.Companion.getActivity() != null &&
                    BaseActivity.PresentActivityContext.Companion.getActivity() is MainActivity) {

            }*/

           /* if(BaseActivity.PresentActivityContext.Companion.getPresentactivity() instanceof MainActivity)
            {
                ((MainActivity) BaseActivity.PresentActivityContext.Companion.getPresentactivity()).increase_noti_count();
            }*/
        }
    }
}
