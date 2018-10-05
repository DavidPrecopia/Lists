package com.example.david.lists.ui.adapaters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.ui.viewmodels.IListViewModelContract;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.UserListViewHolder> {

    private final List<UserList> userLists;
    private final IListViewModelContract viewModel;

    public UserListsAdapter(IListViewModelContract viewModel) {
        this.viewModel = viewModel;
        userLists = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListViewHolder(
                ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, int position) {
        userListViewHolder.bindView(
                userLists.get(userListViewHolder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }


    public void swapData(List<UserList> newUserLists) {
        userLists.clear();
        userLists.addAll(newUserLists);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        userLists.remove(position);
        notifyItemRemoved(position);
    }

    public void reAdd(int position, UserList userList) {
        userLists.add(position, userList);
        notifyItemInserted(position);
    }


    final class UserListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ListItemBinding binding;

        UserListViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        void bindView(UserList userList) {
            binding.tvTitle.setText(userList.getTitle());
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }
}
