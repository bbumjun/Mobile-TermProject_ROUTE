package com.termproject.route.route;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SharingActivity extends AppCompatActivity {
float x;
int GALLERY_CODE=10;
private String selectedImagePath;
FirebaseStorage storage = FirebaseStorage.getInstance("gs://routetermproject-f7baa.appspot.com/");


    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    StorageReference imageRef = storageRef.child("images");
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        addButton =(Button)findViewById(R.id.addBtn);

        StorageReference imageRef1 = storageRef.child("images/image1.jpg");
        StorageReference imageRef2 = storageRef.child("images/image2.jpg");

        ImageView imageView1 =(ImageView)findViewById(R.id.imageView1);
        ImageView imageView2 =(ImageView)findViewById(R.id.imageView2);

        Glide.with(this).using(new FirebaseImageLoader()).load(imageRef1).into(imageView1);
        Glide.with(this).using(new FirebaseImageLoader()).load(imageRef2).into(imageView2);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // select a picture in gallery
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);
            }
        });
    }
protected  void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK) {
            if(requestCode==GALLERY_CODE) {
                Uri selectedImageUri =data.getData();
                selectedImagePath=getPath(selectedImageUri);
                Toast.makeText(getApplicationContext(), "Image Path = "+selectedImagePath, Toast.LENGTH_SHORT).show();
            }
        }
    Uri file = Uri.fromFile(new File(selectedImagePath));
    StorageReference uploadRef = storageRef.child("images/"+file.getLastPathSegment());
    UploadTask uploadTask = uploadRef.putFile(file);
// Register observers to listen for when the download is done or if it fails
    uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
        }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Toast.makeText(getApplicationContext(),"upload success",Toast.LENGTH_SHORT).show();
        }
    });
}

    public String getPath(Uri uri) {

        if( uri == null ) {
            return null;
        }
        // receive uri that chosen picture
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // return uri path
        return uri.getPath();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            x=event.getX();
        }
        if(event.getAction()==MotionEvent.ACTION_UP) {
            if(x<event.getX()) {
                Intent intent =new Intent(getApplicationContext(),RunningActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
            else if(x>event.getX()) {
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        }
        return true;

    }

}
