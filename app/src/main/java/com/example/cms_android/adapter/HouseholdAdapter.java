package com.example.cms_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Household;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.model.User;

import java.util.ArrayList;
import java.util.List;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.HouseholdViewHolder> {

    private List<Household> households = new ArrayList<>();
    private OnHouseholdClickListener listener;
    private User currentUser; // 添加当前用户字段

    @NonNull
    @Override
    public HouseholdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_household, parent, false);
        return new HouseholdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdViewHolder holder, int position) {
        Household household = households.get(position);
        holder.bind(household);
        
        // 根据用户权限控制按钮可见性
        if (currentUser != null) {
            // 编辑按钮对所有登录用户可见
            holder.btnEdit.setVisibility(View.VISIBLE);
            
            // 删除按钮仅对管理员可见
            if (PermissionManager.canDelete(currentUser)) {
                holder.btnDelete.setVisibility(View.VISIBLE);
            } else {
                holder.btnDelete.setVisibility(View.GONE);
            }
        } else {
            // 如果没有用户信息，默认隐藏所有操作按钮
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(household);
            }
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(household);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(household);
            }
        });
    }

    @Override
    public int getItemCount() {
        return households.size();
    }

    public void setHouseholds(List<Household> households) {
        this.households = households;
        notifyDataSetChanged();
    }

    public void setOnHouseholdClickListener(OnHouseholdClickListener listener) {
        this.listener = listener;
    }
    
    // 添加设置当前用户的方法
    public void setCurrentUser(User user) {
        this.currentUser = user;
        notifyDataSetChanged(); // 通知数据改变以刷新UI
    }

    static class HouseholdViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHouseholdNumber;
        private TextView tvAddress;
        private TextView tvHouseholderName;
        private TextView tvPopulationCount;
        private Button btnEdit;
        private Button btnDelete;

        public HouseholdViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHouseholdNumber = itemView.findViewById(R.id.tv_household_number);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvHouseholderName = itemView.findViewById(R.id.tv_householder_name);
            tvPopulationCount = itemView.findViewById(R.id.tv_population_count);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Household household) {
            tvHouseholdNumber.setText("户籍编号: " + household.getHouseholdNumber());
            tvAddress.setText("地址: " + household.getAddress());
            tvHouseholderName.setText("户主: " + household.getHouseholderName());
            tvPopulationCount.setText("人口: " + household.getPopulationCount() + "人");
        }
    }

    public interface OnHouseholdClickListener {
        void onItemClick(Household household);
        void onEditClick(Household household);
        void onDeleteClick(Household household);
    }
}