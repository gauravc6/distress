package com.gauravc6.distress.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gauravc6.distress.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravc6.distress.data.DatabaseHandler;
import com.gauravc6.distress.model.Contact;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Contact> contactList;

    public RecyclerViewAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        Contact contact = contactList.get(position);
        holder.contactName.setText(MessageFormat.format("Contact Name: {0}", contact.getName()));
        holder.contactNumber.setText(MessageFormat.format("Contact Number: {0}", String.valueOf(contact.getContactNumber())));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contactName, contactNumber;
        public int id;
        public Button editButton, deleteButton;


        public ViewHolder(@NonNull View contactView, Context ctx) {
            super(contactView);
            context = ctx;

            contactName = contactView.findViewById(R.id.contact_name);
            contactNumber = contactView.findViewById(R.id.contact_number);

            editButton = contactView.findViewById(R.id.editButton);
            deleteButton = contactView.findViewById(R.id.deleteButton);
        }

        @Override
        public void onClick(View v) {
            int position;

            switch(v.getId()) {
                case R.id.editButton:
                    // TODO: edit contact
                    break;
                case R.id.deleteButton:
                    position = getAdapterPosition();
                    Contact contact = contactList.get(position);
                    deleteContact(contact.getId());
                    break;
            }

        }

        private void deleteContact(int id) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.deleteContact(id);
            contactList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
