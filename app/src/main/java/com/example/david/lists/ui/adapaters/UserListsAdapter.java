package com.example.david.lists.ui.adapaters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.view.ItemTouchHelperCallback;
import com.example.david.lists.ui.viewmodels.IViewModelContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.david.lists.util.UtilRecyclerView.getDragTouchListener;
import static com.example.david.lists.util.UtilRecyclerView.getPopupMenu;

public final class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.UserListViewHolder> {

    private final List<UserList> userLists;

    private final IViewModelContract viewModel;
    private final ItemTouchHelperCallback.IStartDragListener startDragListener;

    public UserListsAdapter(
            IViewModelContract viewModel,
            ItemTouchHelperCallback.IStartDragListener startDragListener) {
        this.viewModel = viewModel;
        this.startDragListener = startDragListener;
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

    public void move(int fromPosition, int toPosition) {
        Collections.swap(userLists, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
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
            bindTitle(userList);
            initDragHandle();
            initPopupMenu();
            binding.executePendingBindings();
        }

        private void bindTitle(UserList userList) {
            binding.tvTitle.setText(userList.getTitle());
        }

        private void initDragHandle() {
            binding.ivDrag.setOnTouchListener(
                    getDragTouchListener(this, startDragListener)
            );
        }

        private void initPopupMenu() {
            PopupMenu popupMenu = getPopupMenu(
                    getAdapterPosition(), binding.ivOverflowMenu, viewModel
            );
            binding.ivOverflowMenu.setOnClickListener(view -> popupMenu.show());
        }


        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }
}
