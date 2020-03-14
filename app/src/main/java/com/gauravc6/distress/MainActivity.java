package com.gauravc6.distress;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.gauravc6.distress.data.DatabaseHandler;
import com.gauravc6.distress.model.Contact;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private String distressMessage;
    private ImageView sendDistress;
    private static int sms_request = 1;
    private List<Contact> contactList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("SettingPreferences",Context.MODE_PRIVATE);

        sendDistress = findViewById(R.id.send_distress);

        sendDistress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDistressAlert(v);
            }
        });

        sendDistress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this,"Send a distress alert!", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void sendDistressAlert(View view) {
        distressMessage = sharedPreferences.getString("DistressMessage", "I'm in danger!! HELP!!");

        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, sms_request);
        } else {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            contactList = databaseHandler.getAllContacts();
            try {
                SmsManager smsManager = SmsManager.getDefault();

                if (contactList.size() == 0) {
                    Toast.makeText(this, "You need to add at least 1 contact!", Toast.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "Sending distress alerts...", Snackbar.LENGTH_LONG).show();
                    for (Contact contact: contactList) {
                        smsManager.sendTextMessage(String.valueOf(contact.getContactNumber()),null, distressMessage,null,null);
                        Snackbar.make(view, "Distress Alert sent!", Snackbar.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.d("SMS", "sendDistressAlert: " + e);
                Toast.makeText(this,"Failed sending!",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grant) {
        super.onRequestPermissionsResult(requestCode, permissions, grant);
        if (requestCode == sms_request) {
            if (grant.length>0 && grant[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Click again to send distress!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission to send messages is denied.",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add_contact:
                Intent contactListActivity = new Intent(MainActivity.this, ContactListActivity.class);
                startActivity(contactListActivity);
                return false;

            case R.id.action_settings:
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
                return false;

            case R.id.action_exit:
                finish();
                return false;
        }
        return false;
    }
}
