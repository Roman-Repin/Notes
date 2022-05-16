package com.example.notes;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import ui.DateFragment;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private CardSource cardSource;
    private FragmentNoteOutput fragmentNoteOutput;
    private int menuPosition;
    private OnItemClickListener listener;
    private Navigation navigation;
    private Publisher publisher;

    public Adapter(FragmentNoteOutput fragmentNoteOutput) {
        this.fragmentNoteOutput = fragmentNoteOutput;
    }

    public void setCardSource(CardSource cardSource) {
        this.cardSource = cardSource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
        MainActivity activity = (MainActivity) view.getContext();
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(cardSource.dataClass(position));
    }

    @Override
    public int getItemCount() {
        return cardSource.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titles;
        private final TextView descriptions;
        private final CardView cardView;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_cardView);
            titles = itemView.findViewById(R.id.item_title);
            descriptions = itemView.findViewById(R.id.item_description);
            date = itemView.findViewById(R.id.date);
            registerContextMenu(itemView);
            titles.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View view) {
                    menuPosition = getLayoutPosition();
                    itemView.showContextMenu(10, 10);
                    return true;
                }
            });
        }

        public void bind(DataClass dataClass) {
            titles.setText(dataClass.getNameNote());
            descriptions.setText(dataClass.getNoteDescription());
            date.setText(new SimpleDateFormat("dd-MM-yy").format(dataClass.getDate()));
            titles.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getLayoutPosition();
                    navigation.addFragment(DateFragment.newInstance(cardSource.dataClass(position)),
                            true);
                    publisher.subscribe(new Observer() {
                        @Override
                        public void updateDataClass(DataClass dataClass) {
                            cardSource.updateDataClass(position, dataClass);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

/*        public CardView getCardView() {
            return cardView;
        }*/
    }

    private void registerContextMenu(View itemView) {
        if (fragmentNoteOutput != null) {
            fragmentNoteOutput.registerForContextMenu(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public int getMenuPosition() {
        return menuPosition;
    }

    interface OnItemClickListener {
        public void OnItemClicked(DataClass dataClass, int position); // менял View view
    }
}
