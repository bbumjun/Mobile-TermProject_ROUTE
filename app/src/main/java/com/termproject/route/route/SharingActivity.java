package com.termproject.route.route;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import static android.net.Uri.parse;

public class SharingActivity extends AppCompatActivity {

    private Firebase postRef;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();
    private DatabaseReference databaseReference;
    ImageButton runningBtn,settingBtn;
    private static final String TAG = SharingActivity.class.getName();

    // Create a storage reference from our app
    private LinearLayoutManager mLinearLayoutManager;
    private List<thePost> mPost = new ArrayList<>();
    private  List<String> mKeys = new ArrayList<>();
    private SharingActivity.MyAdapter myAdapter;
    private RecyclerView rv;
    public final static int REQUEST_WRITE=0;
    boolean isCompleteAll = false;
    public static final String FIREBASE_POST_URL ="https://routetermproject-f7baa.firebaseio.com/Route";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference();


    public FloatingActionButton addBtn;


    public SharingActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_sharing);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        rv = (RecyclerView)findViewById(R.id.recView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myAdapter= new SharingActivity.MyAdapter();
        rv.setLayoutManager(mLinearLayoutManager);
        rv.setAdapter(myAdapter);

        runningBtn=(ImageButton)findViewById(R.id.runText);
        settingBtn=(ImageButton)findViewById(R.id.setText);
        addBtn =(FloatingActionButton) findViewById(R.id.addBtn);

        postRef=new Firebase(FIREBASE_POST_URL);
        postRef.orderByChild("write");


        addBtn.setOnClickListener(new View.OnClickListener() {
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
        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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


                myAdapter.notifyDataSetChanged();
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
            cardView=itemView.findViewById(R.id.cardView);
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
            StorageReference imgRef = ref.child("route/"+imageUrls.get(position));
            Glide.with(SharingActivity.this).using(new FirebaseImageLoader()).load(imgRef).into(imageView);
            Log.d("imgRef",imgRef.toString());
            Log.d("Uri",imageUrls.get(position));
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
            //LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final mAdapter pager = new mAdapter(getLayoutInflater(),post.getImageUrl());

            holder.routeView.setText(post.getRoute());
            holder.userId.setText(post.getName());
//
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

}
