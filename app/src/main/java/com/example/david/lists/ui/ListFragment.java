package com.example.david.lists.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;

public class ListFragment extends Fragment {

    private FragmentListSharedBinding binding;

    private ListFragmentClickListener fragmentClickListener;

    public ListFragment() {
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init ViewModel
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_shared, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvError.setText("List");
        binding.fab.setOnClickListener(view -> fragmentClickListener.openDetailFragment(-1));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListFragmentClickListener) {
            fragmentClickListener = (ListFragmentClickListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement ListFragmentClickListener"
            );
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        fragmentClickListener = null;
    }


    interface ListFragmentClickListener {
        void openDetailFragment(int listId);
    }
}
