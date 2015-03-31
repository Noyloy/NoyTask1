package com.levi.noy.noytask1;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_KEY_ROW = "USER_ROW_INPUT";
    public final static String EXTRA_KEY_COL = "USER_COL_INPUT";
    public final static String DEBUG_TAG = "DEBUG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views
        Button goButton = (Button)findViewById(R.id.go_btn);
        final EditText userInputR = (EditText)findViewById(R.id.user_r_input);
        final EditText userInputC = (EditText)findViewById(R.id.user_c_input);

        // add on click listener
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userIntR = -1;
                int userIntC = -1;
                boolean conversion_succ = true;

                // verify user input
                try {
                    userIntR = Integer.parseInt(userInputR.getText().toString());
                    userIntC = Integer.parseInt(userInputC.getText().toString());
                }
                catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this,R.string.int_only,Toast.LENGTH_SHORT).show();
                    conversion_succ = false;
                }

                // if user input verified, start RuntimeMatrix activity and pass user input
                if (conversion_succ) {
                    Intent intent = new Intent(MainActivity.this,RuntimeMatrix.class);
                    intent.putExtra(EXTRA_KEY_ROW,userIntR);
                    intent.putExtra(EXTRA_KEY_COL,userIntC);
                    startActivity(intent);
                    Log.d(DEBUG_TAG, "activity switch, rows=" + userIntR+" cols="+userIntC);
                }
            }
        });
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
