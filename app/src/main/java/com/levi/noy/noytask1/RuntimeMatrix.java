package com.levi.noy.noytask1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;


public class RuntimeMatrix extends ActionBarActivity {
    private final static int DEFAULT_MAT_SIZE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

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
        // generate GridLayout
        GridLayout runtimeGL = new GridLayout(getApplicationContext());

        // get rows and columns
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int mat_r = settings.getInt("rows",5);
        int mat_c = settings.getInt("cols",5);


        // specify layout rows and columns
        runtimeGL.setColumnCount(mat_c);
        runtimeGL.setRowCount(mat_r);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width=screenWidth;
        params.height=screenHeight;
        runtimeGL.setLayoutParams(params);

        // create and insert buttons
        for (int i = 0; i<mat_r*mat_c;i++){
            Button currentB = new Button(getApplicationContext());
            currentB.setText(i+"");
            currentB.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/mat_c,screenHeight/mat_r));
            currentB.setOnClickListener(new ButtonListen());
            runtimeGL.addView(currentB);
        }

        // apply the view to the screen
        setContentView(runtimeGL);
    }

    class ButtonListen implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button b = (Button)v;
            String buttonText = b.getText().toString();
            Toast.makeText(RuntimeMatrix.this,getText(R.string.clicked_on)+" "+buttonText,Toast.LENGTH_SHORT).show();
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
