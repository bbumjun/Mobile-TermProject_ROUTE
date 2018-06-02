package com.termproject.route.route;

import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class Post {
    String writerId;
    String text;
    String bgUrl;
    long writeTime;
    Map<String, Comment> commentMap = new HashMap<>();


    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

    public Map<String, Comment> getCommentMap() {
        return commentMap;
    }

    public void setCommentMap(Map<String, Comment> commentMap) {
        this.commentMap = commentMap;
    }
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
        ArrayList<String> arrayList = gson.fromJson(filter.getImage(), new TypeToken<ArrayList<String>>() {
        }.getType());
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

    /*  Getting the time difference between edited and current time in millisecond  */
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

    /**
     * Progressing view
     */
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


