package com.gauravc6.distress;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;


public class SettingsActivity extends AppCompatActivity {

    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    EditText distressMessageEditText;
    Button saveButton;
    Switch toggleDistressConfirmation;
    String currentDistressMessage;
    boolean currentConfirmationToggleState;
    TextView cardDistressMessage;
    CardView changeDistressMessage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("SettingPreferences",Context.MODE_PRIVATE);

        currentConfirmationToggleState = sharedPreferences.getBoolean("DistressConfirmation",false);
        toggleDistressConfirmation = findViewById(R.id.confirmationToggle);
        toggleDistressConfirmation.setChecked(currentConfirmationToggleState);

        currentDistressMessage = sharedPreferences.getString("DistressMessage","I'm in danger!! HELP!!");
        cardDistressMessage = findViewById(R.id.alertText);
        cardDistressMessage.setText(currentDistressMessage);

        toggleDistressConfirmation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("DistressConfirmation",true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Ask confirmation: ON!", Toast.LENGTH_LONG).show();
                } else {
                    editor.putBoolean("DistressConfirmation",false);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Ask confirmation: OFF!", Toast.LENGTH_LONG).show();
                }
            }
        });

        changeDistressMessage = findViewById(R.id.changeAlertMessageCardView);
        changeDistressMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangeAlertMessagePopup(v);
            }
        });
    }

    private void createChangeAlertMessagePopup(View v) {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.change_distress_message_popup, null);
        distressMessageEditText = view.findViewById(R.id.distressMessageField);
        distressMessageEditText.setText(sharedPreferences.getString("DistressMessage","I'm in danger!! HELP!!"));
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!distressMessageEditText.getText().toString().isEmpty()) {
                    if (distressMessageEditText.getText().toString().length() <= 50) {
                        editor = sharedPreferences.edit();
                        editor.putString("DistressMessage",distressMessageEditText.getText().toString().trim());
                        editor.apply();
                        Snackbar.make(v, "Distress Message changed!", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        },1000);
                    } else {
                        Snackbar.make(v, "Distress Message must be less than 50 characters!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(v, "Distress Message cannot be empty!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }
}
