package com.example.david.lists.ui.view;

import com.example.david.lists.ui.viewmodels.IListViewModelContract;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public final class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final IListViewModelContract viewModel;

    private boolean postMove = false;

    public ItemTouchHelperCallback(IListViewModelContract viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT
        );
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        postMove = true;
        viewModel.dragging(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        postMove = false;
        final int position = viewHolder.getAdapterPosition();
        switch (direction) {
            case ItemTouchHelper.LEFT:
                viewModel.swipedLeft(position);
                break;
            case ItemTouchHelper.RIGHT:
                viewModel.swipedRight(position);
                break;
        }
    }


    /**
     * This is called post onMove <i>and</i> onSwiped
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Timber.d("clearView");
        super.clearView(recyclerView, viewHolder);
        if (postMove) {
            viewModel.movePermanently(viewHolder.getAdapterPosition());
        }
    }


    public interface IStartDragListener {
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }
}
