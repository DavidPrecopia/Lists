package com.example.david.lists.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.david.lists.R;
import com.example.david.lists.ui.viewmodels.IListViewModelContract;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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


    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            return;
        }

        Context context = recyclerView.getContext();
        View itemView = viewHolder.itemView;
        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;

        if (dX > 0) {
            edit(canvas, dX, context, itemView, width);
        } else if (dX < 0) {
            delete(canvas, dX, context, itemView, width);
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    private void edit(Canvas canvas, float dX, Context context, View itemView, float width) {
        Paint paint = new Paint();
        paint.setColor(getColor(context, R.color.colorPrimaryLight));

        canvas.drawRect(getEditBackground(dX, itemView), paint);
        canvas.drawBitmap(
                drawableToBitmap(context, R.drawable.ic_edit_white_16dp),
                null,
                getEditIconDestination(itemView, width),
                paint
        );
    }

    private RectF getEditBackground(float dX, View itemView) {
        return new RectF(
                (float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom()
        );
    }

    private RectF getEditIconDestination(View itemView, float width) {
        return new RectF(
                (float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width
        );
    }


    private void delete(Canvas canvas, float dX, Context context, View itemView, float width) {
        Paint paint = new Paint();
        paint.setColor(getColor(context, R.color.red));

        canvas.drawRect(getDeleteBackground(dX, itemView), paint);
        canvas.drawBitmap(
                drawableToBitmap(context, R.drawable.ic_delete_white_16dp),
                null,
                getDeleteIconDestination(itemView, width),
                paint
        );
    }

    private RectF getDeleteBackground(float dX, View itemView) {
        return new RectF(
                (float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom()
        );
    }

    private RectF getDeleteIconDestination(View itemView, float width) {
        return new RectF(
                (float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width
        );
    }


    private int getColor(Context context, int red) {
        return ContextCompat.getColor(context, red);
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
