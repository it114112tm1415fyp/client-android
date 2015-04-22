package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import it114112tm1415fyp.com.util.PublicData;

public class Login_Activity extends Activity {

    private SharedPreferences sharePref;

    private ProgressDialog progressDialog;
    private EditText edTxtUsername, edTxtPassword;
    private Button btnLogin, btnRegister;
    private CheckBox chBxSaveMe;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharePref = getSharedPreferences("Logistic_System", Context.MODE_PRIVATE);
        username = sharePref.getString("username", "");
        password = sharePref.getString("password", "");
        setContentView(R.layout.login);
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            finish();
            startActivity(new Intent(getApplicationContext(), UserProfile_Activity.class));
        }
    }

    private void findAllView() {
        edTxtUsername = (EditText) findViewById(R.id.edTxtUsername);
        edTxtPassword = (EditText) findViewById(R.id.edTxtPassword);
        chBxSaveMe = (CheckBox) findViewById(R.id.chBxSaveMe);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    private void setAllView() {
        //Set CheckBox State
        if (sharePref.getInt("saveMe", 0) == 1) {
            chBxSaveMe.setChecked(true);
            edTxtUsername.setText(sharePref.getString("username", ""));
        }
        //Save CheckBox State
        chBxSaveMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                SharedPreferences.Editor editor = sharePref.edit();
                if (chBxSaveMe.isChecked()) {
                    editor.putInt("saveMe", 1);
                } else {
                    editor.putInt("saveMe", 0);
                }
                editor.apply();
                Log.d("Save Me", sharePref.getInt("saveMe", 0) + "");
            }
        });
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String format = getResources().getString(R.string.format);
                        if (edTxtUsername.getText().toString().equals("") || edTxtPassword.getText().toString().equals("")) {
                            Toast.makeText(Login_Activity.this, "Username and password can not be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!edTxtUsername.getText().toString().matches(format) || !edTxtPassword.getText().toString().matches(format)) {
                            Toast.makeText(Login_Activity.this, "Only accept 0-9, a-z, A-Z", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        username = edTxtUsername.getText().toString();
                        password = edTxtPassword.getText().toString();
                        // Check network state
                        if (!PublicData.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Send send login request
                        progressDialog = ProgressDialog.show(Login_Activity.this, "Loading...", "Sending Request...", true);
                        new Thread(SendLoginRequest).start();
                    }
                }
        );
        btnRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        startActivityForResult(new Intent(Login_Activity.this, Register_Activity.class), 0);
                    }
                }

        );
    }

    Thread SendLoginRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.login(username, password);
            try {
                if (result != null) {
                    btnLogin.setClickable(true);
                    if (result.getBoolean("success")) {
                        PublicData.username = username;
                        PublicData.password = password;
                        PublicData.logined = 1;
                        JSONObject content = result.getJSONObject("content");
                        PublicData.userId = content.getString("id");
                        PublicData.realName = content.getString("name");
                        PublicData.email = content.getString("email");
                        PublicData.phone = content.getString("phone");
                        PublicData.register_time = DateFormator.getDateTime(content.getString("created_at"));
                        PublicData.last_modify_time = DateFormator.getDateTime(content.getString("updated_at"));
                        PublicData.UserAddresses = content.getJSONArray("specify_addresses");
                        SharedPreferences.Editor editor = sharePref.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                startActivity(new Intent(Login_Activity.this, UserProfile_Activity.class));
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(Login_Activity.this, result.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Fail to server. \nPlease try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };
}
