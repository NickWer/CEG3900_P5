package com.ceg3900.nick.passcheck;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCalculate = (Button)findViewById(R.id.btnCalculate);
        TextView lblStrength = (TextView) findViewById(R.id.lblStrength);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);


        class CalculatePassowrdStrength extends AsyncTask<String, Long, Long> {

            @Override
            protected Long doInBackground(String... params) {
                final String target = params[0].toLowerCase();

                BufferedReader reader = null;
                try {
                    InputStream is = getApplicationContext().getAssets().open("rockyou.txt");
                    reader = new BufferedReader(new InputStreamReader(is));
                } catch (IOException e) {
                    e.printStackTrace();
                    return 999999999L; //Better safe than sorry I suppose
                }

                return reader.lines().parallel().filter(line -> {
                    Boolean matches = false;

                    if(line.toLowerCase().contains(target))
                        matches = true;

                    if(target.contains(line))
                        matches = true;

                    return matches;
                }).count();
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Long count) {
                count -= 1;
                super.onPostExecute(count);
                String output = "Your password isn't similar to any in our list, and is probably strong" +
                        " enough to be considered secure";

                if(count > 0)
                 output = "Your password is similar to " + Long.toString(count) +
                        " items in known PW lists. Any number greater than 0 should be extremely" +
                        " concerning!";

                lblStrength.setText(output);
            }
        }


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatePassowrdStrength c = new CalculatePassowrdStrength();
                lblStrength.setText("Calculating... Please wait");
                c.execute(txtPassword.getText().toString());
            }
        });
    }
}
