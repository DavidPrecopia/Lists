package com.example.david.lists.widget.configactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.databinding.WidgetConfigListItemBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class WidgetConfigAdapter extends RecyclerView.Adapter<WidgetConfigAdapter.WidgetConfigViewHolder> {

    private final List<Group> groups;

    private final IWidgetConfigViewModelContract viewModel;

    WidgetConfigAdapter(IWidgetConfigViewModelContract viewModel) {
        this.groups = new ArrayList<>();
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public WidgetConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WidgetConfigViewHolder(
                WidgetConfigListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetConfigViewHolder holder, int position) {
        holder.bindView(groups.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    void swapData(List<Group> newGroups) {
        groups.clear();
        groups.addAll(newGroups);
        notifyDataSetChanged();
    }


    final class WidgetConfigViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final WidgetConfigListItemBinding binding;

        WidgetConfigViewHolder(WidgetConfigListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        private void bindView(Group group) {
            binding.setGroup(group);
        }

        @Override
        public void onClick(View v) {
            viewModel.groupClicked(
                    groups.get(getAdapterPosition())
            );
        }
    }
}