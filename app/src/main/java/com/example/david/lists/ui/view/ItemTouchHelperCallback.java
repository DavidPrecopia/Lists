package com.example.david.lists.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.david.lists.R;
import com.example.david.lists.ui.viewmodels.IViewModelContract;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final IViewModelContract viewModel;

    private boolean postMove = false;

    public ItemTouchHelperCallback(IViewModelContract viewModel) {
        this.viewModel = viewModel;
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
        }
    }

    /**
     * This is called post onMove <i>and</i> onSwiped
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (postMove) {
            viewModel.movedPermanently(viewHolder.getAdapterPosition());
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
                drawableToBitmap(context, R.drawable.ic_delete_white_16dp),
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


    private Bitmap drawableToBitmap(Context context, int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public interface IStartDragListener {
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }
}
