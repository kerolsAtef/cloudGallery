package com.kerols2020.cloudgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AddImageActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Uri uri;
    StorageReference storageReference;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference databaseReference,reference1;
    ImageView image,addImage;
    EditText description;
    Button save;
    String DESCRIPTION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        progressDialog=new ProgressDialog(AddImageActivity.this);
        auth=FirebaseAuth.getInstance();
        databaseReference=database.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid()).child("images");
        reference1= database.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid()).child("images");
        storageReference=FirebaseStorage.getInstance().getReference().child("user_images/");
        image=findViewById(R.id.addedImage);
        addImage=findViewById(R.id.add_image_from_gallery);
        description=findViewById(R.id.addedDescription);
        save=findViewById(R.id.save);
        openGallery();

            addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreImageAndDescription();
            }
        });
    }

    private void openGallery()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(AddImageActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                uri=result.getUri();
                image.setVisibility(View.VISIBLE);
                Picasso.get().load(result.getUri()).into(image);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
            else if (image.getDrawable()==null)
            {
                goToMainActivity();
            }
        }


    }


    private void StoreImageAndDescription()
    {
        showProgressDialog();
        DESCRIPTION=description.getText().toString();
        storageReference.child(auth.getCurrentUser().getUid()+uri.getLastPathSegment())
                .putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             storageReference.child(auth.getCurrentUser().getUid()+uri.getLastPathSegment())
                     .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     final String downloadedURL = uri.toString();
                     reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             int n=(int) dataSnapshot.getChildrenCount();
                             HashMap image=new HashMap();
                             image.put("Link",downloadedURL);
                             image.put("Description",DESCRIPTION);
                             reference1.child(String.valueOf(n+1)).setValue(image).
                                     addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful())
                                     {
                                         disMissProgressDialog();
                                         goToMainActivity();
                                         Toast.makeText(getApplicationContext(),"image saved Successfully",Toast.LENGTH_LONG).show();
                                     }
                                     else
                                         {
                                         Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                         disMissProgressDialog();
                                         }
                                 }
                             });
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });

                 }
             });
            }
        });
    }

    private void goToMainActivity()
    {

        startActivity(new Intent(AddImageActivity.this,MainActivity.class));
        finish();
    }
    void showProgressDialog()
    {
        progressDialog.setTitle("Save image....");
        progressDialog.setMessage("please wait......");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

    }
    void disMissProgressDialog()

    {
        progressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
