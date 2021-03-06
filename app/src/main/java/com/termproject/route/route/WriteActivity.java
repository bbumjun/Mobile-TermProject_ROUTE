package com.termproject.route.route;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.util.DateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPagerActivity;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;
//import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.net.Uri.parse;

public class WriteActivity extends AppCompatActivity  {

    int GALLERY_CODE = 10;
    final int PICTURE_REQUEST_CODE = 100;
    private Firebase postRef;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();
    private String path, Uid, EmailId, routeInfo;
    private DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://routetermproject-f7baa.appspot.com/");
    private static final String TAG = WriteActivity.class.getName();

    FloatingActionButton addBtn;
    // Create a storage reference from our app
    EditText editText;
    String timeStamp;
    private LinearLayoutManager mLinearLayoutManager;
    StorageReference ref = storage.getReference();
    private List<thePost> mPost = new ArrayList<>();
    private List<String> mKeys = new ArrayList<>();
    private FirebaseUser currentUser;
    //private SharingActivity.MyAdapter myAdapter;
    private ClipData clipData;
    private Uri photoUri;
    private RecyclerView mRecyclerView;
    private Query mapRef;
    final int REQ_CODE_SELECT_IMAGE = 100;
    private RecyclerView rv;
    public static int position = 0;
    public final static int REQUEST_CODE = 1;
    boolean isCompleteAll = false;
    int progress = 0;
    public static final String FIREBASE_POST_URL = "https://routetermproject-f7baa.firebaseio.com/Route";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_write);

        addBtn=(FloatingActionButton) findViewById(R.id.addBtn);
        editText=(EditText)findViewById(R.id.routeTheText);
        postRef=new Firebase(FIREBASE_POST_URL);

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteActivity.this);
                intent.setMaxSelectCount(5);
                intent.setShowCamera(false);
                intent.setShowGif(true);
                intent.setSelectCheckBox(false);
                intent.setMaxGrideItemCount(3);
                startActivityForResult(intent, REQUEST_CODE);
                routeInfo = editText.getText().toString();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> photos = null;
        timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            thePost posting = new thePost();
            if (currentUser != null) {
                String mail = currentUser.getEmail();
                int ids = mail.indexOf("@");
                EmailId = mail.substring(0, ids);
                boolean emailVerified = currentUser.isEmailVerified();
                Uid = timeStamp;
                Log.d("Uid check", Uid);
            }

            posting.setName(EmailId);
            posting.setRoute(routeInfo);

            posting.setTime(timeStamp);
            int k = photos.size();
            ArrayList<Uri> imageUri = new ArrayList<Uri>();
            for (int i = 0; i < k; i++) {
                imageUri.add(i, Uri.fromFile(new File(photos.get(i))));
            }

            upLoadImages(posting, k, Uid, EmailId, imageUri);


        }
    }
    /*1.post 수정함
                    2.이미지 업로드 하기*/

    public void upLoadImages(thePost posting, int num, String uid, String id, ArrayList<Uri> list) {
        StorageReference[] upLoadRef = new StorageReference[list.size()];
        UploadTask[] uploadTask = new UploadTask[list.size()];
        ArrayList<String> theAddress = new ArrayList<String>();
        final List arrayList = posting.getImageUrl();
        CheckTypesTask task =new CheckTypesTask();
        task.execute();

        for (int i = 0; i < list.size(); ++i) {

            upLoadRef[i] = ref.child("route/" + uid + timeStamp + "/" + id + "/" + (num + i) + ".jpeg");
            String uploadRefStr = uid + timeStamp + "/" + id + "/" + (num + i) + ".jpeg";
            uploadTask[i] = upLoadRef[i].putFile(list.get(i));

            uploadTask[i].addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    isCompleteAll = false;
                    Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isCompleteAll = true;
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            });
            theAddress.add(i, uploadRefStr);
        }
        posting.setImageUrl(theAddress);
        postRef.child(uid).setValue(posting);


    }


    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                WriteActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Uploding..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 7; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }




}