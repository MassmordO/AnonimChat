package com.example.anonimchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Stream;

public class Chat extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    FloatingActionButton fab;
    EditText editText;
    ListView massages;
    DatabaseReference database;
    DatabaseReference database2;
    DatabaseReference database3;
    ArrayList<String> massagesList;
    int i;
    String name;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FloatingActionButton fab =findViewById(R.id.fab);
        editText = findViewById(R.id.input);
        database = FirebaseDatabase.getInstance().getReference("Rooms");
        massages = findViewById(R.id.list_of_messages);
        massagesList = new ArrayList<String>();
        adapter =  new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, massagesList);

        massages.setAdapter(adapter);
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
         //getMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i==1){
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
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
//    private void displayChat() {
//
//        ListView listMessages = (ListView)findViewById(R.id.list_of_messages);
//        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
//                .setLayout(R.layout.chat_maket)
//                .setQuery(database.child("messages"), ChatMessage.class)
//                .build();
//        adapter = new FirebaseListAdapter<ChatMessage>(options) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//
//                TextView textMessage;
//                textMessage = (TextView)v.findViewById(R.id.message_text);
//
//                textMessage.setText(model.getMessageText());
//            }
//        };
//        listMessages.setAdapter(adapter);
//    }
    private void getMessages(){
        ValueEventListener chelListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                massagesList.removeAll(massagesList);
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
}

