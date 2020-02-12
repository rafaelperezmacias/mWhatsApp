package com.zamnadev.mwhatsapp.Notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zamnadev.mwhatsapp.ChatActivity;
import com.zamnadev.mwhatsapp.R;

import java.util.Random;

public class ServicioMensajes extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String token = s;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens")
                    .child(firebaseUser.getUid())
                    .child("token");

            reference.setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String receptor = remoteMessage.getData().get("receptor");

        if (firebaseUser != null && receptor.equals(firebaseUser.getUid())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enviarOreoNotificacion(remoteMessage);
            } else {
                enviarNotificacion(remoteMessage);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarOreoNotificacion(RemoteMessage remoteMessage) {
        String titulo = remoteMessage.getData().get("titulo");
        String cuerpo = remoteMessage.getData().get("mensaje");
        String usuario = remoteMessage.getData().get("emisor");

        int j = Integer.parseInt(usuario.replaceAll("[\\D]",""));
        Bundle bundle = new Bundle();
        bundle.putString("id",usuario);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.zamnadev.mwhasapp.Notificaciones")
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setAutoCancel(true)
                .setSound(sonido)
                .setVibrate(new long[] {100,250,100,2})
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        int randomNo = 0;
        if (j > 0){
            randomNo = j;
        }

        randomNo = (int) (Math.random() * 100000);

        NotificationChannel channel = new NotificationChannel("com.zamnadev.mwhasapp.Notificaciones","mWhatsApp",NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        channel.enableLights(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(randomNo,builder.build());
    }

    private void enviarNotificacion(RemoteMessage remoteMessage) {

        String titulo = remoteMessage.getData().get("titulo");
        String mensaje = remoteMessage.getData().get("mensaje");
        String emisor = remoteMessage.getData().get("emisor");

        int j = Integer.parseInt(emisor.replaceAll("[\\D]",""));
        Bundle bundle = new Bundle();
        bundle.putString("id",emisor);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.zamnadev.mwhasapp.Notificaciones")
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(sonido)
                .setVibrate(new long[] {100,250,100,200})
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        int randomNo = 0;
        if (j > 0){
            randomNo = j;
        }

        notificationManager.notify(randomNo,builder.build());
    }
}
