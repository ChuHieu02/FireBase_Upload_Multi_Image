package com.hieufirebase.multip_upload;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hieufirebase.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by akshayejh on 19/12/17.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{
    public List<String> fileDoneList;
    private Context context;

    public UploadListAdapter(List<String>fileDoneList , Context context){

        this.fileDoneList = fileDoneList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context).load(new File(fileDoneList.get(position))).into(holder.uploadIcon);

    }

    @Override
    public int getItemCount() {
        return fileDoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private ImageView uploadIcon;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            uploadIcon = itemView.findViewById(R.id.upload_icon);



        }

    }

}
