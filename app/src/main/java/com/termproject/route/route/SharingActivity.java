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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.yongbeam.y_photopicker.util.photopicker.PhotoPagerActivity;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

//import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


import static android.net.Uri.parse;

public class SharingActivity extends AppCompatActivity {
float x,u;
int GALLERY_CODE=10;
final int PICTURE_REQUEST_CODE = 100;
    private Firebase postRef;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();
private String selectedImagePath;
    Button runningBtn,settingBtn;

    private static final String TAG = SharingActivity.class.getName();

    // Create a storage reference from our app
    Button addButton;
    private LinearLayoutManager mLinearLayoutManager;
    //FirebaseStorage storage=FirebaseStorage.getInstance("gs://routetermproject-f7baa.appspot.com/");

    private List<thePost> mPost = new ArrayList<>();
    private  List<String> mKeys = new ArrayList<>();
    private FirebaseUser currentUser;
    private SharingActivity.MyAdapter myAdapter;
    private ClipData clipData;
    private Uri photoUri;
    private Query mapRef;
    final int REQ_CODE_SELECT_IMAGE=100;
    private RecyclerView rv;
    public final static int REQUEST_CODE = 1;
    public final static int REQUEST_WRITE=0;
    boolean isCompleteAll = false;
    private FirebaseDatabase mDatabase;

    public static final String FIREBASE_POST_URL ="https://routetermproject-f7baa.firebaseio.com/Post";
    public static final String FIREBASE_STORAGE = "gs://routetermproject-f7baa.appspot.com";
    FirebaseStorage storage = FirebaseStorage.getInstance(FIREBASE_STORAGE);
    StorageReference ref = storage.getReference();

    public SharingActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
       // JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_sharing);

        rv = (RecyclerView)findViewById(R.id.recView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        myAdapter= new SharingActivity.MyAdapter();
        rv.setLayoutManager(mLinearLayoutManager);
        rv.setAdapter(myAdapter);
        //mPost = new thePost();
        runningBtn=(Button)findViewById(R.id.runText);
        settingBtn=(Button)findViewById(R.id.setText);
        addButton =(Button)findViewById(R.id.addBtn);

        mapRef = new Firebase(FIREBASE_POST_URL).orderByChild("writeTime");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SharingActivity.this, WriteActivity.class);
                startActivityForResult(intent, REQUEST_WRITE);
            }
        });
        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });
        mapRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("TAGG","?!?!?!?!");
                thePost value = dataSnapshot.getValue(thePost.class);
                String key = dataSnapshot.getKey();
                if(s==null){
                    mPost.add(0,value);
                    mKeys.add(0,key);
                }else{
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex+1;
                    if(nextIndex==mPost.size()){
                        mPost.add(value);
                        mKeys.add(key);
                    }
                }
//                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();
                thePost value = dataSnapshot.getValue(thePost.class);
                int index = mKeys.indexOf(key);
                mPost.set(index,value);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                mKeys.remove(index);
                mPost.remove(index);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();
                thePost newModel = dataSnapshot.getValue(thePost.class);
                int index = mKeys.indexOf(key);
                mPost.remove(index);
                mKeys.remove(index);
                if (s == null) {
                    mPost.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mPost.size()) {
                        mPost.add(newModel);
                        mKeys.add(key);
                    } else {
                        mPost.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                firebaseError.toException().printStackTrace();
            }
        });


    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userId;
        TextView time;
        ImageView mapView;
        TextView routeView;
        ViewPager viewPager;
        CardView cardView;

        public MyViewHolder(View itemView){
            super(itemView);
            userId=itemView.findViewById(R.id.userId);
            time=itemView.findViewById(R.id.timeText);
            mapView=itemView.findViewById(R.id.mapImage);
            routeView=itemView.findViewById(R.id.routeText);
            viewPager=itemView.findViewById(R.id.vp);
        }
    }
    class mAdapter extends PagerAdapter{
        LayoutInflater inflater;
        List<String> imageUrls;

        public mAdapter(LayoutInflater inflater, List arrayList){
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
            Glide.with(SharingActivity.this).load(ref.child(imageUrls.get(position))).centerCrop().into(imageView);
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
    }
    class MyAdapter extends RecyclerView.Adapter<SharingActivity.MyViewHolder> {

        @Override
        public SharingActivity.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.sharing_card_view, null);
            SharingActivity.MyViewHolder myViewHolder = new SharingActivity.MyViewHolder(itemView);

            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(final SharingActivity.MyViewHolder holder,final int position){
            final thePost post = mPost.get(position);
            final mAdapter pager = new mAdapter(getLayoutInflater(),post.getImageUrl());
            holder.routeView.setText(post.getRoute());
            holder.userId.setText(post.getName());
            holder.time.setText(post.getTime());
            pager.notifyDataSetChanged();
            holder.viewPager.setAdapter(pager);
            pager.notifyDataSetChanged();
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



// Register observers to listen for when the download is done or if it fails

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

   /* public void upLoadImages(int position, int num, String uid, String filterName, ArrayList<Uri> list) {
        StorageReference[] childRef = new StorageReference[list.size()];
        UploadTask[] uploadTask = new UploadTask[list.size()];
        thePost newPost = mPost.get(position);
       // final List arrayList = newPost.getHello();

        for (int i = 0; i < list.size(); ++i) {
            childRef[i] = ref.child(uid + "/" + filterName + "/" + (num + i) + ".jpeg");
            //arrayList.add(childRef[i].getPath());
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
      //  postRef.child(mKeys.get(position)).child("f").setValue(arrayList);
    }*/

}
