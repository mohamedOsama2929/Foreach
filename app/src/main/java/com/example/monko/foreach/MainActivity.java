package com.example.monko.foreach;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView PostList;

    private DatabaseReference Database;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean mProcessLike=false;

    public void Go(View view) {

        startActivity(new Intent(MainActivity.this , PostActivity.class));

    }

    public void logOut(View view) {

        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

       mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        Database = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Like");
      //  String currentUserId=mAuth.getCurrentUser().getUid();

        //mDatabaseCurrentUser=FirebaseDatabase.getInstance().getReference().child("Post");
       // mQueryCurrentUser=mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);


        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        Database.keepSynced(true);

        PostList = (RecyclerView) findViewById(R.id.post_list);
        PostList.setHasFixedSize(true);
        PostList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        checksUserExist();
        mAuth.addAuthStateListener(mAuthStateListener);


        FirebaseRecyclerAdapter<Post , PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                Post.class,
                R.layout.post_row,
                PostViewHolder.class,
                Database

        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, final int position) {

                final String post_key=getRef(position).getKey();

                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext() , model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setRemoveBtn(post_key);
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_LONG).show();

                        Intent singlePostIntent = new Intent(MainActivity.this , PostSingleActivity.class);
                        singlePostIntent.putExtra("Post_Id",post_key);
                        startActivity(singlePostIntent);
                    }
                });
                viewHolder.mlikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike=true;


                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                            mProcessLike=false;
                                        } else {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");

                                            mProcessLike=false;
                                        }

                                    }
                                }

                                    @Override
                                    public void onCancelled (DatabaseError databaseError){

                                    }

                            });

                    }
                });

                viewHolder.mRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Database.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               Database.child(post_key).removeValue();
                            }

                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }

                        });

                    }
                });

            }
        };

        PostList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checksUserExist(){


        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageButton mlikeBtn;
        Button mRemove;
        DatabaseReference mDatabaseLike;
        DatabaseReference Database;
        FirebaseAuth mAuth;


        public PostViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            mlikeBtn=(ImageButton)view.findViewById(R.id.like_btn);
            mRemove=(Button)view.findViewById(R.id.remove) ;
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Like");
            Database=FirebaseDatabase.getInstance().getReference().child("Post");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);


        }
        public void setRemoveBtn(final String post_key){

            Database.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String post_uid = (String) dataSnapshot.child("uid").getValue();


                    if (mAuth.getCurrentUser() != null) {
                        if (mAuth.getCurrentUser().getUid().equals(post_uid)) {

                            mRemove.setVisibility(View.VISIBLE);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });



        }
        public void setLikeBtn(final String post_key){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (mAuth.getCurrentUser() != null) {

                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                            mlikeBtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);


                        } else {

                            mlikeBtn.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setDesc(String desc) {

            TextView post_desc = (TextView) view.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setUsername(String username) {

            TextView post_username = (TextView) view.findViewById(R.id.post_username);
            post_username.setText(username);

        }

        public void setImage(final Context context , final String image) {

            final ImageView post_image = (ImageView) view.findViewById(R.id.post_image);
            //Picasso.with(context).load(image).into(post_image);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(context).load(image).into(post_image);

                }
            });

        }
    }
}
