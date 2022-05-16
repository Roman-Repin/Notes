package ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.notes.DataClass;
import com.example.notes.FragmentNoteOutput;
import com.example.notes.MainActivity;
import com.example.notes.Navigation;
import com.example.notes.Publisher;
import com.example.notes.R;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Date;

public class DateFragment extends Fragment {

    private static final String ARG_DATA_CLASS = "Param_DataClass";

    private DataClass dataClass;
    private Publisher publisher;
    private TextInputEditText title;
    private TextInputEditText description;
    private DatePicker datePicker;
    private FragmentNoteOutput fragmentNoteOutput;
    private Navigation navigation;

    public static DateFragment newInstance(DataClass dataClass) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_CLASS, dataClass);
        fragment.setArguments(args);
        return fragment;
    }

    public static DateFragment newInstance() {
        DateFragment dateFragment = new DateFragment();
        return dateFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataClass = getArguments().getParcelable(ARG_DATA_CLASS);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        publisher = activity.getPublisher();
        navigation = activity.getNavigation();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtonBack(view);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        initView(view);
        setHasOptionsMenu(true);
        if (dataClass != null) {
            populateView();
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        dataClass = collectDataClass();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        publisher.notifySingle(dataClass);
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.description_fragment_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.confirm);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentNoteOutput fragment = FragmentNoteOutput.newInstance(/*getId()*/);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        /*.addToBackStack("")*/
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_clear).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private DataClass collectDataClass() {
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        Date date = getDateFromDatePicker();
        return new DataClass(title, description, date);
    }

    private Date getDateFromDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.datePicker.getYear());
        cal.set(Calendar.MONTH, this.datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, this.datePicker.getDayOfMonth());
        return cal.getTime();
    }

    private void initView(View view) {
        title = view.findViewById(R.id.inputTitle);
        description = view.findViewById(R.id.inputDescription);
        datePicker = view.findViewById(R.id.inputDate);
    }

    private void initButtonBack(View view) {
        Button buttonBackFnd = view.findViewById(R.id.button_back_fragment_note_description);
        buttonBackFnd.setOnClickListener(view1 -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        /*navigation.addFragment(FragmentNoteOutput.newInstance(), false);*/
    }

    private void populateView() {
        title.setText(dataClass.getNameNote());
        description.setText(dataClass.getNoteDescription());
        initDatePicker(dataClass.getDate());
    }

    private void initDatePicker(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null);
    }
}




