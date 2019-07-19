package com.example.david.lists.view.userlistlist;

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
import com.example.david.lists.view.common.ListItemViewHolderBase;

import java.util.List;

public final class UserListAdapter extends ListAdapter<UserList, UserListAdapter.UserListViewHolder>
        implements IUserListViewContract.Adapter {

    private IUserListViewContract.Logic logic;

    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public UserListAdapter(ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        super(new UserListDiffCallback());
        this.viewBinderHelper = viewBinderHelper;
        this.itemTouchHelper = itemTouchHelper;
    }

    @Override
    public void init(IUserListViewContract.Logic logic) {
        this.logic = logic;
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
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void remove(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void reAdd(int position, UserList userList) {
        notifyItemInserted(position);
    }


    final class UserListViewHolder extends ListItemViewHolderBase implements View.OnClickListener {

        UserListViewHolder(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
            super(binding, viewBinderHelper, itemTouchHelper);
            // Background view has its own click listener.
            binding.foregroundView.setOnClickListener(this);
        }

        @Override
        protected void swipedLeft(int adapterPosition) {
            logic.delete(adapterPosition);
        }

        @Override
        protected void edit(int adapterPosition) {
            logic.edit(getItem(adapterPosition));
        }

        @Override
        protected void delete(int adapterPosition) {
            logic.delete(adapterPosition);
        }

        @Override
        public void onClick(View v) {
            logic.userListSelected(getAdapterPosition());
        }
    }
}
