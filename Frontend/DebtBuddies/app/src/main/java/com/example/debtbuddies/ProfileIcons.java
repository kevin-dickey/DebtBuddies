package com.example.debtbuddies;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Random;

public class ProfileIcons extends AppCompatActivity {
    String icon;
    ImageView playerIcon;
    CardView playerIcon0, playerIcon1, playerIcon2, playerIcon3, playerIcon4,
            playerIcon5, playerIcon6, playerIcon7, playerIcon8;

    Button b_icon0, b_icon1, b_icon2, b_icon3, b_icon4, b_icon5, b_icon6, b_icon7,
            b_icon8;
    int image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icons);

        playerIcon = findViewById(R.id.icon);
        playerIcon0 = findViewById(R.id.icon0);
        playerIcon1 = findViewById(R.id.icon1);
        playerIcon2 = findViewById(R.id.icon2);
        playerIcon3 = findViewById(R.id.icon3);
        playerIcon4 = findViewById(R.id.icon4);
        playerIcon5 = findViewById(R.id.icon5);
        playerIcon6 = findViewById(R.id.icon6);
        playerIcon7 = findViewById(R.id.icon7);
        playerIcon8 = findViewById(R.id.icon8);


        b_icon0 = findViewById(R.id.b_icon0);
        b_icon1 = findViewById(R.id.b_icon1);
        b_icon2 = findViewById(R.id.b_icon2);
        b_icon3 = findViewById(R.id.b_icon3);
        b_icon4 = findViewById(R.id.b_icon4);
        b_icon5 = findViewById(R.id.b_icon5);
        b_icon6 = findViewById(R.id.b_icon6);
        b_icon7 = findViewById(R.id.b_icon7);
        b_icon8 = findViewById(R.id.b_icon8);

    }


    public void onButtonClick0(View view) {
        image = getResources().getIdentifier("icon0", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick1(View view) {
        image = getResources().getIdentifier("icon1", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick2(View view) {
        image = getResources().getIdentifier("icon2", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick3(View view) {
        image = getResources().getIdentifier("icon3", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick4(View view) {
        image = getResources().getIdentifier("icon4", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick5(View view) {
        image = getResources().getIdentifier("icon5", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick6(View view) {
        image = getResources().getIdentifier("icon6", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick7(View view) {
        image = getResources().getIdentifier("icon7", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }
    public void onButtonClick8(View view) {
        image = getResources().getIdentifier("icon8", "drawable", getPackageName());
        playerIcon.setImageResource(image);
    }

}
