package com.example.david.lists.ui.adapaters;

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
     * This is called post onMove <i>and</i> onSwiped.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        movementCallback.movedPermanently(viewHolder.getAdapterPosition());
    }
}
