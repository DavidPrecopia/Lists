package com.example.david.lists.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.UserList;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.UserListViewHolder> {

    private ListFragment.ListFragmentClickListener fragmentClickListener;
    private List<UserList> userLists;

    UserListsAdapter(ListFragment.ListFragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
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

    void swapData(List<UserList> newUserLists) {
        this.userLists.clear();
        this.userLists.addAll(newUserLists);
        notifyDataSetChanged();
    }

    void removeUserList(int position) {
        userLists.remove(position);
        notifyItemRemoved(position);
    }

    UserList getData(int position) {
        return userLists.get(position);
    }


    final class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            UserList userList = userLists.get(getAdapterPosition());
            fragmentClickListener.openDetailFragment(
                    userList.getId(), userList.getTitle()
            );
        }
    }
}
