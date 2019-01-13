package com.example.david.lists.ui.adapaters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.viewmodels.IUserListViewModelContract;
import com.example.david.lists.util.UtilExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.UserListViewHolder>
        implements IUserListAdapterContract {

    private final ArrayList<UserList> userLists;

    private final IUserListViewModelContract viewModel;
    private final ItemTouchHelper itemTouchHelper;
    private final ViewBinderHelper viewBinderHelper;

    public UserListsAdapter(IUserListViewModelContract viewModel, ItemTouchHelper itemTouchHelper) {
        this.viewModel = viewModel;
        this.itemTouchHelper = itemTouchHelper;

        viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);

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
        userListViewHolder.bindView(userLists.get(userListViewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }


    public void swapData(List<UserList> newUserLists) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UserListDiffUtilCallback(this.userLists, newUserLists));
        userLists.clear();
        userLists.addAll(newUserLists);
        diffResult.dispatchUpdatesTo(this);
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


    final class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ListItemBinding binding;

        UserListViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Background view has its own click listener.
            binding.foregroundView.setOnClickListener(this);
        }


        void bindView(UserList userList) {
            bindTitle(userList.getTitle());
            initBackgroundView(userList.getId());
            initDragHandle();
            initPopupMenu(userList.getId());
            binding.executePendingBindings();
        }

        private void bindTitle(String title) {
            binding.tvTitle.setText(title);
        }

        private void initBackgroundView(String userListId) {
            // Ensures only one row can be opened at a time - see Adapter's constructor.
            viewBinderHelper.bind(binding.swipeRevealLayout, userListId);
            binding.backgroundView.setOnClickListener(view -> {
                viewModel.swipedLeft(UserListsAdapter.this, getAdapterPosition());
                viewBinderHelper.closeLayout(userListId);
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        private void initDragHandle() {
            binding.ivDrag.setOnTouchListener((view, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            itemTouchHelper.startDrag(this);
                        }
                        view.performClick();
                        return true;
                    }
            );
        }

        private void initPopupMenu(String userListId) {
            binding.ivOverflowMenu.setOnClickListener(view -> getPopupMenu(userListId).show());
        }

        private PopupMenu getPopupMenu(String userListId) {
            PopupMenu popupMenu = new PopupMenu(binding.ivOverflowMenu.getContext(), binding.ivOverflowMenu);
            popupMenu.inflate(R.menu.popup_menu_list_item);
            popupMenu.setOnMenuItemClickListener(getMenuClickListener(userListId));
            return popupMenu;
        }

        private PopupMenu.OnMenuItemClickListener getMenuClickListener(String userListId) {
            return item -> {
                switch (item.getItemId()) {
                    case R.id.menu_item_edit:
                        viewModel.edit(getAdapterPosition());
                        break;
                    case R.id.menu_item_delete:
                        viewBinderHelper.openLayout(userListId);
                        viewModel.delete(UserListsAdapter.this, getAdapterPosition());
                        break;
                    default:
                        UtilExceptions.throwException(new IllegalArgumentException());
                }
                return true;
            };
        }


        @Override
        public void onClick(View v) {
            viewModel.userListClicked(
                    userLists.get(getAdapterPosition())
            );
        }
    }


    final class UserListDiffUtilCallback extends DiffUtil.Callback {
        private final List<UserList> oldList;
        private final List<UserList> newList;

        UserListDiffUtilCallback(List<UserList> oldList, List<UserList> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.toString().equals(newList.toString());
        }
    }
}
