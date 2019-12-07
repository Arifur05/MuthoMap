package com.example.muthomap.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.muthomap.R;
import com.example.muthomap.adapters.RecyclerViewAdapter;
import com.example.muthomap.models.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    String cartFoodName, cartFoodPrice, cartFoodImage, userId;
    String Quantity;

    ImageView foodImage;
    TextView foodName, foodPrice, totalPrice;
    EditText name, Phone, address;
    Button submit;
    ElegantNumberButton quantity;
    DatabaseReference foodCart;

    String finalnames, finalphone, finaladdresss, finalprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Intent intent = getIntent();
        cartFoodName = intent.getStringExtra("foodName");
        cartFoodPrice = intent.getStringExtra("foodPrice");
        cartFoodImage = intent.getStringExtra("foodImage");
        foodName = findViewById(R.id.items_name);
        foodPrice = findViewById(R.id.items_price);
        foodImage = findViewById(R.id.carts_food_image);
        quantity = findViewById(R.id.quantity);
        totalPrice = findViewById(R.id.value);
        name = (EditText) findViewById(R.id.name);
        Phone = (EditText) findViewById(R.id.address);
        address = (EditText) findViewById(R.id.phone);
        submit = findViewById(R.id.order);

        if (cartFoodImage != null) {
            Picasso.get().load(cartFoodImage).placeholder(R.drawable.ic_launcher_background).into(foodImage);
        }
        foodName.setText(cartFoodName);
        foodPrice.setText(cartFoodPrice);

        quantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quantity = quantity.getNumber();
                int Price = (Integer.parseInt(cartFoodPrice) * Integer.parseInt(Quantity));
                String price1 = String.valueOf(Price);
                totalPrice.setText(price1);
            }
        });


        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        foodCart = FirebaseDatabase.getInstance().getReference();

        userId = User.getUid();
        HashMap<Object, String> foodOrder = new HashMap<>();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalnames = name.getText().toString();
                finalphone = Phone.getText().toString();
                finaladdresss = address.getText().toString();
                finalprice = totalPrice.getText().toString();

                setFoodOrder();

                Toast.makeText(CartActivity.this, "Your Order Has been Successfully Submitted" , Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setFoodOrder() {


        HashMap<Object, String> foodOrder = new HashMap<>();
        foodOrder.put("uid", userId);
        foodOrder.put("name", finalnames);
        foodOrder.put("phone", finalphone);
        foodOrder.put("address", finaladdresss);
        foodOrder.put("foodName", cartFoodName);
        foodOrder.put("foodPrice", cartFoodPrice);
        foodOrder.put("quantity", Quantity);
        foodOrder.put("totalPrice", finalprice);

        foodCart.child("foodOrder").push().setValue(foodOrder);
    }

}
