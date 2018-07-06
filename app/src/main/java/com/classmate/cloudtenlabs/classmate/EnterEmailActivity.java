package com.classmate.cloudtenlabs.classmate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;

public class EnterEmailActivity extends AppCompatActivity {

    private EditText emailTextField;

    private FirebaseAuth mAuth;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enteremail);

        context = this;

        emailTextField = (EditText) findViewById(R.id.emailTextField);

        TextView nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailTextField.getText().toString();
                if (email.equals("")) {
                    GlobalFunction.getInstance().showAlertMessage("Error", "Please enter email address", context);
                    return;
                }

                FirebaseAuth.getInstance().fetchProvidersForEmail(email).addOnSuccessListener(new OnSuccessListener<ProviderQueryResult>() {
                    @Override
                    public void onSuccess(ProviderQueryResult providerQueryResult) {
                        if (providerQueryResult.getProviders().size() == 0) {
                            Intent intent = new Intent(EnterEmailActivity.this, PasswordActivity.class);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("SCREEN_TYPE", "SignUp");
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        } else {
                            Intent intent = new Intent(EnterEmailActivity.this, PasswordActivity.class);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("SCREEN_TYPE", "SignIn");
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                    }
                });
            }
        });
    }

}
