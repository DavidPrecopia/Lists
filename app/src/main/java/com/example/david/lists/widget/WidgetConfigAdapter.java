package com.example.david.lists.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.databinding.WidgetListItemBinding;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.ui.viewmodels.IViewModelContract;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class WidgetConfigAdapter extends RecyclerView.Adapter<WidgetConfigAdapter.WidgetConfigViewHolder> {

    private final List<UserList> userLists;

    private final IViewModelContract viewModel;

    WidgetConfigAdapter(IViewModelContract viewModel) {
        this.userLists = new ArrayList<>();
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public WidgetConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WidgetConfigViewHolder(
                WidgetListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetConfigViewHolder holder, int position) {
        holder.bindView(userLists.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    void swapData(List<UserList> newUserLists) {
        userLists.clear();
        userLists.addAll(newUserLists);
        notifyDataSetChanged();
    }


    final class WidgetConfigViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final WidgetListItemBinding binding;

        WidgetConfigViewHolder(WidgetListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        private void bindView(UserList userList) {
            binding.widgetListItemTvTitle.setText(userList.getTitle());
        }

        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }
}