package com.example.cms_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Medical;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalAdapter extends RecyclerView.Adapter<MedicalAdapter.MedicalViewHolder>
{
    private List<Medical> medicals = new ArrayList<>();
    private OnMedicalClickListener listener;
    private Map<Long, String> residentNames = new HashMap<>(); // 存储居民ID到姓名的映射
    private User currentUser; // 添加当前用户字段

    public MedicalAdapter()
    {
    }

    @NonNull
    @Override
    public MedicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_record, parent, false);
        return new MedicalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalViewHolder holder, int position) {
        Medical medical = medicals.get(position);
        holder.bind(medical, listener, residentNames.get(medical.getResidentId()));
        
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
    }

    @Override
    public int getItemCount() {
        return medicals.size();
    }

    public void updateData(List<Medical> medicals) {
        this.medicals = medicals;
        notifyDataSetChanged();
    }

    // 更新居民姓名映射
    public void updateResidentNames(Map<Long, String> residentNames) {
        this.residentNames = residentNames;
        notifyDataSetChanged();
    }

    public void setOnMedicalClickListener(OnMedicalClickListener listener) {
        this.listener = listener;
    }
    
    // 添加设置当前用户的方法
    public void setCurrentUser(User user) {
        this.currentUser = user;
        notifyDataSetChanged(); // 通知数据改变以刷新UI
    }

    public static class MedicalViewHolder extends RecyclerView.ViewHolder {
        private TextView tvResidentName;
        private TextView tvVisitDate;
        private TextView tvHospital;
        private TextView tvDepartment;
        private TextView tvDiagnosis;
        private TextView tvDoctor;
        private TextView tvCost;
        private TextView tvInsurance;
        private Button btnEdit;
        private Button btnDelete;

        public MedicalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvVisitDate = itemView.findViewById(R.id.tv_visit_date);
            tvHospital = itemView.findViewById(R.id.tv_hospital);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            tvDiagnosis = itemView.findViewById(R.id.tv_diagnosis);
            tvDoctor = itemView.findViewById(R.id.tv_doctor);
            tvCost = itemView.findViewById(R.id.tv_cost);
            tvInsurance = itemView.findViewById(R.id.tv_insurance);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Medical medical, OnMedicalClickListener listener, String residentName) {
            // 显示居民姓名或ID
            if (residentName != null && !residentName.isEmpty()) {
                tvResidentName.setText(residentName);
            } else {
                tvResidentName.setText("居民ID: " + medical.getResidentId());
            }
            
            tvVisitDate.setText("就诊日期: " + medical.getLastCheckupDate());
            tvHospital.setText("医院: " + medical.getHospital());
            tvDepartment.setText("科室: " + medical.getDepartment());
            tvDiagnosis.setText("诊断: " + medical.getDiagnosis());
            tvDoctor.setText("主治医生: " + medical.getDoctor());
            tvCost.setText("总费用: " + medical.getCost() + "元");
            tvInsurance.setText("医保报销: " + medical.getInsurance() + "元");

            // 设置编辑按钮点击事件
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(medical);
                }
            });

            // 设置删除按钮点击事件
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(medical);
                }
            });

            // 设置整个项目的点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(medical);
                }
            });
        }
    }

    public interface OnMedicalClickListener {
        void onItemClick(Medical medical);
        void onEditClick(Medical medical);
        void onDeleteClick(Medical medical);
    }
}