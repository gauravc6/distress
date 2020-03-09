package com.gauravc6.distress.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Contact> contactList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;


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

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position;
            position = getAdapterPosition();
            Contact contact = contactList.get(position);

            switch(v.getId()) {
                case R.id.editButton:
                    editContact(contact);
                    break;
                case R.id.deleteButton:
                    deleteContact(contact.getId());
                    break;
            }

        }

        private void editContact(final Contact contact) {

            builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.contact_popup, null);
            Button saveButton;
            TextView title;

            contactName = view.findViewById(R.id.contactName);
            contactNumber = view.findViewById(R.id.contactNumber);
            saveButton = view.findViewById(R.id.saveButton);
            title = view.findViewById(R.id.title);

            title.setText(R.string.edit_contact);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            contactName.setText(contact.getName());
            contactNumber.setText(String.valueOf(contact.getContactNumber()));

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    contact.setName(contactName.getText().toString());
                    contact.setContactNumber(Long.parseLong(contactNumber.getText().toString()));

                    if (!contactName.getText().toString().isEmpty()
                            && !contactNumber.getText().toString().isEmpty()
                            && contactNumber.getText().toString().length() == 10) {
                        databaseHandler.updateContact(contact);
                        notifyItemChanged(getAdapterPosition());
                        dialog.dismiss();
                    } else {
                        Snackbar.make(v, "Please enter valid details!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        }

        private void deleteContact(final int id) {
            builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to delete this contact?").setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseHandler db = new DatabaseHandler(context);
                            db.deleteContact(id);
                            contactList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
            });
            dialog = builder.create();
            dialog.setTitle("Delete Contact");
            dialog.show();
        }
    }
}
