package com.levi.noy.noytask1;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class RuntimeMatrix extends ActionBarActivity implements LocalService.ISimulatedPlayerListener {
    // CONSTANTS
    public final static int DEFAULT_MAT_SIZE = 1;
    public final static String BOARD_SIZE = "SIZE";
    public final static String BOARD_SIZE_BUNDLE_KEY = "BUNDLE_KEY";
    public final float BOARD_WEIGHT = 0.8f;
    public final float STAT_WEIGHT = 0.2f;

    // SERVICE
    LocalService mService;
    boolean isBound =false;
    boolean otherThinks = false;
    //private ProgressDialog progress;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalService.LocalBinder binder = (LocalService.LocalBinder)service;
            mService = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    // VIEWS AND LAYOUTS
    LinearLayout runtimeLL,runtimeLLChild;
    ImageView runtimeIV;
    GridLayout runtimeGL;
    TextView left,right;

    // ANIMATOR
    RotateAnimation anim;
    Animation fadeIn;
    Animation fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        left = new TextView(RuntimeMatrix.this);
        right = new TextView(RuntimeMatrix.this);
        left.setTextSize(26);
        right.setTextSize(26);
        fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                runtimeIV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runtimeIV.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOut = new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(0);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runtimeIV.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //progress = new ProgressDialog(RuntimeMatrix.this);
        //progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progress.setIndeterminate(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(runtimeLL != null){
            runtimeLL.removeAllViews();
            runtimeLLChild.removeAllViews();
            runtimeGL.removeAllViews();
        }
        // get rows and columns
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int mat_r = settings.getInt("rows",5);
        int mat_c = settings.getInt("cols",5);

        // Bind to Service
        Intent intent = new Intent(this,LocalService.class);
        Bundle b = new Bundle();
        b.putInt(BOARD_SIZE,mat_r*mat_c);
        intent.putExtra(BOARD_SIZE_BUNDLE_KEY,b);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // get screen size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int barHeight = getNavBarHeight();
        Log.d("DEBUG","nav height = "+barHeight);
        int statusBarHeight = getStatusBarHeight();
        Log.d("DEBUG","stat height = "+statusBarHeight);
        int actionBarHeight = getActBarHeight();
        Log.d("DEBUG","action height = "+actionBarHeight);
        screenHeight = screenHeight-(statusBarHeight+actionBarHeight);

        // generate LinearLayout
        runtimeLL = new LinearLayout(RuntimeMatrix.this);

        LinearLayout.LayoutParams linParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        runtimeLL.setLayoutParams(linParams);
        runtimeLL.setOrientation(LinearLayout.VERTICAL);

        // generate GridLayout
        runtimeGL = new GridLayout(RuntimeMatrix.this);

        // specify layout rows and columns
        runtimeGL.setColumnCount(mat_c);
        runtimeGL.setRowCount(mat_r);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth, (int)(BOARD_WEIGHT*screenHeight));
        runtimeGL.setLayoutParams(params);

        // create and insert buttons
        for (int i = 0; i<mat_r*mat_c;i++){
            Button currentB = new Button(getApplicationContext());
            currentB.setBackground(getResources().getDrawable( R.drawable.btn_all ));
            currentB.setText(i+"");
            currentB.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/mat_c,params.height/mat_r));
            currentB.setOnClickListener(new ButtonListen());
            runtimeGL.addView(currentB);
        }

        // generate LinearLayout
        runtimeLLChild = new LinearLayout(RuntimeMatrix.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, (int)(STAT_WEIGHT*screenHeight));
        lp.weight = 1;
        LinearLayout.LayoutParams linParamsChild = new LinearLayout.LayoutParams(screenWidth,(int)(STAT_WEIGHT*screenHeight));
        // generate ImageView
        runtimeIV = new ImageView(RuntimeMatrix.this);
        runtimeIV.setImageResource(R.drawable.hour_glass);
        runtimeIV.setMinimumHeight((int)(STAT_WEIGHT*screenHeight));
        runtimeIV.setMinimumWidth((int)(STAT_WEIGHT*screenHeight));
        runtimeIV.setLayoutParams(lp);
        runtimeIV.setVisibility(View.INVISIBLE);
        // animation properties
        anim = new RotateAnimation(0f,360f,screenWidth/6,((STAT_WEIGHT*screenHeight))/2);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2450);


        left.setText("Your Turn");
        left.setLayoutParams(lp);
        right.setLayoutParams(lp);
        // add left textview
        runtimeLLChild.addView(left);
        // add image view to child layout
        runtimeLLChild.addView(runtimeIV);
        // add right textview
        runtimeLLChild.addView(right);
        runtimeLLChild.setLayoutParams(linParamsChild);

        // add GL to Main LinearLay
        runtimeLL.addView(runtimeGL);
        // add child layout to LinearLay
        runtimeLL.addView(runtimeLLChild);
        // apply the view to the screen
        setContentView(runtimeLL);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    public void didDecide(final int place) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(RuntimeMatrix.this,"I go for place "+place,Toast.LENGTH_SHORT).show();
                runtimeIV.clearAnimation();
                runtimeIV.startAnimation(fadeOut);
                left.setText("Your Turn");
                right.setText("");
                //progress.hide();
                otherThinks=false;
            }
        });

    }

    class ButtonListen implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!otherThinks) {
                Button b = (Button) v;
                b.setBackground(getResources().getDrawable( R.drawable.btn_miss ));
                b.setBackground(getResources().getDrawable( R.drawable.btn_hit ));
                b.setEnabled(false);

                runtimeIV.startAnimation(fadeIn);
                left.setText("Phones Turn");
                right.setText("Thinking...");
                //progress.show();
                otherThinks = true;
                mService.makeMove(RuntimeMatrix.this);
                //String buttonText = b.getText().toString();
                //Toast.makeText(RuntimeMatrix.this,getText(R.string.clicked_on)+" "+buttonText,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getActBarHeight(){
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getNavBarHeight() {
        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(RuntimeMatrix.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
