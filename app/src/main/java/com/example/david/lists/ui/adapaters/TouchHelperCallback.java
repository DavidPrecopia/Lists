package com.example.david.lists.ui.adapaters;

import android.graphics.Canvas;
import android.view.View;

import com.example.david.lists.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class TouchHelperCallback extends ItemTouchHelper.Callback {


    public interface MovementCallback {
        void dragging(int fromPosition, int toPosition);

        void movedPermanently(int newPosition);

        void swipedLeft(int position);
    }


    private final MovementCallback movementCallback;

    private boolean postMove = false;

    public TouchHelperCallback(MovementCallback movementCallback) {
        this.movementCallback = movementCallback;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
        );
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        postMove = true;
        movementCallback.dragging(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        postMove = false;
        final int position = viewHolder.getAdapterPosition();
        switch (direction) {
            case ItemTouchHelper.LEFT:
                movementCallback.swipedLeft(position);
                break;
            default:
                // intentionally left blank
                break;
        }
    }


    /**
     * This is called post onMove <i>and</i> onSwiped.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (postMove) {
            movementCallback.movedPermanently(viewHolder.getAdapterPosition());
        } else {
            getDefaultUIUtil().clearView(getForegroundView(viewHolder));
        }
    }


    /**
     * Returns the fraction that the user should move the View to be considered as swiped.
     * Default value is .5f.
     */
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 1.0f;
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && currentlySwiping(actionState)) {
            getDefaultUIUtil().onSelected(getForegroundView(viewHolder));
        } else {
            super.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (currentlySwiping(actionState)) {
            getDefaultUIUtil().onDraw(c, recyclerView, getForegroundView(viewHolder), dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (currentlySwiping(actionState)) {
            getDefaultUIUtil().onDrawOver(c, recyclerView, getForegroundView(viewHolder), dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private boolean currentlySwiping(int actionState) {
        return actionState == ItemTouchHelper.ACTION_STATE_SWIPE;
    }

    private View getForegroundView(@Nullable RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.findViewById(R.id.foreground_view);
    }
}
