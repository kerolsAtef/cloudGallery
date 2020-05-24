package com.kerols2020.cloudgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {

    RecyclerView list_of_images;
    TextView number_of_images;
    CircleButton add_image, change_password,logOut;
    FirebaseAuth auth;
    DatabaseReference reference,reference1;
    FirebaseDatabase database;
    StorageReference storageReference;
    AlertDialog.Builder alert;
    EditText search_text;
    FirebaseRecyclerOptions<carDetails>options;
    FirebaseRecyclerAdapter<carDetails,MyViewHolder>adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth=FirebaseAuth.getInstance();
        reference=database.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());
        reference1=database.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid()).child("images");
        storageReference= FirebaseStorage.getInstance().getReference().child("user_images/");
        list_of_images=findViewById(R.id.ListOfImages);
        number_of_images=findViewById(R.id.number_of_images);
        add_image=findViewById(R.id.addImage);
        change_password =findViewById(R.id.change_password);
        logOut=findViewById(R.id.signOut);
        SetNumberOfImages();
        loadData("");
        search_text=findViewById(R.id.searchText);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToAddImageActivity();
            }
        });
        list_of_images.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        list_of_images.setLayoutManager(linearLayoutManager);

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString()==null)
                {
                   loadData("");
                }
                else
                {
                  loadData(s.toString());
                }
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                finish();

            }
        });
    }

    private void changePassword()
    {
        alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("change password");
        final EditText pass  = new EditText(MainActivity.this);
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pass.setHint("Enter new password");

        alert.setView(pass);
        alert.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passWord=pass.getText().toString();
                reference.child("password")
                        .setValue(passWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "password changed successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Error,please try again", Toast.LENGTH_SHORT).show();
                    }

                });



            }
        });
        alert.show();

    }

    private void loadData(String data) {
            Query query = reference1.orderByChild("Description").startAt(data).endAt(data + "\uf8ff");
            options = new FirebaseRecyclerOptions.Builder<carDetails>().setQuery(query, carDetails.class).build();
            adapter = new FirebaseRecyclerAdapter<carDetails, MyViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final carDetails car) {
                    final String imageKey=getRef(position).getKey();
                    final String linkUrl=car.getLink();
                    holder.name.setText(car.getDescription());
                    Picasso.get().load(car.getLink()).into(holder.image);

                        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (!car.getLink().equals("https://firebasestorage.googleapis.com/v0/b/cloud-gallery-40fbd.appspot.com/o/user_" +
                                        "images%2Fhello_image.jpeg?alt=media&token=5ae8d094-348b-4e76-8852-442802d77bb9"))
                                {alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setMessage("Do you want to delete this image ? ");
                                alert.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteImage(imageKey);
                                    }
                                });
                                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alert.show();

                                }
                                return true;

                            }
                        });

                        holder.image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!car.getLink().equals("https://firebasestorage.googleapis.com/v0/b/cloud-gallery-40fbd.appspot.com/o/user_" +
                                        "images%2Fhello_image.jpeg?alt=media&token=5ae8d094-348b-4e76-8852-442802d77bb9"))
                                {
                                    Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
                                    intent.putExtra("key", imageKey);
                                    intent.putExtra("url", linkUrl);
                                    startActivity(intent);
                                }
                            }
                        });
                    }


                @NonNull
                @Override
                public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_row, parent, false);

                    return new MyViewHolder(view);

                }
            };
            adapter.startListening();
            list_of_images.setAdapter(adapter);
        }


    private void SetNumberOfImages()
    {

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int n = (int) dataSnapshot.getChildrenCount();
                    number_of_images.setText(" " + n);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    private void GoToAddImageActivity()
    {
    startActivity(new Intent(MainActivity.this,AddImageActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }



    ////////////////////////////////////////////////////

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());

    }

private void DeleteImage(String K)
    {
     reference1.child(K).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


             if(dataSnapshot.exists())
             {
                 String ImageURL=dataSnapshot.child("Link").getValue().toString();
                 StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(ImageURL);
                 storageReference.delete();

             }


         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });
        reference1.child(K).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getApplicationContext(),"تم الحذف بنجاح .", Toast.LENGTH_LONG).show();
            }
        });

    }

}
