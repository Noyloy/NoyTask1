package com.levi.noy.noytask1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;


public class RuntimeMatrix extends Activity {
    private final static int DEFAULT_MAT_SIZE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        Log.d(MainActivity.DEBUG_TAG, "activity starting");
        GridLayout runtimeGL = new GridLayout(getApplicationContext());
        Log.d(MainActivity.DEBUG_TAG, "grid view created");
        Intent intent = getIntent();
        int mat_r = intent.getIntExtra(MainActivity.EXTRA_KEY_ROW,DEFAULT_MAT_SIZE);
        int mat_c = intent.getIntExtra(MainActivity.EXTRA_KEY_COL,DEFAULT_MAT_SIZE);
        Log.d(MainActivity.DEBUG_TAG, "extra from intent is: row="+mat_r+" col="+mat_c);
        runtimeGL.setColumnCount(mat_c);
        runtimeGL.setRowCount(mat_r);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width=screenWidth;
        params.height=screenHeight;
        runtimeGL.setLayoutParams(params);
        for (int i = 0; i<mat_r*mat_c;i++){
            Button currentB = new Button(getApplicationContext());
            currentB.setText(i+"");
            currentB.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/mat_c,screenHeight/mat_r));
            currentB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    String buttonText = b.getText().toString();
                    Toast.makeText(RuntimeMatrix.this,getText(R.string.clicked_on)+" "+buttonText,Toast.LENGTH_SHORT).show();
                }
            });
            runtimeGL.addView(currentB);
        }


        setContentView(runtimeGL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_runtime_matrix, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
