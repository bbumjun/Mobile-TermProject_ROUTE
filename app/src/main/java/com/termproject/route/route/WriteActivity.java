package com.termproject.route.route;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

public class WriteActivity extends AppCompatActivity {
    float x,u;
    int GALLERY_CODE=10;
    final int PICTURE_REQUEST_CODE = 100;
    private Firebase postRef;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();
    private String path,Uid,EmailId,routeInfo,theTime;
    private DatabaseReference mDatabase;
    Button runningBtn,settingBtn;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://routetermproject-f7baa.appspot.com/");
    private static final String TAG = WriteActivity.class.getName();

    // Create a storage reference from our app
    Button addButton;
    EditText editText;

    private LinearLayoutManager mLinearLayoutManager;
    StorageReference ref = storage.getReference();
    private List<thePost> mPost = new ArrayList<>();
    private  List<String> mKeys = new ArrayList<>();
    private FirebaseUser currentUser;
    //private SharingActivity.MyAdapter myAdapter;
    private ClipData clipData;
    private Uri photoUri;
    private RecyclerView mRecyclerView;
    private Query mapRef;
    final int REQ_CODE_SELECT_IMAGE=100;
    private RecyclerView rv;
    public static int position=0;
    public final static int REQUEST_CODE = 1;
    boolean isCompleteAll = false;
    public static final String FIREBASE_POST_URL ="https://routetermproject-f7baa.firebaseio.com/Route";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_write);
        addButton=(Button)findViewById(R.id.goToGallery);
        editText=(EditText)findViewById(R.id.routeTheText);
        postRef=new Firebase(FIREBASE_POST_URL);
        /*rv = (RecyclerView)findViewById(R.id.recView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(mLinearLayoutManager);*/
    /*    rv = (RecyclerView)findViewById(R.id.recView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(mLinearLayoutManager);
        runningBtn=(Button)findViewById(R.id.runText);
        settingBtn=(Button)findViewById(R.id.setText);
        addButton =(Button)findViewById(R.id.addBtn);*/



        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteActivity.this);
                intent.setMaxSelectCount(20);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                intent.setSelectCheckBox(false);
                intent.setMaxGrideItemCount(3);
                startActivityForResult(intent, REQUEST_CODE);
                routeInfo=editText.getText().toString();
            }

        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
          /* if (photos != null) {
                selectedPhotos.addAll(photos);
            }*/
            currentUser= FirebaseAuth.getInstance().getCurrentUser();

            thePost posting = new thePost();
            if(currentUser!=null) {
               String mail  = currentUser.getEmail();
                int ids = mail.indexOf("@");
                EmailId = mail.substring(0,ids);
                boolean emailVerified = currentUser.isEmailVerified();

                Uid = currentUser.getUid();
                Log.d("abcd","hello"+" ");
            }
            else
                Log.d("abcde","bye"+" ");
            posting.setName(EmailId);
            posting.setRoute(routeInfo);
            String timeStamp = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date());
            theTime=timeStamp;
            posting.setTime(theTime);
            int k=photos.size();
            ArrayList<Uri> imageUri = new ArrayList<Uri>();
            for(int i=0;i<k;i++){
                imageUri.add(i,Uri.fromFile(new File(photos.get(i))));
            }
            //posting.setImageUrl(imageUri);
            upLoadImages(posting,k,Uid,EmailId,imageUri);



           /* Uri file = Uri.fromFile(new File(selectedImagePath));
            StorageReference uploadRef = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = uploadRef.putFile(file);

            new Firebase(SharingActivity.FIREBASE_POST_URL).push().setValue(posting);*/

        }
    }
    /*1.post 수정함
                    2.이미지 업로드 하기*/

    public void upLoadImages(thePost posting,int num, String uid, String id, ArrayList<Uri> list) {
        StorageReference[] upLoadRef = new StorageReference[list.size()];
        UploadTask[] uploadTask = new UploadTask[list.size()];
        ArrayList<String> theAddress= new ArrayList<String>();
        final List arrayList = posting.getImageUrl();

        for (int i = 0; i < list.size(); ++i) {
            upLoadRef[i] = ref.child("route/"+uid + "/" + id + "/" + (num + i) + ".jpeg");
            uploadTask[i] = upLoadRef[i].putFile(list.get(i));

            uploadTask[i].addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    isCompleteAll = false;
                    Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isCompleteAll = true;
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            });
        theAddress.add(i,upLoadRef[i].toString());
        }
        posting.setImageUrl(theAddress);
      //postRef.child("Post").child(filterName).child("UID").child(uid).child("ImageList").setValue(arrayList);
       postRef.child(uid).setValue(posting);
    }
  /*  String getDiffTimeText(long targetTime) {
        DateTime curDateTime = new DateTime();
        DateTime targetDateTime = new DateTime().withMillis(targetTime);

        int diffDay = Days.daysBetween(curDateTime, targetDateTime).getDays();
        int diffHours = Hours.hoursBetween(targetDateTime, curDateTime).getHours();
        int diffMinutes = Minutes.minutesBetween(targetDateTime, curDateTime).getMinutes();
        if (diffDay == 0) {
            if(diffHours == 0 && diffMinutes == 0){
                return "방금전";
            }
            if(diffHours > 0){
                return "" + diffHours + "시간 전";
            }
            return "" + diffMinutes + "분 전";

        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(new Date(targetTime));
        }
    }
*/

  /*  private String setProfile() {
        //path = FilterManger.getInstance().requestProfile();

        String a=(currentUser.getDisplayName());
        return a;
    }*/
    /*class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userId;
        ImageView mapView;
        TextView routeView;
        ViewPager viewPager;

        public MyViewHolder(View itemView){
            super(itemView);
            userId=itemView.findViewById(R.id.userId);
            mapView=itemView.findViewById(R.id.mapImage);
            routeView=itemView.findViewById(R.id.routeText);
            viewPager=itemView.findViewById(R.id.vp);
        }
    }
    class mAdapter extends PagerAdapter{
        LayoutInflater inflater;
        ArrayList<String> imageUrls;

        public mAdapter(LayoutInflater inflater, ArrayList arrayList){
            this.inflater=inflater;
            this.imageUrls=arrayList;
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.row, null);
            ImageView imageView = view.findViewById(R.id.mapImage);
            Glide.with(WriteActivity.this).load(imageUrls.get(position)).centerCrop().into(imageView);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }*//*
    class MyAdapter extends RecyclerView.Adapter<WriteActivity.MyViewHolder> {

        @Override
        public WriteActivity.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.sharing_card_view, null);
            WriteActivity.MyViewHolder myViewHolder = new WriteActivity.MyViewHolder(itemView);

            return myViewHolder;
        }

        public void onBindViewHolder(final WriteActivity.MyViewHolder holder,final int position){
            final thePost post = mPost.get(position);
            //final mAdapter m = new mAdapter(getLayoutInflater(),post.getHello());
            //Gson gson = new Gson();

            //ArrayList<String> arrayList = gson.fromJson(post.getImageUrl(), new TypeToken<ArrayList<String>>(){}.getType());
            holder.routeView.setText("111");
            holder.userId.setText("333");

            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //holder.pageIndicatorView.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        @Override
        public int getItemCount(){
            return mPost.size();
        }
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
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
    public ArrayList<Uri> getUritoImageName(List<String> route){
        ArrayList<Uri> hello= new ArrayList<>();
        for(int i=0;i<route.size();i++) {
            hello.add(parse(route.get(i)));
        }
        return hello;
    }
*/
   /* public void upLoadImages(int position, int num, String uid, String filterName, ArrayList<Uri> list) {
        StorageReference[] childRef = new StorageReference[list.size()];
        UploadTask[] uploadTask = new UploadTask[list.size()];
        thePost newPost = mPost.get(position);
        final List arrayList = newPost.getHello();

        for (int i = 0; i < list.size(); ++i) {
            childRef[i] = ref.child(uid + "/" + filterName + "/" + (num + i) + ".jpeg");
            arrayList.add(childRef[i].getPath());
            Log.e(TAG, childRef[i].getPath().toString());
            uploadTask[i] = childRef[i].putFile(list.get(i));

            uploadTask[i].addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    isCompleteAll = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isCompleteAll = true;
                }
            });
        }
        postRef.child(mKeys.get(position)).child("f").setValue(arrayList);
    }*/

}
