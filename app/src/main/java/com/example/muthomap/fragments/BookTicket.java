package com.example.muthomap.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muthomap.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


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
        View view = inflater.inflate(R.layout.fragment_book_ticket, container, false);

        mSingleWay = view.findViewById(R.id.single_way);
        mRounded = view.findViewById(R.id.rounded);
        mSingleWayCard = view.findViewById(R.id.cardView);
        mRoundedCard2 = view.findViewById(R.id.cardView2);
        mfrom_input = view.findViewById(R.id.from_input);
        mto_input = view.findViewById(R.id.to_input);
        mpassenger_input = view.findViewById(R.id.passenger_input);
        mdate = view.findViewById(R.id.date1);
        // Inflate the layout for this fragment


        mSubmit1 = view.findViewById(R.id.submit);
        mSubmit2=view.findViewById(R.id.submit2);

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

        mSubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference mTicketDatabase = FirebaseDatabase.getInstance().getReference().child("ticket_order");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> hashMap = new HashMap();
                hashMap.put("id", currentFirebaseUser.getUid());
                hashMap.put("From", mfrom_input.getText().toString());
                hashMap.put("Destination", mto_input.getText().toString());
                hashMap.put("Number of Tickets", mpassenger_input.getText().toString());
                hashMap.put("Tour Date", mdate.getText().toString());

                mTicketDatabase.setValue(hashMap);

                Toast.makeText(getContext(), "Successfully Submitted", Toast.LENGTH_SHORT).show();
                mfrom_input.getText().clear();
                mto_input.getText().clear();
                mpassenger_input.getText().clear();
                mdate.getText().clear();
            }
        });
        mSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference mTicketDatabase = FirebaseDatabase.getInstance().getReference().child("ticket_order");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> hashMap = new HashMap();
                hashMap.put("id", currentFirebaseUser.getUid());
                hashMap.put("From", mfrom_input.getText().toString());
                hashMap.put("Destination", mto_input.getText().toString());
                hashMap.put("Number of Tickets", mpassenger_input.getText().toString());
                hashMap.put("Tour Date", mdate.getText().toString());

                mTicketDatabase.setValue(hashMap);

                Toast.makeText(getContext(), "Successfully Submitted", Toast.LENGTH_SHORT).show();
                mfrom_input.getText().clear();
                mto_input.getText().clear();
                mpassenger_input.getText().clear();
                mdate.getText().clear();
            }
        });

        return view;
    }

}
