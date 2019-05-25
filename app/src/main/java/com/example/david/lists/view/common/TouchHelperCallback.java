package com.example.david.lists.view.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class TouchHelperCallback extends ItemTouchHelper.Callback {


    public interface MovementCallback {
        void dragging(int fromPosition, int toPosition);

        void movedPermanently(int newPosition);
    }


    private final MovementCallback movementCallback;

    public TouchHelperCallback(MovementCallback movementCallback) {
        this.movementCallback = movementCallback;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        );
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        movementCallback.dragging(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }


    /**
     * This is called post {@link TouchHelperCallback#onMove(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)}
     * and {@link TouchHelperCallback#onSwiped(RecyclerView.ViewHolder, int)}.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        movementCallback.movedPermanently(viewHolder.getAdapterPosition());
    }
}
