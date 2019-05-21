package com.example.david.lists.ui;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.R;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.util.UtilExceptions;

public abstract class ViewHolderBase extends RecyclerView.ViewHolder {

    private String id;
    private String title;

    private final ListItemBinding binding;
    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public ViewHolderBase(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        super(binding.getRoot());
        this.binding = binding;
        this.viewBinderHelper = viewBinderHelper;
        this.itemTouchHelper = itemTouchHelper;
    }

    public void bindView(String id, String title) {
        this.id = id;
        this.title = title;
        bindTitle();
        initBackgroundView();
        initDragHandle();
        initPopupMenu();
        binding.executePendingBindings();
    }


    public abstract void swipedLeft(int adapterPosition);

    public abstract void edit(int adapterPosition);

    public abstract void delete(int adapterPosition);


    private void bindTitle() {
        binding.tvTitle.setText(title);
    }

    private void initBackgroundView() {
        // Ensures only one row can be opened at a time - see Adapter's constructor.
        viewBinderHelper.bind(binding.swipeRevealLayout, id);
        binding.backgroundView.setOnClickListener(view -> {
            this.swipedLeft(getAdapterPosition());
            viewBinderHelper.closeLayout(id);
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
                    this.edit(getAdapterPosition());
                    break;
                case R.id.menu_item_delete:
                    viewBinderHelper.openLayout(id);
                    this.delete(getAdapterPosition());
                    break;
                default:
                    UtilExceptions.throwException(new IllegalArgumentException());
            }
            return true;
        };
    }
}
