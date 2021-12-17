package com.learn.sosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Toast;

public class UpdateEmergencyContacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_emergency_contacts);
        String reg = "^[+]?[0-9]{10,13}$";
        Toast.makeText(UpdateEmergencyContacts.this, "Open Activity", Toast.LENGTH_SHORT).show();
        Button save = findViewById(R.id.save);
        EditText con1 = findViewById(R.id.contact1);
        EditText con2 = findViewById(R.id.contact2);
        EditText con3 = findViewById(R.id.contact3);
        final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String contact1 = sharedpreferences.getString("contact1","").toString();
        con1.setText(contact1);
        String contact2 = sharedpreferences.getString("contact2","").toString();
        con2.setText(contact2);
        String contact3 = sharedpreferences.getString("contact3","").toString();
        con3.setText(contact3);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Pattern.matches(reg, con1.getText()) )
                    Toast.makeText(UpdateEmergencyContacts.this, "InValid Contact 1", Toast.LENGTH_SHORT).show();
                if(!Pattern.matches(reg, con2.getText()) )
                    Toast.makeText(UpdateEmergencyContacts.this, "InValid Contact 2", Toast.LENGTH_SHORT).show();
                if(!Pattern.matches(reg, con3.getText()) )
                    Toast.makeText(UpdateEmergencyContacts.this, "InValid Contact 3", Toast.LENGTH_SHORT).show();
                final String MyPREFERENCES = "MyPrefs" ;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("contact1", con1.getText().toString());
                editor.putString("contact2", con2.getText().toString());
                editor.putString("contact3", con3.getText().toString());
                editor.commit();


                Toast.makeText(UpdateEmergencyContacts.this, "Contacts Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}