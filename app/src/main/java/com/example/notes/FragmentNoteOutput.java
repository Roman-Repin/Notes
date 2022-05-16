package com.example.notes;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;

import ui.DateFragment;

public class FragmentNoteOutput extends Fragment {

    private static final String CURRENT_DESCRIPTION = "CurrentDescription";
    static final String ARG_INDEX = "index";
    private int currentPosition = 0;
    private CardSource cardSource;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private Navigation navigation;
    private Publisher publisher;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_note_output, container, false);
        return inflatedView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_DESCRIPTION, 0);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            int index = arguments.getInt(ARG_INDEX);
        }
        initView(view);
        setHasOptionsMenu(true);
        if (isLandscape()) {
            showLandFragment(currentPosition);
        }
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        initRecyclerView();
        cardSource = new CardSourceFirebaseImpl().init(new CardSourceResponse() {
            @Override
            public void initialized(CardSource cardSource) {
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setCardSource(cardSource);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        adapter = new Adapter(this);
        /*adapter.setCardSource(new CardSourceFirebaseImpl());*/
        adapter.setOnItemClickListener((dataClass, position) -> {
            showDescription(position, dataClass);
            /*navigation.addFragment(DateFragment.newInstance(), true);*/
        });
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.data_menu, menu);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.second_data_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                /*cardSource.clearDataClass();*/
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "notify сработал", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                navigation.addFragment(DateFragment.newInstance(), true);
                publisher.subscribe(dataClass -> {
                    cardSource.addDataClass(dataClass);
                    adapter.notifyItemInserted(cardSource.size());
                    recyclerView.smoothScrollToPosition(cardSource.size()-1);

                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();
        switch (item.getItemId()) {
            case R.id.action_update:
                navigation.addFragment(DateFragment.newInstance(cardSource.dataClass(position)),
                        true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateDataClass(DataClass dataClass) {
                        cardSource.updateDataClass(position, dataClass);
                        adapter.notifyItemChanged(position);
                    }
                });
                return true;
            case R.id.action_delete:
                cardSource.deleteDataClass(position);
                adapter.notifyItemRemoved(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showDescription(int index, DataClass dataClass) {
        if (isLandscape()) {
            showLandFragment(index);
        } else {
            showPortFragment(dataClass);
        }
    }

    private void showPortFragment(DataClass dataClass) {
        DateFragment fragment = DateFragment.newInstance(dataClass);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                /*//.addToBackStack("")*/
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void showLandFragment(int index) {
        FragmentNoteOutput fragmentNoteOutput = FragmentNoteOutput.newInstance(index);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.description_container, fragmentNoteOutput)
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_DESCRIPTION, currentPosition);
        super.onSaveInstanceState(outState);
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    @NotNull
    public static FragmentNoteOutput newInstance(int index) {
        // Создание фрагмента
        FragmentNoteOutput fragmentNoteOutput = new FragmentNoteOutput();
        // Передача параметра через бандл
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragmentNoteOutput.setArguments(args);
        return fragmentNoteOutput;
    }

    public static FragmentNoteOutput newInstance() {
        return new FragmentNoteOutput();
    }
}

