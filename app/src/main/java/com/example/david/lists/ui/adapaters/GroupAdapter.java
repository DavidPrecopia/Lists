package com.example.david.lists.ui.adapaters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.data.datamodel.Group;
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

public final class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<Group> groups;

    private final IViewModelContract viewModel;
    private final ItemTouchHelperCallback.IStartDragListener startDragListener;

    public GroupAdapter(
            IViewModelContract viewModel,
            ItemTouchHelperCallback.IStartDragListener startDragListener) {
        this.viewModel = viewModel;
        this.startDragListener = startDragListener;
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
        groupViewHolder.bindView(
                groups.get(groupViewHolder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public void swapData(List<Group> newGroups) {
        groups.clear();
        groups.addAll(newGroups);
        notifyDataSetChanged();
    }

    public void move(int fromPosition, int toPosition) {
        Collections.swap(groups, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void remove(int position) {
        groups.remove(position);
        notifyItemRemoved(position);
    }

    public void reAdd(int position, Group group) {
        groups.add(position, group);
        notifyItemInserted(position);
    }


    final class GroupViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ListItemBinding binding;

        GroupViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        void bindView(Group group) {
            bindTitle(group);
            initDragHandle();
            initPopupMenu();
            binding.executePendingBindings();
        }

        private void bindTitle(Group group) {
            binding.tvTitle.setText(group.getTitle());
        }

        @SuppressLint("ClickableViewAccessibility")
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
            viewModel.groupClicked(
                    groups.get(getAdapterPosition())
            );
        }
    }
}
