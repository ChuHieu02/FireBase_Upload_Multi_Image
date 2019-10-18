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
//    private int k=0;


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


                for (int i = 0; i < listNameImage.size(); i++) {

                    final StorageReference fileToUpload = mStorage.child("Images").child(listNameImage.get(i));
                    fileToUpload.putFile(listFileUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    listUriRespon.add(uri.toString());
                                    Log.e("url", uri.toString() + "\n");

                                    if (listUriRespon.size()==listNameImage.size()){
                                      newPost();
                                    }
                                }
                            });

                        }
                    });

                }


            }
        });

    }

    private void newPost() {
        Post post = new Post("linh ling", "Paris is beautiful");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Post");
        final String id = myRef.push().getKey();
        myRef.child(id).setValue(post);
        DatabaseReference myRef2 = database.getReference("Post").child(id).child("Images");
        int j = 0;
        while (j < listUriRespon.size()) {
            myRef2.push().setValue(listUriRespon.get(j));
            j++;
        }


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
                    this.listFileUri.add(fileUri);
                    final String fileName = getFileName(fileUri);
                    this.listNameImage.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();
                }

            } else if (data.getData() != null) {
                this.listFileUri.add(data.getData());
                final String fileName = getFileName(data.getData());
                this.listNameImage.add(fileName);
                uploadListAdapter.notifyDataSetChanged();

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
