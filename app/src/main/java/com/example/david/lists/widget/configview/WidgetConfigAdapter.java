package com.example.david.lists.widget.configview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.WidgetConfigListItemBinding;
import com.example.david.lists.view.userlistlist.UserListDiffCallback;

import java.util.ArrayList;
import java.util.List;

public final class WidgetConfigAdapter extends ListAdapter<UserList, WidgetConfigAdapter.WidgetConfigViewHolder>
        implements IWidgetConfigContract.Adapter {

    private final List<UserList> userLists;

    private final IWidgetConfigContract.ViewModel viewModel;

    public WidgetConfigAdapter(IWidgetConfigContract.ViewModel viewModel) {
        super(new UserListDiffCallback());
        this.userLists = new ArrayList<>();
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
        holder.bindView(getItem(position));
    }

    @Override
    public void submitList(@Nullable List<UserList> list) {
        super.submitList(list);
        // In case the exact same List is submitted twice
        if (this.userLists != list) {
            userLists.clear();
            userLists.addAll(list);
        }
    }


    final class WidgetConfigViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final WidgetConfigListItemBinding binding;

        WidgetConfigViewHolder(WidgetConfigListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        private void bindView(UserList userList) {
            binding.tvTitle.setText(userList.getTitle());
        }

        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }
}