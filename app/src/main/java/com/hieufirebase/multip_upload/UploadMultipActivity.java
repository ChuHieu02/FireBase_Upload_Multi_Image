package com.hieufirebase.multip_upload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hieufirebase.R;
import com.hieufirebase.model.Post;

import java.util.ArrayList;
import java.util.List;

public class UploadMultipActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton mSelectBtn;
    private RecyclerView mUploadList;

    private List<String> listPathFile = new ArrayList<>();
    private List<String> listUriRespon = new ArrayList<>();

    private List<String> listNameImage = new ArrayList<>();
    private List<Uri> listFileUri = new ArrayList<Uri>();

    private UploadListAdapter uploadListAdapter;

    private StorageReference mStorage;

    private Button btnUpload;

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_multip);

        mapping();
        mStorage = FirebaseStorage.getInstance().getReference();

        //RecyclerView

        mUploadList.setHasFixedSize(true);
        mUploadList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        uploadListAdapter = new UploadListAdapter(listPathFile, this);
        mUploadList.setAdapter(uploadListAdapter);


        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (int i = 0; i < listNameImage.size(); i++) {
//
//                    final StorageReference fileToUpload = mStorage.child("Images").child(listNameImage.get(i));
//
//                    fileToUpload.putFile(listFileUri.get(i));
//
//                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            listUriRespon.add(uri.toString());
//                            Log.e("url", uri.toString() + "\n");
//                        }
//                    });
//                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("Post");
                Post post = new Post("linh linh", "bali that dep", listUriRespon);
                databaseReference.push().setValue(post);
            }
        });

    }

    private void mapping() {
        btnUpload = (Button) findViewById(R.id.btn_upload);
        mTextView = (TextView) findViewById(R.id.mTextView);

        mSelectBtn = (ImageButton) findViewById(R.id.select_btn);
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    listFileUri.add(fileUri);

                    final String fileName = getFileName(fileUri);
                    listNameImage.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();
                    Log.e("data", data.getData() + "");

//                    /*add  nhanh images firebase store*/
//                    final StorageReference fileToUpload = mStorage.child("Images").child(fileName);
//                    /* push image len nhanh images*/
//                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    listUriRespon.add(uri.toString());
//                                    Log.e("url",uri.toString()+"\n");
//                                }
//                            });
//                        }
//                    });

                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null) {
                listFileUri.add(data.getData());
                final String fileName = getFileName(data.getData());

                uploadListAdapter.notifyDataSetChanged();


//                Toast.makeText(this, "Selected Single File", Toast.LENGTH_SHORT).show();
                Log.e("data", data.getData() + "");

            }

        }

    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    listPathFile.add(data);
//                    Log.e("path", data + "\n");
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}
