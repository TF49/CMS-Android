package com.example.cms_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Resident;

import java.util.ArrayList;
import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private List<Resident> residents = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resident, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        Resident resident = residents.get(position);
        holder.bind(resident);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(resident);
            }
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(resident);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(resident);
            }
        });
    }

    @Override
    public int getItemCount() {
        return residents.size();
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ResidentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvIdCard;
        private TextView tvGender;
        private TextView tvBirthDate;
        private TextView tvPhoneNumber;
        private Button btnEdit;
        private Button btnDelete;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvIdCard = itemView.findViewById(R.id.tv_id_card);
            tvGender = itemView.findViewById(R.id.tv_gender);
            tvBirthDate = itemView.findViewById(R.id.tv_birth_date);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Resident resident) {
            tvName.setText("姓名: " + resident.getName());
            tvIdCard.setText("身份证号: " + resident.getIdCard());
            tvGender.setText("性别: " + resident.getGender());
            tvBirthDate.setText("出生日期: " + resident.getBirthDate());
            tvPhoneNumber.setText("联系电话: " + resident.getPhoneNumber());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Resident resident);
        void onEditClick(Resident resident);
        void onDeleteClick(Resident resident);
    }
}