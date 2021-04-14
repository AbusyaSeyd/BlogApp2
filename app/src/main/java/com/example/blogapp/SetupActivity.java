package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView user_profile;
    private Uri mainImageURI = null;

    private  String user_id;


    private EditText setup_username;
    private Button setupBtn;
    private ProgressBar setup_Progress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    StorageReference image_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolBar = (Toolbar) findViewById(R.id.setupToolBar);
        setSupportActionBar(setupToolBar);
        getSupportActionBar().setTitle("Account Settings");

        user_profile = (CircleImageView)findViewById(R.id.setup_image);
        setup_username = (EditText)findViewById(R.id.setup_name);
        setupBtn = (Button)findViewById(R.id.setup_btn);
        setup_Progress = (ProgressBar) findViewById(R.id.setup_progress);

        /* Firebase */


        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        // change User

        setup_Progress.setVisibility(View.VISIBLE);
       // setupBtn.setEnabled(false);


        show_logged_User();


        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Part 5 12:45  'https://www.youtube.com/watch?v=sDf7NKROoDM&list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA&index=5'

                saveToFirebase();


            }
        });



            // ImageViewButton
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageViewButton();
            }
        });



    }




    /*------------------------------------------------------------------------*/

    private void ImageViewButton() {
        // чекним версии Андроид выше Android Marshmallow 6.0.0
        // is require user running  Marshmallow higher version

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check есть ли доступ к хранилищу
            if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(SetupActivity.this, "Отказано  доступ к хранилищу", Toast.LENGTH_SHORT).show();

                // Даем доступ к хранилищу в софте (в приложениии)
                // TODO: Важно ||new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1||

                ActivityCompat.requestPermissions(SetupActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
//                        Toast.makeText(SetupActivity.this,"Доступно к хранилищу", Toast.LENGTH_SHORT).show();

                CropImagePicker();
            }

        } else {  // Version lower then M(Marshmallow)  Пример: Lollipop  || Don't working
            CropImagePicker();

        }
    }

    private void CropImagePicker() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(SetupActivity.this);
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);




        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if ( resultCode == RESULT_OK ) {
                mainImageURI = result.getUri();
         //       mainImageURI = result.getOriginalUri();
                user_profile.setImageURI(mainImageURI);

                //  cropImageView.getCroppedImageAsync();



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                String errorMessage = result.getError().getMessage(); // show error mesage
                Toast.makeText(SetupActivity.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();
                Exception error = result.getError();
            }
        }

    }




    private void saveToFirebase() {
        String username = setup_username.getText().toString();
// && mainImageURI != null
        if(!TextUtils.isEmpty(username)  ){
             user_id = firebaseAuth.getCurrentUser().getUid();

            // Done or Not
            setup_Progress.setVisibility(View.VISIBLE);


            final String randomKey = UUID.randomUUID().toString();

            user_id = randomKey;
// Create a folder
            image_path =  storageReference.child("Profile_images").child(user_id.toString()+".jpg");
//            Log.i("URL Storage: ",image_path.toString());



//image_path.getDownloadUrl(mainImageURI);

//            image_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
////                    //this is the new way to do it
////                    profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Uri> task) {
////                            String profileImageUrl=task.getResult().toString();
////                            Log.i("URL",profileImageUrl);
////                        }
////                    });
//
//                    //this is the new way to do it
//                    image_path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            String profileImageUrl=task.getResult().toString();
//                            Log.i("URL",profileImageUrl);
//                        }
//                    });
//
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SetupActivity.this, "aaa "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });


            // Returns a Uri of the form gs://bucket/path that can be used
// in future calls to getReferenceFromUrl to perform additional
// actions
//String date = mainImageURI.toString();


            image_path.putFile(mainImageURI)
//
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
////                    //this is the new way to do it
////                    profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Uri> task) {
////                            String profileImageUrl=task.getResult().toString();
////                            Log.i("URL",profileImageUrl);
////                        }
////                    });
//
//                            //this is the new way to do it
//                            image_path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    String profileImageUrl=task.getResult().toString();
//                                    Log.i("URL",profileImageUrl);
//                                }
//                            });
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SetupActivity.this, "aaa "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

//                        user_profile.setDrawingCacheEnabled(true);
//                        user_profile.buildDrawingCache();
//                        Bitmap bitmap = ((BitmapDrawable) user_profile.getDrawable()).getBitmap();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] data = baos.toByteArray();
//
//                        UploadTask uploadTask = image_path.putBytes(data);


                        // For first time. Check image is uploaded
//                       Uri download_uri = Objects.requireNonNull(task.getResult()).getUploadSessionUri();

                        // Get a non-default Storage bucket
//                        FirebaseStorage storage = FirebaseStorage.getInstance("https://console.firebase.google.com/project/blogapp-4fc8f/storage/blogapp-4fc8f.appspot.com/files/Profile_images");

//                        StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");


//                       StorageReference dateRef = storageReference.child("Profile_images").child(user_id+".jpg");



//                      Uri download_uri = task.getResult().getUploadSessionUri().toString();
/*
Uri d=
        Uri.parse(dateRef.getStream().toString());
*/
//                        Map<String, String> userMap= new HashMap<>();
//                        userMap.put("name",username);
//                        userMap.put("img",d.toString());

//String username = setup_username.getText().toString();

                        // 1) Table: Users, 2) Set user_id(Firebase) , 3) Put Array(MAP) of "userMap" values to Table("Users")
//
//                        firebaseFirestore.collection("Users").document(user_id).set(username).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    Toast.makeText(SetupActivity.this,"Аккаунт создан!" , Toast.LENGTH_LONG).show();
////                                    Toast.makeText(SetupActivity.this,image_path.getName() , Toast.LENGTH_LONG).show();
//                                    sendToMainPage();
//
//                                } else {
//                                    // Error FireBaseFireStore
//
//                                    String errorMessage = task.getException().getMessage(); // show error mesage
//                                    Toast.makeText(SetupActivity.this,"[firebaseFirestore] Error: " + errorMessage, Toast.LENGTH_LONG).show();
//
//                                }
//                            }
//                        });



//                        Toast.makeText(SetupActivity.this,"URL:" + download_uri,Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = task.getException().getMessage(); // show error mesage
                        Toast.makeText(SetupActivity.this,"Image Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        setup_Progress.setVisibility(View.INVISIBLE);

                    }


                }
            });
        }

    }

   private void show_logged_User_Image() {

       Uri file = Uri.fromFile(new File("Profile_images/" + user_id + ".jpg"));

       final   StorageReference dateRef = storageReference.child("Profile_images").child(user_id+".jpg");

       UploadTask uploadTask = dateRef.putFile(file);

       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
           @Override
           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if (!task.isSuccessful()) {
                   throw task.getException();
               }

               // Continue with the task to get the download URL
               return dateRef.getDownloadUrl();
           }
       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   Uri downloadUri = task.getResult();
               } else {
                   // Handle failures
                   // ...
               }
           }
       });

       String image = dateRef.toString();

       mainImageURI = Uri.parse(image);

       RequestOptions requestOptions = new RequestOptions();
       requestOptions.placeholder(R.drawable.default_profile);

       // For show profile img
       Glide.with(SetupActivity.this)
               .setDefaultRequestOptions(requestOptions)
               .load(image).into(user_profile);
       Toast.makeText(SetupActivity.this, "Suret" + image, Toast.LENGTH_LONG).show();
       Toast.makeText(SetupActivity.this, "IMG:" + requestOptions, Toast.LENGTH_LONG).show();
    }

    private void show_logged_User(){
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

//                        StorageReference dateRef = storageReference.child("Profile_images").child(user_id+".jpg");

                        String name = task.getResult().getString("name");
//                        String image = dateRef.toString();

                        setup_username.setText(name);
//                        mainImageURI = Uri.parse(image);
/*
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.default_profile);
*/

                        /* -------------------------------------
                        // For show profile img
                        Glide.with(SetupActivity.this)
                                .setDefaultRequestOptions(requestOptions)
                                .load(image).into(user_profile);



                         */

                        show_logged_User_Image();

//                        Toast.makeText(SetupActivity.this, "Suret" + image, Toast.LENGTH_LONG).show();
                    //    Toast.makeText(SetupActivity.this, "IMG:" + requestOptions, Toast.LENGTH_LONG).show();


                    }
//                    } else {
//                        Toast.makeText(SetupActivity.this,"Data doesn't exists",Toast.LENGTH_LONG).show();
//                    }
                } else {
                    String errorMessage = task.getException().getMessage(); // show error mesage
                    Toast.makeText(SetupActivity.this,"[Firestore Image NOT] Error: " + errorMessage, Toast.LENGTH_LONG).show();

                }

                setup_Progress.setVisibility(View.INVISIBLE);
                //    setupBtn.setEnabled(true);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.go_back, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //in Menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.back:
                sendToMainPage();
                return true;



            default:
                return false;

        }


    }

    private void sendToMainPage() {

        Intent main_intent = new Intent(SetupActivity.this,MainActivity.class);
        startActivity(main_intent);
        finish();

    }



}