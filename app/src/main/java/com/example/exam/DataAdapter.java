package com.example.exam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<String> messages;
    ArrayList<String> nicknames;
    LayoutInflater inflater;

    public DataAdapter(Context context, ArrayList<String> messages, ArrayList<String> nicknames) {
        this.nicknames = nicknames;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(messages.size()*nicknames.size() == 0)
            return;
        String msg = messages.get(position);
        String nick = nicknames.get(position);

        holder.message.setText(msg);
        holder.nickname.setText(nick);
    }

    @Override
    public int getItemCount() {
        return nicknames.size();
    }
}
