package com.example.foodtracker3;

import android.app.Notification;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.foodtracker3.App.expDate_Warning;
import static com.example.foodtracker3.App.expFood;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable
{
    //-----------------------------------------------------------------------------------
    private static NotificationManagerCompat notificationManager;
    //-----------------------------------------------------------------------------------

    public ArrayList<Product> list;
    public ArrayList<Product> listFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public ImageView DeleteImage;
        public TextView foodNameView;
        public TextView foodQuantityView;
        public TextView expirationView;
        public ImageSwitcher expiredImageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            foodNameView = itemView.findViewById(R.id.foodNameText);
            foodQuantityView = itemView.findViewById(R.id.foodQuantityText);
            expirationView = itemView.findViewById(R.id.expirationText);
            //-----------------------------------------------------------------------------------
            notificationManager = NotificationManagerCompat.from(itemView.getContext());
            //-----------------------------------------------------------------------------------

            itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (listener != null) {
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION);
                        listener.onItemClick(position);
                   }
                   //---------------------------------------------------------------------------------
                   sendOnChannel1(v);
                   //---------------------------------------------------------------------------------
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
        listFull = new ArrayList<>(list);
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

        //-----------------------------------------------------------------------------------
/*
        Date today = new Date();
        if(today.after(currentProduct.getExpiration_date())) {     // if expires today, or already expired
            holder.expiredImageView.setImageResource(R.drawable.ic_error_outline);
           // sendOnChannel1(holder.itemView);
        }
        else {

            if(currentProduct.getExpiration_date().getTime() - today.getTime() <= 345600000) {        // 4 days
                holder.expiredImageView.setImageResource(R.drawable.ic_warning);
               // sendOnChannel2(holder.itemView);
            } else {
                holder.expiredImageView.setImageResource(0);
            }
        }*/
        //------------------------------------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Filter getFilter()
    {
        return filter;
    }

    private Filter filter = new Filter()
    {
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Product> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(listFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Product item: listFull){
                    if(item.getName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        protected void publishResults(CharSequence contraint, FilterResults results)
        {
            list.clear();
            list.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
    //-----------------------------------------------------------------------------------

   public static void sendOnChannel1(View v)
   {
        //String title = editTextTitle.getText().toString();
        //String message = editTextMessage.getText().toString();

        String title = "Title #1";
        String message = "Message #1";

        Notification notification = new NotificationCompat.Builder(v.getContext(), expFood)
                .setSmallIcon(R.drawable.ic_kitchen)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification);
   }

    public void sendOnChannel2(View v)
    {
        //String title = editTextTitle.getText().toString();
        //String message = editTextMessage.getText().toString();
        String title = "Title #2";
        String message = "Message #2";

        Notification notification = new NotificationCompat.Builder(v.getContext(), expDate_Warning)
                .setSmallIcon(R.drawable.ic_kitchen)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        notificationManager.notify(2, notification);
    }
    //-----------------------------------------------------------------------------------

}//End Adapter
