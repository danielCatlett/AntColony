package com.daniel_catlett.antcolony;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Button startButton;
    TextView titleText;
    TextView subtitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Fieldwork.otf");

        titleText = findViewById(R.id.titleText);
        titleText.setTypeface(font);

        subtitleText = findViewById(R.id.subtitleText);
        subtitleText.setTypeface(font);

        startButton = findViewById(R.id.startButton);
        startButton.setTypeface(font);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, com.daniel_catlett.antcolony.SimulationActivity.class));
            }
        });
    }
}
