package com.example.habitassistant;

import static android.hardware.Sensor.TYPE_GYROSCOPE;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habitassistant.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;

public class MainActivity extends AppCompatActivity implements SensorHandler.SensorDataListener {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Button ok;

    private Spinner placeSpinner;
    private Spinner actionSpinner;

    private SensorHandler sensorHandler;
    private ScreenStatusChecker screenStatusChecker;
    private Handler handler = new Handler();

    private double gyroscopeX;
    private double gyroscopeY;
    private double gyroscopeZ;
    private double accelerX;
    private double accelerY;
    private double accelerZ;
    private boolean screenStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.activity_main);

        ok = findViewById(R.id.ok);
        placeSpinner = findViewById(R.id.placeSpinner);
        actionSpinner = findViewById(R.id.actionSpinner);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = (String) placeSpinner.getSelectedItem();
                String action = (String) actionSpinner.getSelectedItem();
                Toast.makeText(MainActivity.this, place, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, action, Toast.LENGTH_SHORT).show();
//                Intent startIntent = new Intent(MainActivity.this, MyService.class);
//                startService(startIntent);
            }
        });
        //传感器
        sensorHandler = new SensorHandler(this, this);

        //屏幕是否亮
        screenStatusChecker = new ScreenStatusChecker(this);

        // 开始循环
        putTable();
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

    //传感器
    @Override
    protected void onResume() {
        super.onResume();
        sensorHandler.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHandler.unregister();
    }

    @Override
    public void onGyroscopeDataChanged(float x, float y, float z) {
        //System.out.println(String.format("Gyroscope - x: %.2f, y: %.2f, z: %.2f", x, y, z));
        gyroscopeX = x;
        gyroscopeY = y;
        gyroscopeZ = z;
    }

    @Override
    public void onAccelerometerDataChanged(float x, float y, float z) {
        //System.out.println(String.format("Accelerometer - x: %.2f, y: %.2f, z: %.2f", x, y, z));
        accelerX = x;
        accelerY = y;
        accelerZ = z;
    }


    private void putTable() {
        // 这个 Runnable 对象定义了每10秒执行一次的代码
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                screenStatus = screenStatusChecker.isScreenOn();
                // 检查并打印屏幕状态
//                if (screenStatusChecker.isScreenOn()) {
//                    Log.i("ScreenStatus", String.valueOf(screenStatus));
//                } else {
//                    Log.i("ScreenStatus", "Screen is OFF");
//                }

                //陀螺仪x,y,z：gyroscopeX,gyroscopeX,gyroscopeZ
                //加速度器x,y,z：accelerX,accelerY,accelerZ
                //屏幕状态：screenStatus（true/false）

                //以上是数据，直接写入table

                // 重新安排代码在10秒后再次运行
                handler.postDelayed(this, 10000);
            }
        };

        // 首次运行
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当活动销毁时，取消定时操作，以避免内存泄漏
        handler.removeCallbacksAndMessages(null);
    }
}