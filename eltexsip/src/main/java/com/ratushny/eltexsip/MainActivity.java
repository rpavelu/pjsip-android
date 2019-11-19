package com.ratushny.eltexsip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.pjsip_status_code;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SIP_ACCOUNT = "sip_account";
    private static final int DEFAULT_SIP_PORT = 5060;

    private SipAccountData sipAccount;

    private BroadcastEventReceiver sipEvents = new BroadcastEventReceiver() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            Toast.makeText(MainActivity.this, registrationStateCode.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private EditText sipServerEditText;
    private EditText sipUsernameEditText;
    private EditText sipPasswordEditText;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_SIP_ACCOUNT, sipAccount);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            sipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT);
        }
    }

    private void bindViews() {
        sipServerEditText = findViewById(R.id.sipServer);
        sipUsernameEditText = findViewById(R.id.username);
        sipPasswordEditText = findViewById(R.id.password);
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
            sipAccount = new SipAccountData();

            String sipServerText = sipServerEditText.getText().toString();
            String sipUsernameText = sipUsernameEditText.getText().toString();
            String sipPasswordText = sipPasswordEditText.getText().toString();

            if (sipServerText.isEmpty()) {
                Toast.makeText(MainActivity.this, getString(R.string.error_no_data), Toast.LENGTH_SHORT).show();
            } else {
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
