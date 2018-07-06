package com.classmate.cloudtenlabs.classmate;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private TextView selectYearButton;

    private PopupMenu yearMenu, photoMenu;

    private UserObject editingUser;

    private EditText nameTextField, emailTextField, schoolTextField, majorTextField, bioTextField;

    private SwitchCompat email_showSwitch;

    private ImageView photoImageView;

    private static Context context;

    private ImageView editButton;

    private static final String IMAGE_DIRECTORY = "/classmate";
    private int GALLERY = 1, CAMERA = 2;

    private boolean photoUpdated = false;

    private int widthInDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = this;

        selectYearButton = (TextView) findViewById(R.id.selectYearButton);
        selectYearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                yearMenu.show();
            }
        });

        yearMenu = new PopupMenu(this, findViewById(R.id.selectYearButton));
        yearMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "Freshman");
        yearMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Sophomore");
        yearMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Junior");
        yearMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Senior");
        yearMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, "Grad Student");
        yearMenu.setOnMenuItemClickListener(this);

        nameTextField = (EditText) findViewById(R.id.nameTextField);
        emailTextField = (EditText) findViewById(R.id.emailTextField);
        email_showSwitch = (SwitchCompat) findViewById(R.id.email_showSwitch);
        schoolTextField = (EditText) findViewById(R.id.schoolTextField);
        majorTextField = (EditText) findViewById(R.id.majorTextField);
        bioTextField = (EditText) findViewById(R.id.bioTextField);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        editButton = (ImageView) findViewById(R.id.editButton);
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoMenu.show();
            }
        });

        photoMenu = new PopupMenu(this, findViewById(R.id.editButton));
        photoMenu.getMenu().add(Menu.NONE, 5, Menu.NONE, "Take a Photo");
        photoMenu.getMenu().add(Menu.NONE, 6, Menu.NONE, "Choose a Photo");
        photoMenu.setOnMenuItemClickListener(this);

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserObject.class) != null) {
                    GlobalVariable.getInstance().loggedInUser = dataSnapshot.getValue(UserObject.class);
                    editingUser = dataSnapshot.getValue(UserObject.class);
                    showUserData();
                } else {
                    editingUser = new UserObject();
                    photoUpdated = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView nextButton = (TextView) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editingUser.setName(nameTextField.getText().toString());
                editingUser.setEmail(emailTextField.getText().toString());
                editingUser.setEmail_show(email_showSwitch.isChecked());
                editingUser.setSchool(schoolTextField.getText().toString());
                editingUser.setMajor(majorTextField.getText().toString());
                editingUser.setYear(selectYearButton.getText().toString());
                editingUser.setBio(bioTextField.getText().toString());

                if (photoUpdated) {
                    photoImageView.setDrawingCacheEnabled(true);
                    photoImageView.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageData = byteArrayOutputStream.toByteArray();

                    FirebaseStorage.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getUid()).putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            editingUser.setPhoto(taskSnapshot.getMetadata().getPath());
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).setValue(editingUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    GlobalVariable.getInstance().loggedInUser = editingUser;
                                    Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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
                    if (dataUpdated()) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).setValue(editingUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                GlobalVariable.getInstance().loggedInUser = editingUser;
                                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                GlobalFunction.getInstance().showAlertMessage("Error", e.getLocalizedMessage(), context);
                            }
                        });
                    } else {
                        GlobalVariable.getInstance().loggedInUser = editingUser;
                        Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                }
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        LayoutParams layoutParams = photoImageView.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.widthPixels;

        photoImageView.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                selectYearButton.setTextColor(Color.BLACK);
                selectYearButton.setText("Freshman");
                break;
            case 1:
                selectYearButton.setTextColor(Color.BLACK);
                selectYearButton.setText("Sophomore");
                break;
            case 2:
                selectYearButton.setTextColor(Color.BLACK);
                selectYearButton.setText("Junior");
                break;
            case 3:
                selectYearButton.setTextColor(Color.BLACK);
                selectYearButton.setText("Senior");
                break;
            case 4:
                selectYearButton.setTextColor(Color.BLACK);
                selectYearButton.setText("Grad Student");
                break;
            case 5:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
                break;
            case 6:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
                break;
        }
        return false;
    }

    private boolean dataUpdated() {
        if (GlobalVariable.getInstance().loggedInUser == editingUser) {
            return false;
        } else {
            return true;
        }
    }

    private void showUserData() {
        if (editingUser.getYear().equals("")) {
            selectYearButton.setTextColor(Color.LTGRAY);
            selectYearButton.setText("Select Year Here");
        } else {
            selectYearButton.setTextColor(Color.BLACK);
            selectYearButton.setText(editingUser.getYear());
        }

        nameTextField.setText(editingUser.getName());
        emailTextField.setText(editingUser.getEmail());
        email_showSwitch.setChecked(editingUser.getEmail_show());
        schoolTextField.setText(editingUser.getSchool());
        majorTextField.setText(editingUser.getMajor());
        bioTextField.setText(editingUser.getBio());

        FirebaseStorage.getInstance().getReference().child(editingUser.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString())
                        .placeholder(R.drawable.image_no_avatar)
                        .error(R.drawable.image_no_avatar)
                        .into(photoImageView);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    photoImageView.setImageBitmap(bitmap);
                    photoUpdated = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            photoImageView.setImageBitmap(thumbnail);
            photoUpdated = true;
        }
    }

}