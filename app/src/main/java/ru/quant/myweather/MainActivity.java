package ru.quant.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String TAG = "WEATHER";
    TextView tvTemp;
    ImageView tvImage;
    LinearLayout llForecast;
    WeatherAPI.ApiInterface api;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button) findViewById(R.id.btnStart);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvImage = (ImageView) findViewById(R.id.ivImage);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                String units = "metric";
                String key = WeatherAPI.KEY;

                Log.d(TAG, "OK");

                // get weather for today
                Call<WeatherDay> callToday = api.getToday(lat, lng, units, key);
                callToday.enqueue(new Callback<WeatherDay>() {
                    @Override
                    public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {
                        Log.e(TAG, "onResponse");
                        WeatherDay data = response.body();
                        //Log.d(TAG,response.toString());

                        if (response.isSuccessful()) {
                            tvTemp.setText(data.getCity() + " " + data.getTempWithDegree());
                            Glide.with(MainActivity.this).load(data.getIconUrl()).into(tvImage);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherDay> call, Throwable t) {
                        Log.e(TAG, "onFailure");
                        Log.e(TAG, t.toString());
                    }
                });

            }
        });


    }



    public int convertDPtoPX(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int)(dp * density);
        return px;
    }

}