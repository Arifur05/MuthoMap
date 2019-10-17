package com.example.muthomap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class Places extends AppCompatActivity {
    private Spinner mselectPlace;
    private SeekBar mAreaSeekBar;
    private Button mSearchPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mselectPlace=findViewById(R.id.places_spinner);
        mAreaSeekBar=findViewById(R.id.area_seekbar);
        mSearchPlace= findViewById(R.id.search_places);

    }
}
