package com.termproject.route.route;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.http.Query;

public class SharingActivity extends AppCompatActivity {
    float x;
    public static final String FIREBESE_POST_URL = "gs://routetermproject-f7baa.appspot.com/Post";
        private FirebaseStorage storage = FirebaseStorage.getInstance(FIREBESE_POST_URL);
     private StorageReference storageReference = storage.getReference();
    //private StorageReference spaceRef;
    private RecyclerView rv;
    //public PageIndicatorView pageIndicatorView;
    private Query mapRef, routeRef;
    public Button sharing;
    ArrayList<thePost> sPost = new ArrayList<>();

    public static final String URL = "gs://routetermproject-f7baa.appspot.com/";
    StorageReference storageRef = storage.getReferenceFromUrl(URL);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);


        PostAdapter mCardAdapter = new PostAdapter(getSupportFragmentManager(), sPost);
        rv = (RecyclerView) findViewById(R.id.recView);
        sharing=(Button)findViewById(R.id.addBtn);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mCardAdapter);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(rv, "Refresh", Snackbar.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
        sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
              
            }
        });

    }
       /* PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv);*/


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView id;
            ImageView mapImage;
            TextView route;
            ViewPager viewPager;

            public MyViewHolder(View itemView) {
                super(itemView);
                id = itemView.findViewById(R.id.userId);
                mapImage = itemView.findViewById(R.id.mapImage);
                route = itemView.findViewById(R.id.routeText);
            }
        }

        class pagerAdapter extends PagerAdapter {
            LayoutInflater inflater;
            ArrayList<String> imageUrls;

            public pagerAdapter(LayoutInflater inflater, ArrayList arrayList) {
                this.inflater = inflater;
                this.imageUrls = arrayList;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = inflater.inflate(R.layout.sharing_card_view, null);
                ImageView imageView = view.findViewById(R.id.mapImage);
                Glide.with(SharingActivity.this).load(imageUrls.get(position)).centerCrop().into(imageView);
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
            public void onBindViewHolder(final SharingActivity.MyViewHolder holder, final int position) {
                final thePost post = sPost.get(position);
                Gson gson = new Gson();

                ArrayList<String> arrayList = gson.fromJson(post.getId(), new TypeToken<ArrayList<String>>() {
                }.getType());
                holder.id.setText(post.getId());
                holder.route.setText("The route is :" + post.getRoute());
                holder.viewPager.setAdapter(new pagerAdapter(getLayoutInflater(), arrayList));
                holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                /*holder.pageIndicatorView.setCount(holder.viewPager.getAdapter().getCount());
                holder.pageIndicatorView.setSelection(1);
                holder.pageIndicatorView.setViewPager(holder.viewPager);*/
                final Activity activity= getParent();
                if (activity == null || activity.isFinishing())
                    return;
                if (!TextUtils.isEmpty(post.getImageUrl())) {
                    Glide.with(SharingActivity.this)
                            .load(post.getImageUrl())
                            .into(holder.mapImage);
                } else {
                    Glide.with(SharingActivity.this)
                            .load(R.mipmap.ic_launcher_round)
                            .into(holder.mapImage);
                }

            }
            @Override
            public int getItemCount(){
                return sPost.size();
            }


        }
       /* public View onCreateView (LayoutInflater inflater,final ViewGroup container, Bundle
        savedInstanceState)
        {
            View v = inflater.inflate(R.layout.sharing_card_view, container, false);

        }
*/

    }

/*

package com.lsh.fillette;


        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.net.Uri;
        import android.opengl.GLUtils;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Looper;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.design.widget.NavigationView;
        import android.support.v4.app.Fragment;

        import android.support.v4.view.GravityCompat;
        import android.support.v4.view.PagerAdapter;
        import android.support.v4.view.ViewPager;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.widget.CardView;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.StaggeredGridLayoutManager;
        import android.support.v7.widget.Toolbar;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;
        import com.dinuscxj.refresh.RecyclerRefreshLayout;
        import com.firebase.client.ChildEventListener;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.Query;
        import com.getbase.floatingactionbutton.FloatingActionButton;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;
        import com.kakao.kakaotalk.callback.TalkResponseCallback;
        import com.kakao.kakaotalk.response.KakaoTalkProfile;
        import com.kakao.kakaotalk.v2.KakaoTalkService;
        import com.kakao.network.ErrorResult;
        import com.kakao.usermgmt.UserManagement;
        import com.kakao.usermgmt.callback.LogoutResponseCallback;
        import com.lsh.fillette.databinding.ActivityMyPageBinding;
        import com.rd.PageIndicatorView;

        import org.joda.time.DateTime;
        import org.joda.time.Days;
        import org.joda.time.Hours;
        import org.joda.time.Minutes;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.sql.Date;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.List;

        import cn.pedant.SweetAlert.SweetAlertDialog;
        import de.hdodenhof.circleimageview.CircleImageView;

*/
/**
 * A simple {@link Fragment} subclass.
 *//*

public class FilterMarketFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final String FIREBASE_POST_URL = "https://kkotest-41a38.firebaseio.com/Post";
    public static final String FIREBASE_FILTER_URL = "https://kkotest-41a38.firebaseio.com/Filter";

    private static final String TAG = FilterMarketFragment.class.getName();

    private Button downBtn;
    private ProgressBar loginProgress;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private CircleImageView profileImgView;
    private TextView userEmail, userNickName;
    ActivityMyPageBinding binding;
    private String path;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private FilterMarketFragment.MyAdapter mAdapter;
    private FloatingActionButton mFloatingBtn;
    private List<String> mKeys = new ArrayList<>();
    private List<Filter> mFilters = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private Query mRef, filterRef;
    private View layout;
    MainActivity currActivity;
    private FirebaseUser currentUser;
    Fragment fragment = this;

    public FilterMarketFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_market_my_page, container, false);
        //loginProgress = layout.findViewById(R.id.loginProgressBar);
        mRecyclerView = layout.findViewById(R.id.recyclerView);
        //downBtn = layout.findViewById(R.id.btn_down);

        currActivity = (MainActivity) getActivity();
        binding = currActivity.getBinding();
        toolbar = layout.findViewById(R.id.toolbar_main);
        toolbar.setSubtitleTextColor(Color.GRAY);
        toolbar.setTitleTextColor(Color.GRAY);
        //ImageView icon = new ImageView(getContext());
        //icon.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_more));
        currActivity.setSupportActionBar(toolbar);

        currentUser = currActivity.getCurrentUser();
        currActivity.setSupportActionBar(toolbar);
        drawer = layout.findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                currActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(0);


        com.getbase.floatingactionbutton.FloatingActionButton createWithCamera = layout.findViewById(R.id.filter_preview);
        createWithCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(currActivity, FilterUploadByCamera.class));
                currActivity.curr = 0;
            }
        });
        com.getbase.floatingactionbutton.FloatingActionButton createWithPhoto = layout.findViewById(R.id.filter_photo);
        createWithPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currActivity.curr = 0;
                startActivity(new Intent(currActivity, PictureActivity.class));
            }
        });


        //layout.setBackgroundColor(Color.WHITE);
        navigationView = layout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView = navigationView.getHeaderView(0);
        profileImgView = headView.findViewById(R.id.profile_img_view);
        userEmail = headView.findViewById(R.id.user_id);
        userNickName = headView.findViewById(R.id.user_name);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        //mStaggeredGridLayoutManager.setReverseLayout(true);
        mStaggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new FilterMarketFragment.MyAdapter();
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        final RecyclerRefreshLayout refreshStyle = layout.findViewById(R.id.refresh);
        refreshStyle.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStyle.setRefreshing(false);
            }
        });


        filterRef = new Firebase(FIREBASE_FILTER_URL);
        filterRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Filter value = dataSnapshot.getValue(Filter.class);
                String key = dataSnapshot.getKey();
                if (s == null) {
                    mFilters.add(0, value);
                    keys.add(0, key);
                } else {
                    int nextIdx = keys.indexOf(s) + 1;
                    if (nextIdx == mFilters.size()) {
                        mFilters.add(value);
                        keys.add(key);
                    } else {
                        mFilters.add(nextIdx, value);
                        keys.add(nextIdx, key);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Filter value = dataSnapshot.getValue(Filter.class);
                int index = keys.indexOf(key);
                mFilters.set(index, value);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = keys.indexOf(key);

                keys.remove(index);
                mFilters.remove(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Filter newModel = dataSnapshot.getValue(Filter.class);
                int index = keys.indexOf(key);
                mFilters.remove(index);
                keys.remove(index);

                if (s == null) {
                    mFilters.add(0, newModel);
                    keys.add(0, key);
                } else {
                    int nextIndex = keys.indexOf(s) + 1;
                    if (nextIndex == mFilters.size()) {
                        mFilters.add(newModel);
                        keys.add(key);
                    } else {
                        mFilters.add(nextIndex, newModel);
                        keys.add(nextIndex, key);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                firebaseError.toException().printStackTrace();
            }
        });
        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            requestProfile();
        }
    }

    private void requestProfile() {
        KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onNotKakaoTalkUser() {

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(KakaoTalkProfile result) {
                Log.d(TAG, "path : " + result.getProfileImageUrl());
                path = result.getProfileImageUrl();
                userEmail.setText(currentUser.getEmail());
                userNickName.setText(currentUser.getDisplayName());
                Activity activity = getActivity();
                if (activity == null || activity.isFinishing())
                    return;
                if (!TextUtils.isEmpty(path)) {
                    Glide.with(activity)
                            .load(path)
                            .into(profileImgView);
                } else {
                    Glide.with(activity)
                            .load(R.mipmap.ic_launcher_round)
                            .into(profileImgView);
                }
            }
        });
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView background;
        TextView creator;
        CardView cardView;
        CircleImageView profile;
        ImageView down;
        PageIndicatorView pageIndicatorView;
        ViewPager viewPager;
        TextView filterName;
        TextView filterRate;

        public MyViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            profile = itemView.findViewById(R.id.profile_img);
            creator = itemView.findViewById(R.id.creatorId);
            cardView = itemView.findViewById(R.id.card_view);
            down = itemView.findViewById(R.id.download);
            pageIndicatorView = itemView.findViewById(R.id.filter_img);
            viewPager = itemView.findViewById(R.id.imageVP);
            filterName = itemView.findViewById(R.id.filter_name);
            filterRate = itemView.findViewById(R.id.filter_rate);
        }
    }

    class vpAdapter extends PagerAdapter {
        LayoutInflater inflater;
        ArrayList<String> imageUrls;

        public vpAdapter(LayoutInflater inflater, ArrayList arrayList) {
            this.inflater = inflater;
            this.imageUrls = arrayList;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.filter_list_image, null);
            ImageView imageView = view.findViewById(R.id.img_viewpager_childimage);
            Glide.with(FilterMarketFragment.this).load(imageUrls.get(position)).centerCrop().into(imageView);
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

    class MyAdapter extends RecyclerView.Adapter<FilterMarketFragment.MyViewHolder> {

        @Override
        public FilterMarketFragment.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.card_post, null);
            FilterMarketFragment.MyViewHolder myViewHolder = new FilterMarketFragment.MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final FilterMarketFragment.MyViewHolder holder, final int position) {
            final Filter filter = mFilters.get(position);
            Gson gson = new Gson();
            ArrayList<String> arrayList = gson.fromJson(filter.getImage(), new TypeToken<ArrayList<String>>(){}.getType());
            holder.creator.setText(filter.getCreator());
            holder.filterName.setText("필터이름 : " + filter.getFilterName());
            holder.viewPager.setAdapter(new vpAdapter(getLayoutInflater(), arrayList));
            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    holder.pageIndicatorView.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            holder.pageIndicatorView.setCount(holder.viewPager.getAdapter().getCount());
            holder.pageIndicatorView.setSelection(1);
            holder.pageIndicatorView.setViewPager(holder.viewPager);
            final Activity activity = getActivity();
            if (activity == null || activity.isFinishing())
                return;
            if (!TextUtils.isEmpty(filter.getProfileUrl())) {
                Glide.with(FilterMarketFragment.this)
                        .load(filter.getProfileUrl())
                        .into(holder.profile);
            } else {
                Glide.with(FilterMarketFragment.this)
                        .load(R.mipmap.ic_launcher_round)
                        .into(holder.profile);
            }
            holder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("해당 필터를 다운받으시겠습니까?")
                            .setConfirmText("OK")
                            .setCancelText("NO")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                    final SweetAlertDialog s = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                    s.setConfirmText("OK")
                                            .setTitleText("다운로드 완료")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    FilterManger.getInstance().saveSharedPreferences_Data(getContext(), "filter", filter.getFilterData());
                                                    FilterManger.getInstance().saveSharedPreferences_Data(getContext(), "bgurl", filter.getImage());
                                                    s.cancel();
                                                    currActivity.onStart();
                                                }
                                            }).show();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    final SweetAlertDialog s = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                    s.setConfirmText("OK")
                                            .setTitleText("다운로드 취소")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    s.cancel();
                                                }
                                            }).show();
                                }
                            })
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }
    }

    */
/*  Getting the time difference between edited and current time in millisecond  *//*

    String getDiffTimeText(long targetTime) {
        DateTime curDateTime = new DateTime();
        DateTime targetDateTime = new DateTime().withMillis(targetTime);

        int diffDay = Days.daysBetween(curDateTime, targetDateTime).getDays();
        int diffHours = Hours.hoursBetween(targetDateTime, curDateTime).getHours();
        int diffMinutes = Minutes.minutesBetween(targetDateTime, curDateTime).getMinutes();

        if (diffDay == 0) {
            if (diffHours == 0 && diffMinutes == 0) {
                return "방금전";
            }
            if (diffHours > 0) {
                return "" + diffHours + "시간 전";
            }
            return "" + diffMinutes + "분 전";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(new Date(targetTime));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    */
/**
     * Progressing view
     *//*

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    public void onBackPressed() {
        currActivity.onBackPressed();
        DrawerLayout drawer = layout.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(getActivity(), FilterCamera.class));
        } else if (id == R.id.nav_gallery) {
            Uri uri = Uri.parse("content://media/internal/images/media");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    FirebaseAuth.getInstance().signOut();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            currActivity.setUI();
                        }
                    });
                }
            });
        }
        DrawerLayout drawer = layout.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
*/
