package com.madein.thebox;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Profile extends AppCompatActivity {

    private Button btnLogOut,btnUpload;
    private ImageView imgProfile;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        btnLogOut=findViewById(R.id.btnLogOut);
        btnUpload=findViewById(R.id.btnUploadImage);
        imgProfile=findViewById(R.id.profile_img);

        Toast.makeText(Profile.this,"Tap on your current dp to select new one",Toast.LENGTH_LONG).show();
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();

            }
        });








        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//---------------------------------------#######
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));//so we cant go back
                finish();
            }
        });



        imgProfile.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view){
                Intent photoIntent=new Intent(Intent.ACTION_PICK);//it will pick something
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);//to handle this result we are overiding onActivityResult fn
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode== RESULT_OK && data!= null){
            imagePath=data.getData();  //its return type is URI .its basically a path to file in our device
            getImageInImageView();//defined later
        }
    }



    private void getImageInImageView(){
        Bitmap bitmap=null;
        try {   /// to prevent app from crashing
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        imgProfile.setImageBitmap(bitmap);
    }

    //code for uploading images
    private void uploadImage(){
        ProgressDialog progressDialog=new ProgressDialog(this); //to show completion
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {//with random strings for uniqueness
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    //for linking image with user
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());//url in arg
                            }
                        }
                    });
                    Toast.makeText(Profile.this,"Image Uploaded!:)",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Profile.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress= 100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded "+(int) progress+"%" );
            }
        });

    }



    private void updateProfilePicture( String url){
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue(url);
    }
}