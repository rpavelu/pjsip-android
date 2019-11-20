package com.ratushny.eltexsip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.pjsip_status_code;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_SIP_PORT = 5060;

    private BroadcastEventReceiver sipEvents = new BroadcastEventReceiver() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            Toast.makeText(MainActivity.this, registrationStateCode.toString(), Toast.LENGTH_SHORT).show();
            if (registrationStateCode.equals(pjsip_status_code.PJSIP_SC_OK)) {
                Intent successfulIntent = new Intent(MainActivity.this, SuccessfulStatusActivity.class);
                startActivity(successfulIntent);
            }
            registerButton.setEnabled(true);
            registerProgress.setVisibility(View.INVISIBLE);
        }
    };

    private EditText sipServerEditText;
    private EditText sipUsernameEditText;
    private EditText sipPasswordEditText;
    private Button registerButton;
    private ProgressBar registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        sipServerEditText = findViewById(R.id.sipServer);
        sipUsernameEditText = findViewById(R.id.username);
        sipPasswordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register);
        registerProgress = findViewById(R.id.registrationProgressBar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sipEvents.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sipEvents.register(this);
    }

    public void onRegister(View view) {
        try {
            SipAccountData sipAccount = new SipAccountData();

            String sipServerText = sipServerEditText.getText().toString();
            String sipUsernameText = sipUsernameEditText.getText().toString();
            String sipPasswordText = sipPasswordEditText.getText().toString();

            if (sipServerText.isEmpty() || sipUsernameText.isEmpty() || sipPasswordText.isEmpty()) {
                Toast.makeText(MainActivity.this, getString(R.string.error_no_data), Toast.LENGTH_SHORT).show();
            } else {
                registerButton.setEnabled(false);
                registerProgress.setVisibility(View.VISIBLE);
                sipAccount.setHost(sipServerText)
                        .setPort(DEFAULT_SIP_PORT)
                        .setTcpTransport(true)
                        .setUsername(sipUsernameText)
                        .setPassword(sipPasswordText)
                        .setRealm(sipServerText);
            }
            SipServiceCommand.setAccount(getApplicationContext(), sipAccount);
        } catch (Throwable e) {
            Log.i("MainActivity", "Exception: " + e.toString());
        }
    }
}