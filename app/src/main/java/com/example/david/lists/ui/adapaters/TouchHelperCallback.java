package com.example.david.lists.ui.adapaters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.david.lists.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
     * Returns the fraction that the user should move the View to be considered as swiped.
     * Default value is .5f.
     */
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 1.0f;
    }


    /**
     * This is called post onMove <i>and</i> onSwiped.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (postMove) {
            movementCallback.movedPermanently(viewHolder.getAdapterPosition());
        }
    }


    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            return;
        }

        View itemView = viewHolder.itemView;
        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;

        if (dX < 0) {
            delete(canvas, dX, recyclerView.getContext(), itemView, width);
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    private void delete(Canvas canvas, float dX, Context context, View itemView, float width) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.red));

        canvas.drawRect(getBackground(dX, itemView), paint);
        canvas.drawBitmap(
                drawableToBitmap(context),
                null,
                getIconDestination(itemView, width),
                paint
        );
    }

    private RectF getBackground(float dX, View itemView) {
        return new RectF(
                (float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom()
        );
    }

    private RectF getIconDestination(View itemView, float width) {
        return new RectF(
                (float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width
        );
    }


    private Bitmap drawableToBitmap(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_16dp);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
