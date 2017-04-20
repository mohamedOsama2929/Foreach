package com.example.monko.foreach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostSingleActivity extends AppCompatActivity {

    private String mPost_key = null;

    private DatabaseReference Database;
    private FirebaseAuth mAuth;

    private ImageView mPostSingleImage;
    private TextView mPostSingleDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        Database = FirebaseDatabase.getInstance().getReference().child("Post");

        mAuth = FirebaseAuth.getInstance();

        mPost_key = getIntent().getExtras().getString("Post_Id");

        mPostSingleImage = (ImageView) findViewById(R.id.singlePostImage);
        mPostSingleDesc = (TextView) findViewById(R.id.singlePostDesc);

        //Toast.makeText(getApplicationContext() , post_key , Toast.LENGTH_LONG).show();

        Database.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();

                mPostSingleDesc.setText(post_desc);
                Picasso.with(PostSingleActivity.this).load(post_image).into(mPostSingleImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }
}
