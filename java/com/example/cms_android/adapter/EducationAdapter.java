package com.example.cms_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Education;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {

    private List<Education> educations = new ArrayList<>();
    private OnEducationClickListener listener;
    private Map<Long, String> residentNames = new HashMap<>();

    @NonNull
    @Override
    public EducationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_education, parent, false);
        return new EducationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationViewHolder holder, int position) {
        Education education = educations.get(position);
        holder.bind(education, residentNames.get(education.getResidentId()));
        
        // 设置编辑按钮点击事件
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(education);
            }
        });

        // 设置删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(education);
            }
        });

        // 设置整个项目的点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(education);
            }
        });
    }

    @Override
    public int getItemCount() {
        return educations.size();
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
        notifyDataSetChanged();
    }

    public void setOnEducationClickListener(OnEducationClickListener listener) {
        this.listener = listener;
    }
    
    public void setResidentNames(Map<Long, String> residentNames) {
        this.residentNames = residentNames;
        notifyDataSetChanged();
    }

    static class EducationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvResidentName;
        private TextView tvEducationLevel;
        private TextView tvSchoolName;
        private TextView tvMajor;
        private TextView tvEnrollmentDate;
        private Button btnEdit;
        private Button btnDelete;

        public EducationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvEducationLevel = itemView.findViewById(R.id.tv_education_level);
            tvSchoolName = itemView.findViewById(R.id.tv_school_name);
            tvMajor = itemView.findViewById(R.id.tv_major);
            tvEnrollmentDate = itemView.findViewById(R.id.tv_enrollment_date);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Education education, String residentName) {
            // 显示居民姓名而不是ID
            if (residentName != null) {
                tvResidentName.setText(residentName);
            } else {
                tvResidentName.setText("居民ID: " + education.getResidentId());
            }
            tvEducationLevel.setText("教育程度: " + education.getEducationLevel());
            tvSchoolName.setText("学校名称: " + education.getSchoolName());
            tvMajor.setText("专业: " + education.getMajor());
            tvEnrollmentDate.setText("入学日期: " + education.getEnrollmentDate());
        }
    }

    public interface OnEducationClickListener {
        void onItemClick(Education education);
        void onEditClick(Education education);
        void onDeleteClick(Education education);
    }
}