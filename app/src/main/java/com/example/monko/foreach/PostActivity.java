package com.example.monko.foreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST =1;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    private Button mSubmitBtn;
    private EditText mPostdesc;
    private Uri mimageUri=null;

    private ProgressDialog mprogress;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private  DatabaseReference mDataBaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();


        //progressDialoge
        mprogress=new ProgressDialog(this);
//firebase elments
        mStorage= FirebaseStorage.getInstance().getReference();
        mDataBase= FirebaseDatabase.getInstance().getReference().child("Post");
        mDataBaseUser=FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
//-------------
        mSelectImage=(ImageButton)findViewById(R.id.imageSelect);
        mSubmitBtn=(Button)findViewById(R.id.submitBtn);
        mPostdesc=(EditText)findViewById(R.id.desc);

//gallery intent call
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
//---------------

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPost();

            }
        });

    }

    //upload the data to firebase
    private void createPost() {


        final String desc_post=mPostdesc.getText().toString().trim();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        final String datepost = df.format(Calendar.getInstance().getTime());

        if (!TextUtils.isEmpty(desc_post)&&mimageUri!=null){

            mprogress.setMessage("loading...");
            mprogress.show();

            StorageReference filePath=mStorage.child("Post_images").child(mimageUri.getLastPathSegment());
            filePath.putFile(mimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final  Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost=mDataBase.push();

                    mDataBaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("desc").setValue(desc_post);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("likes").setValue(0);
                            newPost.child("date").setValue(datepost);
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username" ).setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }else {
                                        Toast.makeText(getApplicationContext(),"errorfirebase",Toast.LENGTH_LONG);

                                    }

                                }
                            });
                            newPost.child("userimage" ).setValue(dataSnapshot.child("image").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
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
                    mprogress.dismiss();
                }
            });

        }


    }
    //-------------
    //gallery intent result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            mimageUri=data.getData();
            mSelectImage.setImageURI(mimageUri);


        }
    }

}
