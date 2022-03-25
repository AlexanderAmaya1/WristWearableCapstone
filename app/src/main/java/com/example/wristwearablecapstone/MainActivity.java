package com.example.wristwearablecapstone;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.wristwearablecapstone.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    private SurfaceView streamElement;
    private MediaPlayer streamPlayer;
    private SurfaceHolder streamHolder;
    private static final String STREAM_PATH = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"; //"rtsp://192.168.0.157/live.mjpeg";

    private boolean stream_on = false;
    private TextView status;
    private Toast status_toast;

    private static String TAG = "MainActivity";

    static{

        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV Installed Successfully");
        }else{
            Log.d(TAG, "OpenCV is not installed");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        //Sets up the streaming elements
        streamElement = findViewById(R.id.streamView);
        streamHolder = streamElement.getHolder();


//        try {
//            streamPlayer.setDataSource(STREAM_PATH);
//        }catch (IOException e){
//            e.printStackTrace();
//        }


        streamElement.setVisibility(View.INVISIBLE);

        status = findViewById(R.id.textview_first);
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

        NavController navHost = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            navHost.navigate(R.id.action_FirstFragment_to_settingsFragment);
            return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Click listener for the stream button
    public void stream_button_listener(View view){

        if(stream_on){

            stream_on = false;
            status_toast.makeText(getApplicationContext(), "Stopping Stream", Toast.LENGTH_SHORT).show();


            if(streamPlayer != null && streamPlayer.isPlaying()){
                //Stops the stream
                streamPlayer.stop();
                //Deletes and releases the streamPlayer
                streamPlayer.release();
                streamElement.setVisibility(View.INVISIBLE);
            }


        }else{

            stream_on = true;
            streamElement.setVisibility(View.VISIBLE);
            status_toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_LONG).show();

            try {

                streamPlayer = new MediaPlayer();
                streamPlayer.setDataSource(STREAM_PATH);
                streamPlayer.prepare();
                streamPlayer.start();
                streamPlayer.setDisplay(streamHolder);

                status_toast.makeText(getApplicationContext(), "Stream Connected", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }


    public void record_button_listener(View view){


    }

}