package com.classmate.cloudtenlabs.classmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private TextView nextButton, resendButton, forgotButton, termsButton;

    private String email, screen_type;

    private EditText loginPasswordTextField, registerPasswordTextField, confirmPasswordTextField;

    private LinearLayout loginLayout, registerLayout, verificationLayout;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        email = getIntent().getStringExtra("EMAIL");
        screen_type = getIntent().getStringExtra("SCREEN_TYPE");
        context = this;

        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        loginPasswordTextField = (EditText) findViewById(R.id.loginPasswordTextField);
        registerPasswordTextField = (EditText) findViewById(R.id.registerPasswordTextField);
        confirmPasswordTextField = (EditText) findViewById(R.id.confirmPasswordTextField);

        nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen_type.equals("SignUp")) {
                    String password = registerPasswordTextField.getText().toString();
                    String confirm = confirmPasswordTextField.getText().toString();

                    if (password.equals("")) {
                        GlobalFunction.getInstance().showAlertMessage("Error", "Please enter password", context);
                        return;
                    }

                    if (!password.equals(confirm)) {
                        GlobalFunction.getInstance().showAlertMessage("Error", "Password does not match the confirm password", context);
                        return;
                    }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            nextButton.setVisibility(View.GONE);

                            loginLayout.setVisibility(View.GONE);
                            registerLayout.setVisibility(View.GONE);
                            verificationLayout.setVisibility(View.VISIBLE);

                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkVerificationStatus();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                        }
                    });
                } else {
                    String password = loginPasswordTextField.getText().toString();

                    if (password.equals("")) {
                        GlobalFunction.getInstance().showAlertMessage("Error", "Please enter password", context);
                        return;
                    }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(PasswordActivity.this, EditProfileActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                            } else {
                                nextButton.setVisibility(View.GONE);

                                loginLayout.setVisibility(View.GONE);
                                registerLayout.setVisibility(View.GONE);
                                verificationLayout.setVisibility(View.VISIBLE);

                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        checkVerificationStatus();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                        }
                    });
                }
            }
        });

        resendButton = (TextView) findViewById(R.id.resendButton);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkVerificationStatus();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                    }
                });
            }
        });

        forgotButton = (TextView) findViewById(R.id.forgotButton);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialg = new AlertDialog.Builder(context);
                alertDialg.setMessage("Are you sure you want to reset this password?");
                alertDialg.setTitle("");
                alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                GlobalFunction.getInstance().showAlertMessage("Check Your Email", "A password reset email has been sent to you. Click the link in the email to reset your password.", context);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                            }
                        });
                    }
                });
                alertDialg.setNegativeButton("No", null);
                alertDialg.create().show();
            }
        });

        termsButton = (TextView) findViewById(R.id.termsButton);
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);

        if (screen_type.equals("SignIn")) {
            loginLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
            verificationLayout.setVisibility(View.GONE);
        } else if (screen_type.equals("SignUp")) {
            loginLayout.setVisibility(View.GONE);
            registerLayout.setVisibility(View.VISIBLE);
            verificationLayout.setVisibility(View.GONE);
        }
    }

    private void checkVerificationStatus() {
        String password = screen_type.equals("SignIn") ? loginPasswordTextField.getText().toString() : registerPasswordTextField.getText().toString();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    Intent intent = new Intent(PasswordActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                } else {
                    checkVerificationStatus();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                checkVerificationStatus();
            }
        });
    }



}
