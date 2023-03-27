package com.example.anonimchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText name;
    Button login;
    private  DatabaseReference database;
    private static DatabaseReference dt2;
    private static DatabaseReference dt3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.editTextName);
        login = findViewById(R.id.logIn);
        database = FirebaseDatabase.getInstance().getReference("Rooms");
        dt2 = FirebaseDatabase.getInstance().getReference("Rooms").child("room1").child("Users");
        dt3 = FirebaseDatabase.getInstance().getReference("Rooms").child("room2").child("Users");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int count = random.nextInt(2) + 1;
                switch (count){
                    case 1:

                        String key = dt2.push().getKey();
                        dt2.child(key).setValue(name.getText().toString());
                        //database.child("room1").child("messages").setValue("Вы начали чат");
                        Intent intent = new Intent(MainActivity.this,Chat.class);
                        intent.putExtra("room",1);
                        intent.putExtra("user",name.getText().toString());
                        intent.putExtra("key",key);
                        startActivity(intent);

                        break;
                    case 2 :
                        String key2 = dt3.push().getKey();
                        dt3.child(key2).setValue(name.getText().toString());
                        //database.child("room2").child("messages").setValue("Вы начали чат");
                        Intent intent2 = new Intent(MainActivity.this,Chat.class);
                        intent2.putExtra("room",2);
                        intent2.putExtra("user",name.getText().toString());
                        intent2.putExtra("key",key2);
                        startActivity(intent2);

                        break;
                }

            }

        });
    }
}