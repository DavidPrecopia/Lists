package com.example.david.lists.ui.userlistlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.ViewHolderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UserListsAdapterImpl extends ListAdapter<UserList, UserListsAdapterImpl.UserListViewHolder>
        implements IUserListAdapter {

    private final ArrayList<UserList> userLists;

    private final IUserListViewModel viewModel;
    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public UserListsAdapterImpl(IUserListViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        super(new UserListDiffCallback());
        userLists = new ArrayList<>();
        this.viewModel = viewModel;
        this.viewBinderHelper = viewBinderHelper;
        this.itemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListViewHolder(
                ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),
                viewBinderHelper,
                itemTouchHelper
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, int position) {
        // I am using `getItem()`, not the List field, because when the entire list is updated,
        // the RecyclerView's internal list is updated before the field is.
        UserList userList = getItem(position);
        userListViewHolder.bindView(userList.getId(), userList.getTitle());
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

    @Override
    public void move(int fromPosition, int toPosition) {
        Collections.swap(userLists, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void remove(int position) {
        userLists.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void reAdd(int position, UserList userList) {
        userLists.add(position, userList);
        notifyItemInserted(position);
    }


    final class UserListViewHolder extends ViewHolderBase implements View.OnClickListener {

        UserListViewHolder(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
            super(binding, viewBinderHelper, itemTouchHelper);
            // Background view has its own click listener.
            binding.foregroundView.setOnClickListener(this);
        }

        @Override
        public void swipedLeft(int adapterPosition) {
            viewModel.swipedLeft(UserListsAdapterImpl.this, adapterPosition);
        }

        @Override
        public void edit(int adapterPosition) {
            viewModel.edit(getItem(adapterPosition));
        }

        @Override
        public void delete(int adapterPosition) {
            viewModel.delete(UserListsAdapterImpl.this, adapterPosition);
        }

        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }
}
