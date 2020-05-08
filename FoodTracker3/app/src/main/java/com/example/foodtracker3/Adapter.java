package com.example.foodtracker3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public ArrayList<Product> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView foodNameView;
        public TextView foodQuantityView;
        public TextView expirationView;
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            foodNameView = itemView.findViewById(R.id.foodNameText);
            foodQuantityView = itemView.findViewById(R.id.foodQuantityText);
            expirationView = itemView.findViewById(R.id.expirationText);

            itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (listener != null) {
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION);
                        listener.onItemClick(position);
                   }
               }
            });

            imageView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) ;
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
    public Adapter(ArrayList<Product> list)
    {
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder evh = new ViewHolder(v, listener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Product currentProduct = list.get(position);
        holder.imageView.setImageResource(currentProduct.getIconResource() );
        holder.foodNameView.setText(currentProduct.getName() );
        holder.foodQuantityView.setText(Integer.toString(currentProduct.getQuantity()) );
        holder.expirationView.setText(Product.date_toAppStr(currentProduct.getExpiration_date()) );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
