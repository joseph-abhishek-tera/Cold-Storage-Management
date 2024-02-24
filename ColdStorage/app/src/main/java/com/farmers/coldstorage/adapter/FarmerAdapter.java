package com.farmers.coldstorage.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farmers.coldstorage.databinding.ItemFarmerStorageBinding;
import com.farmers.coldstorage.model.FarmerModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.ViewHolder> {



    private Context context;
    private ArrayList<FarmerModel> list = new ArrayList<>();


    public FarmerAdapter(Context context, ArrayList<FarmerModel> list) {
        this.context = context;
        this.list = list;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFarmerStorageBinding binding = ItemFarmerStorageBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFarmerStorageBinding binding;
        public ViewHolder(@NonNull ItemFarmerStorageBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        @SuppressLint("SetTextI18n")
        void bind(FarmerModel model){
            binding.name.setText(model.getStorage_name());
            binding.type.setText(model.getRequired_type());
            if (model.getQuantity_type().equals("BAG")){
                binding.requiredSpace.setText(model.getSpace_required()+" BAG's");
            }else {
                binding.requiredSpace.setText(model.getSpace_required()+"KG");
            }
            binding.roomId.setText(String.valueOf(model.getRoom_id()));
//
            Date date = new Date(model.getTimestamp());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()); // You can customize the format
            String formattedDate = dateFormat.format(date);
            binding.date.setText(formattedDate);

            binding.location.setText(model.getLocation());
            double perItem = model.getTotal_price() / model.getSpace_required();
            if (model.getQuantity_type().equals("BAG")){
                binding.totalAmount.setText("₹"+model.getTotal_price() + "  ( ₹"+perItem+" /Bag)");

            }else {
                binding.totalAmount.setText("₹"+model.getTotal_price() + "   ( ₹"+perItem+" /Kg)");

            }

        }

    }
}
