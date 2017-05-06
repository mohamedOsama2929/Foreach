package com.example.monko.foreach;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private DatabaseReference mDatabaseCounterLike;
    private Query mQueryPosts;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean mProcessLike=false;
    private int counter;

    SwipeRefreshLayout mSwipeRefreshLayout;

    public void Go(View view) {

        Intent In = new Intent(MainActivity.this, PostActivity.class);
        In.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(In);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
            }
        });

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

        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        Database.keepSynced(true);

        PostList = (RecyclerView) findViewById(R.id.post_list);
        PostList.setHasFixedSize(true);
        PostList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profileBtn){

            Intent Intent = new Intent(MainActivity.this, Profile_Activity.class);
            Intent.putExtra("user_id",mAuth.getCurrentUser().getUid());
            Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Intent);

        }
        else if (item.getItemId() == R.id.logoutBtn){

            mAuth.signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
        else {

            Intent In = new Intent(MainActivity.this, RoomsActivity.class);
            In.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(In);

        }

        return super.onOptionsItemSelected(item);
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
                Database.orderByChild("likes")

        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, final int position) {

                final String post_key=getRef(position).getKey();

                DatabaseReference likes=Database.child(post_key).child("likes");
                likes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        counter =  dataSnapshot.getValue(Integer.class);
                        viewHolder.setCounter(String.valueOf(counter));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                viewHolder.setDesc(model.getDesc());
                viewHolder.setDate(model.getDate());
                viewHolder.setImage(getApplicationContext() , model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUserImage(getApplicationContext(),post_key);
                viewHolder.setLikeBtn(post_key);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent singlePostIntent = new Intent(MainActivity.this , PostSingleActivity.class);
                        singlePostIntent.putExtra("Post_Id",post_key);

                        singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(singlePostIntent);
                    }
                });

                viewHolder.mUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference likes=Database.child(post_key).child("uid");
                        likes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String use =  dataSnapshot.getValue(String.class);
                                Intent Intent = new Intent(MainActivity.this, Profile_Activity.class);
                                Intent.putExtra("user_id",use);
                                Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(Intent);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.mlikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                        DatabaseReference likes=Database.child(post_key).child("likes");
                        likes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter =  dataSnapshot.getValue(Integer.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike) {

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        counter--;
                                        //viewHolder.setLikesCount(counter);
                                        Database.child(post_key).child("likes").setValue(counter);

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike = false;

                                    } else {

                                        counter++;
                                        //viewHolder.setLikesCount(counter);

                                        Database.child(post_key).child("likes").setValue(counter);

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("random value");

                                        mProcessLike = false;
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                });
            }
        };

        PostList.setAdapter(firebaseRecyclerAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
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
        ImageView mUserImage;
        DatabaseReference mDatabaseLike;
        DatabaseReference Database;
        FirebaseAuth mAuth;


        public PostViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            mlikeBtn=(ImageButton)view.findViewById(R.id.like_btn);

            mUserImage=(ImageView)view.findViewById(R.id.user_Image);

            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Like");
            Database=FirebaseDatabase.getInstance().getReference().child("Post");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
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
        public void setDate(String date){

            TextView post_date=(TextView)view.findViewById(R.id.textDate);
            post_date.setText(date);
        }

        public void setCounter(String counter) {

            TextView post_like = (TextView) view.findViewById(R.id.likesCount);
            post_like.setText(counter);
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

        public void setUserImage(final Context c,String post_key) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            DatabaseReference s =ref.child("Post").child(post_key).child("userimage");
            s.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String imagee = dataSnapshot.getValue(String.class);

                    final ImageView post_userImage = (ImageView) view.findViewById(R.id.user_Image);

                    Picasso.with(c).load(imagee).networkPolicy(NetworkPolicy.OFFLINE).into(post_userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(c).load(imagee).into(post_userImage);

                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
