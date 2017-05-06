package com.example.monko.foreach;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PostSingleActivity extends AppCompatActivity {

   private Button push;
   private EditText writeComment;

    private RecyclerView commentList;

    private String mPost_key = null;

    private DatabaseReference Database;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseComment;
    private FirebaseUser mCurrentUser;
    private  DatabaseReference mDataBaseUser;
    private DatabaseReference mDatabaseCom;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ImageView mPostSingleImage;
    private TextView mPostSingleDesc;
    private Button mPostRemoveBtn;
    String comment_key="";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        mPost_key = getIntent().getExtras().getString("Post_Id");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        if (mAuth.getCurrentUser()!=null) {
            mCurrentUser = mAuth.getCurrentUser();


            push = (Button) findViewById(R.id.pushComment);
            writeComment = (EditText) findViewById(R.id.writeComment);
            push.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    createComment();

                }
            });


            Database = FirebaseDatabase.getInstance().getReference().child("Post");
            mDataBaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
            Database.keepSynced(true);

            commentList = (RecyclerView) findViewById(R.id.comment_list);
            commentList.setHasFixedSize(true);
            commentList.setLayoutManager(new LinearLayoutManager(this));


            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Like");
            mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comment");
            mDatabaseCom = mDatabaseComment.child(mPost_key);

        }




        mPostSingleImage = (ImageView) findViewById(R.id.singlePostImage);
        mPostSingleDesc = (TextView) findViewById(R.id.singlePostDesc);
        mPostRemoveBtn =(Button)findViewById(R.id.singleRemoveBtn);

        //Toast.makeText(getApplicationContext() , post_key , Toast.LENGTH_LONG).show();

        Database.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                mPostSingleDesc.setText(post_desc);

                Picasso.with(PostSingleActivity.this).load(post_image).into(mPostSingleImage);
                if (mAuth.getCurrentUser().getUid().equals(post_uid)){

                    mPostRemoveBtn.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        mPostRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database.child(mPost_key).removeValue();
                mDatabaseLike.child(mPost_key).removeValue();
                mDatabaseComment.child(mPost_key).removeValue();

                Intent mainIntent=new Intent(PostSingleActivity.this,MainActivity.class);

                startActivity(mainIntent);
            }
        });

    }
    private void createComment() {


        final String comment=writeComment.getText().toString().trim();
        if (!TextUtils.isEmpty(comment)){

                    final DatabaseReference newComment=mDatabaseCom.push();

                    mDataBaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newComment.child("comm").setValue(comment);
                            newComment.child("uid").setValue(mCurrentUser.getUid());
                            newComment.child("username" ).setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                    }else {
                                        Toast.makeText(getApplicationContext(),"errorfirebase",Toast.LENGTH_LONG);

                                    }

                                }
                            });
                            newComment.child("userimage" ).setValue(dataSnapshot.child("image").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                    }else {
                                        Toast.makeText(getApplicationContext(),"errorfirebase",Toast.LENGTH_LONG);

                                    }

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

        FirebaseRecyclerAdapter<Comment , commentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, commentViewHolder>(

                Comment.class,
                R.layout.comment_row,
                commentViewHolder.class,
                mDatabaseCom
        ) {

            @Override
            protected void populateViewHolder(commentViewHolder viewHolder, Comment model, int position) {

                final String comment_key=getRef(position).getKey();

                viewHolder.setComment(comment_key,mPost_key);
                viewHolder.setUsername(comment_key,mPost_key);
                viewHolder.setUserImage(getApplicationContext(),comment_key,mPost_key);

            }
        };

        commentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder {

        View view;
        DatabaseReference Database;
        FirebaseAuth mAuth;

        public commentViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            Database=FirebaseDatabase.getInstance().getReference().child("Comment");
            mAuth=FirebaseAuth.getInstance();
        }

        public void setComment(String comment_key,String mPost_key) {


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            DatabaseReference s =ref.child("Comment").child(mPost_key).child(comment_key).child("comm");
            s.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String comment = dataSnapshot.getValue(String.class);

                    TextView comment_desc = (TextView) view.findViewById(R.id.comComment);
                    comment_desc.setText(comment);                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }



        public void setUsername(String comment_key,String mPost_key) {



            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            DatabaseReference s =ref.child("Comment").child(mPost_key).child(comment_key).child("username");
            s.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String username = dataSnapshot.getValue(String.class);

                    TextView comment_desc = (TextView) view.findViewById(R.id.comUsername);
                    comment_desc.setText(username);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void setUserImage(final Context c,String comment_key,String post_key) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            DatabaseReference s =ref.child("Comment").child(post_key).child(comment_key).child("userimage");
            s.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String imagee = dataSnapshot.getValue(String.class);

                    final ImageView comment_userImage = (ImageView) view.findViewById(R.id.comImage);

                    Picasso.with(c).load(imagee).networkPolicy(NetworkPolicy.OFFLINE).into(comment_userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(c).load(imagee).into(comment_userImage);

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