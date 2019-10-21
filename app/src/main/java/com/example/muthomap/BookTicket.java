package com.example.muthomap;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookTicket extends Fragment {

    private EditText mfrom_input, mto_input, mfrom_input2, mto_input2, mpassenger_input, npassenger_input2, mdate, mdate2, mdate3;
    private Button mSingleWay, mRounded, mSubmit1, mSubmit2;
    private MaterialCardView mSingleWayCard, mRoundedCard2;

    public static BookTicket getInstance() {
        return new BookTicket();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_book_ticket, container, false);

        mSingleWay= view.findViewById(R.id.single_way);
        mRounded= view.findViewById(R.id.rounded);
        mSingleWayCard= view.findViewById(R.id.cardView);
        mRoundedCard2= view.findViewById(R.id.cardView2);
        // Inflate the layout for this fragment


        mSingleWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSingleWayCard.setVisibility(View.VISIBLE);
                mRoundedCard2.setVisibility(View.GONE);

            }
        });

        mRounded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRoundedCard2.setVisibility(View.VISIBLE);
                mSingleWayCard.setVisibility(View.GONE);

            }
        });

        return view;
    }

}
