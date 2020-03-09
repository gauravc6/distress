package com.gauravc6.distress;

import android.content.Intent;
import android.os.Bundle;

import com.gauravc6.distress.data.DatabaseHandler;
import com.gauravc6.distress.model.Contact;
import com.gauravc6.distress.ui.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = "ContactListActivity";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Contact> contactList;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton floatingActionButton;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Button saveButton;
    private EditText contactName, contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.contactListRecyclerView);
        databaseHandler = new DatabaseHandler(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton = findViewById(R.id.addContactFloatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Floating Button: clicked");
                createPopupDialog();
            }
        });

        contactList = new ArrayList<>();
        contactList = databaseHandler.getAllContacts();

        for (Contact contact: contactList) {
            Log.d(TAG, "Found contact: " + contact.getName());
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, contactList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void createPopupDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.contact_popup, null);
        contactName = view.findViewById(R.id.contactName);
        contactNumber = view.findViewById(R.id.contactNumber);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contactName.getText().toString().isEmpty()
                    && !contactNumber.getText().toString().isEmpty()
                    && contactNumber.getText().toString().length() == 10) {
                        saveContact(v);
                } else {
                    Snackbar.make(v, "Please enter valid details!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveContact(View view) {
        Contact contact = new Contact();
        String newContact = contactName.getText().toString().trim();
        Long newContactNumber = Long.parseLong(contactNumber.getText().toString().trim());

        contact.setName(newContact);
        contact.setContactNumber(newContactNumber);

        databaseHandler.addContact(contact);

        Snackbar.make(view, "Contact added!",Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        },1200);
    }

}
