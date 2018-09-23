package com.example.david.lists.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;

public class DetailFragment extends Fragment {
    private static final String ARG_PARAM_LIST_ID = "list_id_key";

    private FragmentListSharedBinding binding;


    public DetailFragment() {
    }

    public static DetailFragment newInstance(int listId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_LIST_ID, listId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO initialize ViewModel
        // getArguments().getInt(ARG_PARAM_LIST_ID)
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_shared, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvError.setText("Detail");
        binding.fab.setOnClickListener(view -> Snackbar.make(binding.coordinatorLayout, "FAB clicked in Detail", Snackbar.LENGTH_SHORT).show());
    }
}
