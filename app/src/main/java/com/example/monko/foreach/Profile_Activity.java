package com.example.monko.foreach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Profile_Activity extends AppCompatActivity {

    private ImageView profileimage;
    private ImageView groundimage;
    private Button Gosetup;
    private Button GoMain;
    private TextView username;
    private FirebaseAuth mauthh;
    private DatabaseReference mdatabseusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        mauthh=FirebaseAuth.getInstance();
        String user_id=mauthh.getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        mdatabseusers=ref.child("users").child(user_id).child("name");
        mdatabseusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                username.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mdatabseusers=ref.child("users").child(user_id).child("image");
        mdatabseusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String imagee = dataSnapshot.getValue(String.class);
                Picasso.with(getApplicationContext()).load(imagee).networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(imagee).into(profileimage);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mdatabseusers=ref.child("users").child(user_id).child("ground");
        mdatabseusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String ground = dataSnapshot.getValue(String.class);

                Picasso.with(getApplicationContext()).load(ground).networkPolicy(NetworkPolicy.OFFLINE).into(groundimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(ground).into(groundimage);

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        profileimage=(ImageView)findViewById(R.id.imageprofilepic);
        groundimage=(ImageView)findViewById(R.id.imageground);
        username=(TextView)findViewById(R.id.textNameprof);
        Gosetup=(Button)findViewById(R.id.gosetup);
        GoMain=(Button)findViewById(R.id.button4);

        Gosetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this,SetupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        GoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}