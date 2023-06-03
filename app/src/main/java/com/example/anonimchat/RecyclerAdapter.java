package com.example.anonimchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.anonimchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> groupList;

    public RecyclerAdapter(Context context, ArrayList<String> groupList) {
        this.context=context;
        this.groupList=groupList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String groups = groupList.get(position);
        if(!groups.contains("photos/")) {
            holder.groupNameTV.setText(groups);
        }
        else {
            StorageReference storage;
            storage = FirebaseStorage.getInstance().getReference();
            storage.child(groups).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e("lox",uri.toString());
                    Picasso.get().load(uri.toString()).into(holder.picture);
                }
            });
        }




    }

    @Override
    public int getItemCount()
    {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTV;
        ImageView picture;
        ViewHolder(View view) {
            super(view);
            groupNameTV = view.findViewById(R.id.message_user);
            picture = view.findViewById(R.id.Image_view);
        }
    }
}
