package com.toong.androidcurrencyedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    String a = "1756.6655";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BigDecimal parsed = new BigDecimal(a);
    }
}
