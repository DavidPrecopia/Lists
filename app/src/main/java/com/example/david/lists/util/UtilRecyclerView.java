package com.example.david.lists.util;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class UtilRecyclerView {
    private UtilRecyclerView() {
    }

    public static void initLayoutManager(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
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
