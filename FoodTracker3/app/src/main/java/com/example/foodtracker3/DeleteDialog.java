package com.example.foodtracker3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeleteDialog extends AppCompatDialogFragment
{
    int position;
    ArrayList<Product> productList;
    RecyclerView.Adapter adapter;
    DatabaseHelper dbh;

    public DeleteDialog(int position, ArrayList<Product> list, RecyclerView.Adapter adapter, DatabaseHelper dbh)
    {
        this.position = position;
        this.productList = list;
        this.adapter = adapter;
        this.dbh = dbh;
    }

    //private DeleteDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attention!")
                .setMessage("Do you want to delete the selected item?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //listener.onYesClicked();
                        removeItem(position, productList, adapter, dbh );
                    }
                });
        return builder.create();
    }//method OnCreateDialog

    /*public interface DeleteDialogListener
    {
        void onYesClicked();
    }//interface DeleteDialogListener

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

       try
        {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() + " must implement DeleteDialogListener");
        }
    }//method OnAttach*/

    public void removeItem(int position, ArrayList<Product> list, RecyclerView.Adapter adapter, DatabaseHelper dbh)
    {
        dbh.removeProduct(list.get(position));


        if(list != Adapter.listFull)
        {
            int index = Adapter.listFull.indexOf(list.get(position));
            if(index != -1)
            {
                Adapter.listFull.remove(index);
            }
        }
        list.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
