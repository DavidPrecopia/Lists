package com.example.david.lists.util;

import android.app.Application;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class UtilInitializeListRecyclerView {

    private UtilInitializeListRecyclerView() {
    }

    public static void initRecyclerView(RecyclerView recyclerView,
                                        RecyclerView.Adapter adapter,
                                        ItemTouchHelper.SimpleCallback simpleCallback,
                                        Application application) {
        recyclerView.setHasFixedSize(true);
        initLayoutManager(recyclerView, application);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    private static void initLayoutManager(RecyclerView recyclerView, Application application) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(application);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
    }

    private static DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }
}
