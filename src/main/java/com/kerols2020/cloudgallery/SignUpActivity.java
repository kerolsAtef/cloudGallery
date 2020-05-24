package com.kerols2020.cloudgallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText email,password,confirm_password;
    TextView have_an_account;
    Button sign_up;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    String EMAIL,PASS,CON_PASS;
    FirebaseDatabase database;
    DatabaseReference reference,reference1;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog=new ProgressDialog(SignUpActivity.this);
        auth=FirebaseAuth.getInstance();
        reference=database.getInstance().getReference();
        reference1=database.getInstance().getReference();
        email=findViewById(R.id.emailSignUp);
        password=findViewById(R.id.passwordSignUp);
        confirm_password=findViewById(R.id.confirmPasswordSignUp);
        have_an_account=findViewById(R.id.haveAccount);
        sign_up=findViewById(R.id.SignUp);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkTheProcedureOfSignUp();
            }
        });
        have_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        storageReference= FirebaseStorage.getInstance().getReference();


    }

    private void checkTheProcedureOfSignUp()
    {
        PASS=password.getText().toString();
        CON_PASS=confirm_password.getText().toString();
        EMAIL=email.getText().toString();
        if (EMAIL.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"please, Enter your E-mail",Toast.LENGTH_LONG).show();
        }
        else if (PASS.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"please, Enter your pass-word",Toast.LENGTH_LONG).show();
        }
        else if (CON_PASS.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"please, Enter confirmation pass-word",Toast.LENGTH_LONG).show();
        }
        else if (!PASS.equals(CON_PASS))
        {
            Toast.makeText(getApplicationContext(),"Error, pass-words are not the same",Toast.LENGTH_LONG).show();
        }
        else if (!EMAIL.contains(".com"))
        {
            Toast.makeText(getApplicationContext(),"Error, Invalid E-mail",Toast.LENGTH_LONG).show();
        }
        else if (!EMAIL.contains("@"))
        {
            Toast.makeText(getApplicationContext(),"Error, Invalid E-mail",Toast.LENGTH_LONG).show();
        }
        else if (EMAIL.charAt(0)=='.')
        {
            Toast.makeText(getApplicationContext(),"Error, Invalid E-mail",Toast.LENGTH_LONG).show();
        }
        else
        {
            signUpToApp();
        }

    }

    void showProgressDialog()
    {
        progressDialog.setTitle("Sigup....");
        progressDialog.setMessage("please wait......");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

    }
    void disMissProgressDialogAndMove()
    {
        progressDialog.dismiss();
    }
    private void signUpToApp()
    {
        PASS=password.getText().toString();
        CON_PASS=confirm_password.getText().toString();
        EMAIL=email.getText().toString();

        showProgressDialog();
        auth.createUserWithEmailAndPassword(EMAIL,PASS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    HashMap user=new HashMap<>();
                    user .put("password",PASS);
                    user.put("email",EMAIL);
                   reference.child("user").child(auth.getCurrentUser().getUid()).setValue(user)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful())
                           {

                               HashMap image=new HashMap();
                               image.put("Link","https://firebasestorage.googleapis.com/v0/b/cloud-gallery-40fbd.appspot.com/o/user_images%2Fhello_image.jpeg?alt=media&token=5ae8d094-348b-4e76-8852-442802d77bb9");
                               image.put("Description","welcome to cloud gallery");
                               reference1.child("user").child(auth.getCurrentUser().getUid()).child("images").child("1").setValue(image)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful())
                                               {
                                                   disMissProgressDialogAndMove();
                                                   goToCoverActivity();
                                               }
                                               else
                                               {
                                                   disMissProgressDialogAndMove();
                                                   Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       });
                           }
                           else
                           {
                               disMissProgressDialogAndMove();
                              Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                           }
                       }
                   });

                }
                else
                {
                    disMissProgressDialogAndMove();
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    void goToCoverActivity()
    {
        Intent a= new Intent(SignUpActivity.this , CoverActivity.class);
        startActivity(a);
        finish();
    }

    void goToLoginActivity()
    {
        Intent a= new Intent(SignUpActivity.this , LoginActivity.class);
        startActivity(a);
    }


}
