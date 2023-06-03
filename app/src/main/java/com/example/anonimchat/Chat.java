package com.example.anonimchat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Stream;

public class Chat extends AppCompatActivity {

    RecyclerAdapter adapter;
    FloatingActionButton fab;
    EditText editText;
    DatabaseReference database;
    DatabaseReference database2;
    DatabaseReference database3;
    StorageReference st;
    RecyclerView recyclerView;
    private Uri imageUri;
    ArrayList<String> massagesList;
    int i;
    String name;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fab =findViewById(R.id.fab);
        editText = findViewById(R.id.input);
        database = FirebaseDatabase.getInstance().getReference("Rooms");
        st = FirebaseStorage.getInstance().getReference("photos/");
        recyclerView = findViewById(R.id.recycle_view);
        massagesList = new ArrayList<String>();
        adapter = new RecyclerAdapter(this,massagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
         i = getIntent().getIntExtra("room",0);
         if(i==1) {
             database2 = FirebaseDatabase.getInstance().getReference("Rooms").child("room1").child("messages");
             database3 = FirebaseDatabase.getInstance().getReference("Rooms").child("room1").child("Users");
         }
         else if(i==2){
             database2 = FirebaseDatabase.getInstance().getReference("Rooms").child("room2").child("messages");
             database3 = FirebaseDatabase.getInstance().getReference("Rooms").child("room2").child("Users");
         }
         name = getIntent().getStringExtra("user");
         key = getIntent().getStringExtra("key");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i==1){
                database.child("room1").child("messages")
                        .push()
                        .setValue( name+": "+ editText.getText().toString());

                    getMessages();
                }
                else if(i==2){
                    database.child("room2").child("messages")
                            .push()
                            .setValue(name+": "+editText.getText().toString());

                    getMessages();
                }
                editText.setText("");
            }
        });

    }
    private void getMessages(){
        ValueEventListener chelListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                massagesList.removeAll(massagesList);
                recyclerView.removeAllViews();
                if(i == 1){
                for(DataSnapshot ds: dataSnapshot.getChildren()) {

                        massagesList.add(ds.getValue(String.class));
                }
                }else if(i==2) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        massagesList.add(ds.getValue(String.class));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error",databaseError.getMessage());
            }
        };
        if(i==1) {
            database2.addValueEventListener(chelListener);
        }
        else if(i==2){
            database2.addValueEventListener(chelListener);
        }
    }
    public void deleteUserOrRoom(){

        database3.child(key).removeValue();
        if(i==1) {
           database.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (!snapshot.child("room1").hasChild("Users")) database.child("room1").removeValue();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

        }
        else if(i==2){
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("room2").hasChild("Users")) database.child("room2").removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    public void finish() {
        deleteUserOrRoom();

        super.finish();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }





    public void openFileManager(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mainActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> mainActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                imageUri = result.getData().getData();
                Log.e("SELECTEDIMAGE", imageUri.toString());
                UploadFile();
            }
        }
    });

    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return  typeMap.getExtensionFromMimeType(resolver.getType(uri));
    }


    private void UploadFile(){
        String filename= System.currentTimeMillis() +"."+ getFileExtension(imageUri);
        StorageReference fileReference = st.child(filename);

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(i==1){
                    database.child("room1").child("messages")
                            .push()
                            .setValue("photos/"+filename);

                    getMessages();
                }
                else if(i==2){
                    database.child("room2").child("messages")
                            .push()
                            .setValue("photos/"+filename);

                    getMessages();
                }
            }
        });
    }
}

