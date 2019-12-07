package com.example.muthomap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muthomap.R;
import com.example.muthomap.fragments.CartActivity;
import com.example.muthomap.models.Category;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context;
    List<Category> mcategory;
    public View.OnClickListener mOnItemClickListener;


    public RecyclerViewAdapter(Context context, List<Category> mcategory) {
        this.context = context;
        this.mcategory = mcategory;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v= LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        MyViewHolder viewHolder= new MyViewHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.category_image.setImageResource(mcategory.get(position).getMphoto());
        holder.category_name.setText(mcategory.get(position).getmCategoryName());


    }

    @Override
    public int getItemCount() {
        return  mcategory.size();
    }

    public void setmOnItemClickListener(View.OnClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView category_image;
        private TextView category_name;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            category_image= (ImageView) itemView.findViewById(R.id.category_image);
            category_name= itemView.findViewById(R.id.category_name);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);



        }
    }
}
