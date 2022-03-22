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

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private static final String STREAM_PATH = "rtsp://192.168.0.157/live.mjpeg";
    private static final String TEST_STREAM_PATH = "https://www.youtube.com/watch?v=mT8fBXSjbfI";

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

        surfaceView = (SurfaceView) findViewById(R.id.streamView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(MainActivity.this);

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
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        //media player
        System.out.println("Surface Created");
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(STREAM_PATH);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(MainActivity.this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //mediaPlayer.start();
       // mediaPlayer.setDisplay(binding.streamView.getHolder());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPlayer.setDisplay(surfaceHolder);
    }

}