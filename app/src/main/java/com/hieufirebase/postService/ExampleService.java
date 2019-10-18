package com.hieufirebase.postService;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hieufirebase.R;
import com.hieufirebase.model.Post;

import java.util.ArrayList;

import static com.hieufirebase.postService.App.CHANNEL_ID;

public class ExampleService extends IntentService {
    private static final String TAG = "ExampleIntentService";
    private StorageReference mStorage;

//    private PushNotification pushNotification;
//
//    public interface  PushNotification {
//        void push(String s);
//    }
//
//    public void setPushNotification(PushNotification pushNotification) {
//        this.pushNotification = pushNotification;
//    }

    private PowerManager.WakeLock wakeLock;

    public ExampleService() {
        super("ExampleIntentService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Travel")
                    .setContentText("Posting...")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();
            Log.i(TAG, "noti");


            startForeground(1, notification);
        }


    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final ArrayList<String> listNameImage = intent.getStringArrayListExtra("listNameImage");
        final ArrayList<String> listUriRespon = intent.getStringArrayListExtra("listUriRespon");
        final ArrayList<Uri> listFileUri = intent.getParcelableArrayListExtra("listFileUri");

        for (int i = 0; i < listNameImage.size(); i++) {
            mStorage = FirebaseStorage.getInstance().getReference();

            final StorageReference fileToUpload = mStorage.child("Images").child(listNameImage.get(i));
            fileToUpload.putFile(listFileUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listUriRespon.add(uri.toString());
                            Log.e("url", uri.toString() + "\n");

                            if (listUriRespon.size() == listNameImage.size()) {
                                Post post = new Post("manh", "Paris is beautiful");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Post");
                                final String id = myRef.push().getKey();
                                myRef.child(id).setValue(post);
                                DatabaseReference myRef2 = database.getReference("Post").child(id).child("Images");
                                int j = 0;
                                while (j < listUriRespon.size()) {
                                    myRef2.push().setValue(listUriRespon.get(j));
                                            if (j==listUriRespon.size()-1){

                                            }
                                    j++;

                                }
                            }
                        }
                    });

                }
            });

        }


    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "onDestroy");
//
//    }
}

//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            try {
//                Thread.sleep(5000);
//                Log.i(TAG, "Hello");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    });
//        thread.start();