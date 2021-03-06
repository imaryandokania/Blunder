package com.app.blunder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import de.hdodenhof.circleimageview.CircleImageView;


public class DetailsActivity extends AppCompatActivity {
    private AdView mAdView;
    private Button UpdateAccountSettings;
    private EditText userdob,userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private static final int GalleryPic=1;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingbar;
    private String photoUrl = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        InitilizeFields();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        currentUserID= mAuth.getCurrentUser().getUid();
        Rootref= FirebaseDatabase.getInstance().getReference();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener()
                              {
                                  @Override
                                  public void onAdFailedToLoad(int errorCode) {
                                      // Code to be executed when an ad request fails.
                                      Toast.makeText(DetailsActivity.this, "Ad failed: " + errorCode, Toast.LENGTH_SHORT).show();
                                  }
                              }
        );
        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryintent =new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GalleryPic);
            }
        });

    }
    @Override
    public void onBackPressed() {
        sendUserToMainActivity();
        super.onBackPressed();
    }

    private void InitilizeFields() {
        UpdateAccountSettings=(Button)findViewById(R.id.button3);
        userdob=(EditText)findViewById(R.id.dob);
        userStatus=(EditText)findViewById(R.id.name);
        userProfileImage=(CircleImageView)findViewById(R.id.set_profile_image);
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar=new ProgressDialog(DetailsActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri=data.getData();
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(DetailsActivity.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Uploading");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();
                                        photoUrl=downloadUrl;
                                        Rootref.child("Users").child(currentUserID).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Picasso.get().load(downloadUrl).into(userProfileImage);
                                                            Toast.makeText(DetailsActivity.this, "Image saved in database successfuly", Toast.LENGTH_SHORT).show();
                                                            loadingbar.dismiss();
                                                        }
                                                        else{
                                                            String message = task.getException().toString();
                                                            Toast.makeText(DetailsActivity.this, "Error: " + message,Toast.LENGTH_SHORT).show();
                                                            loadingbar.dismiss();

                                                        }

                                                    }
                                                });

                                    }
                                });

                            }
                        });


            }
        }


    }



    private void UpdateSettings() {
        String setUserdob=userdob.getText().toString();
        String setUserName=userStatus.getText().toString();
        if(TextUtils.isEmpty(setUserdob))
        {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Enter your dob ", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("user_name",setUserName);
            profileMap.put("dob",setUserdob);
            if(photoUrl!=" ")
            {
                profileMap.put("image", photoUrl);
            }

            Rootref.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(DetailsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(DetailsActivity.this,"Error"+message, Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();

                    }
                }
            });
        }

    }
    private void RetrieveUserInfo() {
        Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("user_name") && (dataSnapshot.hasChild("image"))))
                {
                    String retriveUserdob =dataSnapshot.child("dob").getValue().toString();
                    String retriveStatus =dataSnapshot.child("user_name").getValue().toString();
                    String retriveProfileImage =dataSnapshot.child("image").getValue().toString();
                    userdob.setText(retriveUserdob);
                    userStatus.setText(retriveStatus);
                    Picasso.get().load(retriveProfileImage).into(userProfileImage);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("user_name")))
                {
                    String retriveUserdob =dataSnapshot.child("dob").getValue().toString();
                    String retriveStatus =dataSnapshot.child("user_name").getValue().toString();
                    userdob.setText(retriveUserdob);
                    userStatus.setText(retriveStatus);

                }
                else
                {
                    Toast.makeText(DetailsActivity.this, "Please set", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(DetailsActivity.this,DashBoard.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  //user cant use back button.
        startActivity(mainIntent);
        finish();

    }

}