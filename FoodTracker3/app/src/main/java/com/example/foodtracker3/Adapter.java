package com.example.foodtracker3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * When initialized, the Adapter class acts as a controller, or a bridge between the UI components such as the recycler view,
 * and backend components such as the Home Fragment data manipulation logic.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 * */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable
{
    public ArrayList<Product> list;
    public static ArrayList<Product> listFull;
    private OnItemClickListener listener;

    /**
     * Classes that implement must use the following classes.
     * */
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onEditClick(int position);

    }
    /**
     * Initializes on click listener.
     * */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    /**
     * Holds the Object UI components.
     * */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView DeleteImage;
        public ImageView expiredImageView;
        public TextView foodNameView;
        public TextView foodQuantityView;
        public TextView expirationView;
        public TextView foodCategoryView;
        public ConstraintLayout expandableLayout;
        public ImageView editImageView;
        public Button cancelSaveButton;
        public Button saveButton;
        public EditText foodNameEditText;
        public EditText foodQuantityEditText;
        public EditText expirationEditText;
        /**
         * Retrieves UI components using the view parameter, and adds some event listeners to them
         * @param itemView the view required to access the components.
         * @param listener listener that handles the overall event loop.
         * */
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            foodNameView = itemView.findViewById(R.id.foodNameText);
            foodQuantityView = itemView.findViewById(R.id.foodQuantityText);
            expirationView = itemView.findViewById(R.id.expirationText);
            expiredImageView = itemView.findViewById(R.id.expiredImageView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            editImageView = itemView.findViewById(R.id.editImageView);
            foodCategoryView = itemView.findViewById(R.id.foodCategoryText);
            cancelSaveButton = itemView.findViewById(R.id.cancelSaveButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            foodNameEditText = itemView.findViewById(R.id.foodNameEditText);
            foodQuantityEditText = itemView.findViewById(R.id.foodQuantityEditText);
            expirationEditText = itemView.findViewById(R.id.expirationEditText);

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

            editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION);
                        listener.onEditClick(position);
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
    /**
     * Defines what to do when the ViewHolder is created. Inflates the view, passes it to the view holder, and returns the view holder.
     * @param parent The parent ViewGroup
     * @param viewType The ViewHolder view type.
     * */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder evh = new ViewHolder(v, listener);
        return evh;
    }

    /**
     * Defines what to do with the view holder which was passed the view.
     * @param holder The view holder returned by onCreateViewHolder.
     * @param position The position of the current product.
     * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Product currentProduct = list.get(position);
        holder.imageView.setImageResource(currentProduct.getIconResource() );
        holder.foodNameView.setText(currentProduct.getName() );
        holder.foodQuantityView.setText("Quantity: " + currentProduct.getQuantity() + "  (" + (int) currentProduct.getUnit_amount()
                                                     + " " + currentProduct.getUnit().getAbbrev() + " ea.)");
        String objectName;
        if(currentProduct.getCategory() == null)
        {
            objectName = "None";
        }
        else
            {
                objectName = currentProduct.getCategory().getName();
            }
        holder.foodCategoryView.setText("Category: " + objectName);
        holder.expirationView.setText("Expires on "+ DatabaseHelper.date_toAppStr(currentProduct.getExpiration_date()) );
        // currentProduct.getUnit().getAbbrev();    // use this to get abbrev string



        Date today = new Date();
        boolean hasExpiration = (currentProduct.getExpiration_date() != null);
        if(hasExpiration && today.after(currentProduct.getExpiration_date()) )      // if expires today, or already expired
        {
            holder.expiredImageView.setImageResource(R.drawable.ic_error_outline);
        }
        else
        {
            if(hasExpiration && currentProduct.getExpiration_date().getTime() - today.getTime() <= 345600000)   // 4 days
            {
                holder.expiredImageView.setImageResource(R.drawable.ic_warning);
            }
            else
            {
                holder.expiredImageView.setImageResource(0);
            }
        }


        boolean isExpanded = currentProduct.getExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public Filter getFilter()
    {
        return filter;
    }
    /**
     * Defines a new filter based on the search box input.
     * */
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
        /**
         * Updates the recycler view to contain only the filtered items
         * @param constraint the filter constraint
         * @param results the filter results
         * */
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            list.clear();
            list.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };


}//end Adapter
