package hackwestern3.snoozy;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

/**
 * Created by Dylan on 2016-10-15.
 */

public class alarm_popup extends Activity{
    private View dismiss_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent alarm = getIntent();


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone sound = RingtoneManager.getRingtone(getApplicationContext(), notification);

        sound.play();
        setContentView(R.layout.activity_alarm);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        dismiss_button = findViewById(R.id.dismiss_button);

        dismiss_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sound.stop();
                onBackPressed();
            }
        });
    }
}
