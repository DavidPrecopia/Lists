package com.example.david.lists.ui.adapaters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.viewmodels.IGroupViewModelContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>
        implements IGroupAdapterContract {

    private final ArrayList<Group> groups;

    private final IGroupViewModelContract viewModel;
    private final ItemTouchHelper itemTouchHelper;
    private final ViewBinderHelper viewBinderHelper;

    public GroupAdapter(IGroupViewModelContract viewModel, ItemTouchHelper itemTouchHelper) {
        this.viewModel = viewModel;
        this.itemTouchHelper = itemTouchHelper;

        viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);

        groups = new ArrayList<>();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(
                ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int position) {
        groupViewHolder.bindView(groups.get(groupViewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public void swapData(List<Group> newGroups) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GroupDiffUtilCallback(this.groups, newGroups));
        groups.clear();
        groups.addAll(newGroups);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        Collections.swap(groups, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void remove(int position) {
        groups.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void reAdd(int position, Group group) {
        groups.add(position, group);
        notifyItemInserted(position);
    }


    final class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ListItemBinding binding;

        GroupViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Background view has its own click listener.
            binding.foregroundView.setOnClickListener(this);
        }


        void bindView(Group group) {
            bindTitle(group.getTitle());
            initBackgroundView(group.getId());
            initDragHandle();
            initPopupMenu();
            binding.executePendingBindings();
        }

        private void bindTitle(String title) {
            binding.tvTitle.setText(title);
        }

        private void initBackgroundView(String groupId) {
            // Ensures only one row can be opened at a time - see Adapter's constructor.
            viewBinderHelper.bind(binding.swipeRevealLayout, groupId);
            binding.backgroundView.setOnClickListener(view -> {
                viewModel.swipedLeft(GroupAdapter.this, getAdapterPosition());
                viewBinderHelper.closeLayout(groupId);
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

        private void initPopupMenu() {
            binding.ivOverflowMenu.setOnClickListener(view -> getPopupMenu().show());
        }

        private PopupMenu getPopupMenu() {
            PopupMenu popupMenu = new PopupMenu(binding.ivOverflowMenu.getContext(), binding.ivOverflowMenu);
            popupMenu.inflate(R.menu.popup_menu_list_item);
            popupMenu.setOnMenuItemClickListener(getMenuClickListener());
            return popupMenu;
        }

        private PopupMenu.OnMenuItemClickListener getMenuClickListener() {
            return item -> {
                switch (item.getItemId()) {
                    case R.id.menu_item_edit:
                        viewModel.edit(getAdapterPosition());
                        break;
                    case R.id.menu_item_delete:
                        viewModel.delete(GroupAdapter.this, getAdapterPosition());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                return true;
            };
        }


        @Override
        public void onClick(View v) {
            viewModel.groupClicked(
                    groups.get(getAdapterPosition())
            );
        }
    }


    final class GroupDiffUtilCallback extends DiffUtil.Callback {
        private final List<Group> oldList;
        private final List<Group> newList;

        GroupDiffUtilCallback(List<Group> oldList, List<Group> newList) {
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
