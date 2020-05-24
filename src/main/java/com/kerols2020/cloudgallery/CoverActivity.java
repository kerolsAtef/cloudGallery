package com.kerols2020.cloudgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class CoverActivity extends AppCompatActivity {

    RelativeLayout relative;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        relative=findViewById(R.id.LINEAR);
        auth=FirebaseAuth.getInstance();
        relative.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                moveToMainActivity();
            }
        }
        );
    }

    private void moveToMainActivity()
    {
    startActivity(new Intent(CoverActivity.this,MainActivity.class));
    finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser()==null)
            goToSignupActivity();

    }
    private void goToSignupActivity()
    {

        startActivity(new Intent(CoverActivity.this,SignUpActivity.class));
        finish();

    }

}
