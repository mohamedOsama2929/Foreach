package com.example.monko.foreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST =1;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    private Button mSubmitBtn;
    private EditText mPostdesc;
    private Uri mimageUri=null;
    private ProgressDialog mprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //progressDialoge
        mprogress=new ProgressDialog(this);
//firebase elments
        mStorage= FirebaseStorage.getInstance().getReference();
        mDataBase= FirebaseDatabase.getInstance().getReference().child("Post");
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
        if (!TextUtils.isEmpty(desc_post)&&mimageUri!=null){

            mprogress.setMessage("loading...");
            mprogress.show();

            StorageReference filePath=mStorage.child("Post_images").child(mimageUri.getLastPathSegment());
            filePath.putFile(mimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost=mDataBase.push();
                    newPost.child("desc").setValue(desc_post);
                    newPost.child("image").setValue(downloadUrl.toString());
                    mprogress.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));

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
