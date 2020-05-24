package com.kerols2020.cloudgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ShowImageActivity extends AppCompatActivity {

    String imageKey;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ImageView image,download;
    TextView nameOfImage;
    AlertDialog.Builder alert;
    StorageReference storageReference;
    String ImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageKey=getIntent().getExtras().get("key").toString();
        ImageUrl=getIntent().getExtras().get("url").toString();
        auth=FirebaseAuth.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        reference=database.getInstance().getReference().child("user")
                .child(auth.getCurrentUser().getUid()).child("images").child(imageKey);

        loadData();
        image=findViewById(R.id.show_image);
        nameOfImage=findViewById(R.id.show_image_name);
        download=findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert = new AlertDialog.Builder(ShowImageActivity.this);
                alert.setMessage("Download image");
                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadImage();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });


    }

    private void DownloadImage()
    {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Link"))
                {
                    Uri uri=Uri.parse(dataSnapshot.child("Link").getValue().toString());
                    String NOI= nameOfImage.getText().toString(); // NOI is name of image
                    downloadOnPhone(ShowImageActivity.this,NOI,".jpg",DIRECTORY_DOWNLOADS,uri);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void downloadOnPhone(Context context ,String imageName,String imageExtension,
                                  String destinationDirectory,Uri uri)
    {
        DownloadManager downloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,imageName+imageExtension);
        downloadManager.enqueue(request);

    }

    private void loadData()
    {
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild("Description")&&dataSnapshot.hasChild("Link"))
            {
                String name=dataSnapshot.child("Description").getValue().toString();
                String Url=dataSnapshot.child("Link").getValue().toString();
                nameOfImage.setText(name);
                Picasso.get().load(Url).into(image);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

}
