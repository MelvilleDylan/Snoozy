package hackwestern3.snoozy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import hackwestern3.snoozy.R;

import java.io.Serializable;



public class settings extends AppCompatActivity implements Serializable {
    public int radius;
    private EditText rad_value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent menu_settings = getIntent();

        Button exit_settings;
        exit_settings = (Button) findViewById(R.id.settings_go_back);

        exit_settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences settings = getSharedPreferences("proximity_settings", MODE_PRIVATE);
        final SharedPreferences.Editor settingsEditor = settings.edit();
        radius=800;
        rad_value = (EditText) findViewById(R.id.radius_input);

        if (settings.contains("radius")) {
            radius = settings.getInt("radius", 800);
        }
        rad_value.setText(""+radius);
        rad_value.setSelection((""+radius).length());
        rad_value.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // you can call or do what you want with your EditText her

                try {
                    int value = (Integer.parseInt(rad_value.getText().toString()));
                    if (value > 10000) {
                        value = 10000;
                        rad_value.setText("10000");
                        rad_value.setSelection(5);
                    }
                    radius = value;
                }
                catch (Exception e) {
                    radius = 100;
                }
                settingsEditor.putInt("radius", radius);
                settingsEditor.commit();
                Log.d("EditText", ("Radius changed to "+radius));
            }
        });
    }

}