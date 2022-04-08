package com.example.wristwearablecapstone;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.*;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    //Variables for streaming the camera feed
    private SurfaceView streamElement;
    private MediaPlayer streamPlayer;
    private SurfaceHolder streamHolder;
    private static final String STREAM_PATH = "rtsp://192.168.42.1/live.mjpeg";//// <- test stream | real stream ->
    private boolean stream_on = false;
    private TextView status;
    private Toast status_toast;

    //Variables for remote camera control
    private String basis_control_url = "http://192.168.42.1/cgi-bin/foream_remote_control?";
    private URL remote_control;
    private HttpURLConnection camera_connection;

    //Variables for recording timer
    private Long start_time;
    private Long current_time;
    private TextView timer;

    //Variables for recording functionality
    private boolean recording = false;
    private  String video_path;

    private MediaRecorder recorder;

    private static String TAG = "Recorder";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
       // appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        //Allows http requests on main thread, maybe not secure
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Sets up the streaming elements
        streamElement = findViewById(R.id.streamView);
        streamHolder = streamElement.getHolder();
        stream_on = false;

        //Timer
        timer = findViewById(R.id.recording_timer);
        timer.setVisibility(View.INVISIBLE);


        streamElement.setVisibility(View.INVISIBLE);

        status = findViewById(R.id.textview_first);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        NavController navHost = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//            navHost.navigate(R.id.action_FirstFragment_to_settingsFragment);
//            return true;
//
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    //Click listener for the stream button
    public void stream_button_listener(View view){
        if(recording){
            record_button_listener(getCurrentFocus());
        }

        if(stream_on){

            stream_on = false;
            status_toast.makeText(getApplicationContext(), "Stopping Stream", Toast.LENGTH_SHORT).show();


            if(streamPlayer.isPlaying()){
                streamElement.setVisibility(View.INVISIBLE);
                //Stops the stream
                streamPlayer.stop();
                //Deletes and releases the streamPlayer
                streamPlayer.release();

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
                streamPlayer.setVolume(0,0);

                status_toast.makeText(getApplicationContext(), "Stream Connected", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                status_toast.makeText(getApplicationContext(), "Unable to Connect", Toast.LENGTH_SHORT).show();
                streamElement.setVisibility(View.INVISIBLE);
                streamPlayer = null;
                stream_on = false;
                e.printStackTrace();
            }


        }

    }

    public void onVisibilityAggregated (boolean isVisible){}


    public void screen_return(){

        for(int i = 0; i < 10000; i++){

            timer.setVisibility(View.INVISIBLE);

            streamElement.setVisibility(View.INVISIBLE);
        }



    }



    public void record_button_listener(View view){

        if(recording){

            try{

                remote_control = new URL(basis_control_url+"stop_record");
                camera_connection = (HttpURLConnection) remote_control.openConnection();
                camera_connection.setRequestMethod("POST");
                camera_connection.getInputStream();
                camera_connection.disconnect();
                recording = false;
                timer.setText("00:00");
                timer.setVisibility(View.INVISIBLE);

            }catch(Exception e){

                status_toast.makeText(getApplicationContext(), "Stop Recording Failed", Toast.LENGTH_SHORT).show();
                recording = true;
                e.printStackTrace();
            }



        }else{

            if(!stream_on){

                status_toast.makeText(getApplicationContext(), "Stream not on", Toast.LENGTH_SHORT).show();
                return;
            }


            try {

                remote_control = new URL(basis_control_url+"start_record");
                camera_connection = (HttpURLConnection) remote_control.openConnection();
                camera_connection.setRequestMethod("POST");
                camera_connection.getInputStream();
                camera_connection.disconnect();
                recording = true;

                timer.setVisibility(View.VISIBLE);

                start_time = System.nanoTime();

            }catch (Exception e){

                status_toast.makeText(getApplicationContext(), "Start Recording Failed", Toast.LENGTH_SHORT).show();

                recording = false;

                e.printStackTrace();
            }


            final Handler handler = new Handler();

            handler .post(new Runnable() {
                @Override
                public void run() {
                    if(recording) {
                        current_time = System.nanoTime();

                        Long elapsed_seconds = (current_time - start_time) / 1000000000;
                        Long seconds = elapsed_seconds % 60;
                        Long minutes = (seconds % 3600) / 60;

                        if(minutes < 10 && seconds < 10)
                            timer.setText("0"+minutes.toString() + ":0" + seconds.toString());
                        else if(minutes < 10 && seconds > 9){
                            timer.setText("0"+minutes.toString() + ":" + seconds.toString());
                        }else if (minutes > 9 && seconds < 10){
                            timer.setText(""+minutes.toString() + ":0" + seconds.toString());
                        }else{
                            timer.setText(""+minutes.toString() + ":" + seconds.toString());
                        }


                        handler.postDelayed(this, 1000);
                    }
                }
            });






        }

    }

}