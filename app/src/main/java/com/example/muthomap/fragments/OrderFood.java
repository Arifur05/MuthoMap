package com.example.muthomap.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muthomap.R;
import com.example.muthomap.models.Category;
import com.example.muthomap.models.Food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFood extends Fragment {

    View view;
    public RecyclerView categoryRecyclerView , foodList;
    private List<Category> categoryList;

    private DatabaseReference foodDatabase;

    public OrderFood() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_order_food, container, false);
        /*  This is for category*/

       categoryRecyclerView=view.findViewById(R.id.category_list);

       RecyclerViewAdapter recyclerViewAdapter= new RecyclerViewAdapter(getContext(),categoryList);
       LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
       categoryRecyclerView.setLayoutManager(linearLayoutManager);
       categoryRecyclerView.setAdapter(recyclerViewAdapter);

        /*  This is for foodlist*/

        foodList=(RecyclerView) view.findViewById(R.id.recommended);
        foodList.setLayoutManager(new LinearLayoutManager(getContext()));

        foodDatabase= FirebaseDatabase.getInstance().getReference().child("food").child("category").child("pizza");


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options= new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodDatabase,Food.class).build();


        FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                String foodID= getRef(position).getKey();


                foodDatabase.child(foodID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image"))
                        {
                            //String foodImage= dataSnapshot.child("image").getValue().toString();
                            String foodName= dataSnapshot.child("name").getValue().toString();
                            String foodPrice= dataSnapshot.child("price").getValue().toString();

                            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.mFoodImage);
                            holder.mFoodName.setText(foodName);
                            holder.mFoodPrice.setText(foodPrice);
                        }
                        else {
                            String foodName= dataSnapshot.child("name").getValue().toString();
                            String foodPrice= dataSnapshot.child("price").getValue().toString();
                            holder.mFoodName.setText(foodName);
                            holder.mFoodPrice.setText(foodPrice);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);

                FoodViewHolder viewHolder= new FoodViewHolder(view);
                return viewHolder;
            }
        };

        foodList.setAdapter(adapter);

        adapter.startListening();

    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        ImageView mFoodImage;
        TextView mFoodName, mFoodPrice;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            mFoodImage= itemView.findViewById(R.id.food_image);
            mFoodName= itemView.findViewById(R.id.food_name);
            mFoodPrice= itemView.findViewById(R.id.food_price);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryList= new ArrayList<>();
        categoryList.add(new Category("Pizza",R.drawable.pizza));
        categoryList.add(new Category("Burger",R.drawable.burger));
        categoryList.add(new Category("Italian",R.drawable.italian_food));
        categoryList.add(new Category("Indian",R.drawable.india));
        categoryList.add(new Category("Chinese",R.drawable.chinese));
        categoryList.add(new Category("Chicken",R.drawable.chicken));

    }


}
