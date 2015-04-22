package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import it114112tm1415fyp.com.util.PublicData;

public class UserProfile_Activity extends Activity {

    private SharedPreferences sharePref;

    private LinearLayout layLinearAddress;
    private TextView txtViewId, txtViewUserName, txtViewRealName, txtViewPhone, txtViewEmail, txtViewRegisterTime, txtViewLastModifyTime;
    private Button btnEdit, btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharePref = getSharedPreferences("Logistic_System",
                Context.MODE_PRIVATE);
        setContentView(R.layout.user_profile);
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setTextView();
        }
    }

    public void findAllView() {
        layLinearAddress = (LinearLayout) findViewById(R.id.layLinearAddress);
        txtViewId = (TextView) findViewById(R.id.txtViewUserId);
        txtViewUserName = (TextView) findViewById(R.id.txtViewUsername);
        txtViewRealName = (TextView) findViewById(R.id.txtViewRealName);
        txtViewPhone = (TextView) findViewById(R.id.txtViewPhone);
        txtViewEmail = (TextView) findViewById(R.id.txtViewEmail);
        txtViewRegisterTime = (TextView) findViewById(R.id.txtViewRegisterTime);
        txtViewLastModifyTime = (TextView) findViewById(R.id.txtViewLastModifyTime);
        btnEdit = (Button) findViewById(R.id.btnEditUser);
        btnLogout = (Button) findViewById(R.id.btnLogout);
    }

    private void setTextView() {
        txtViewId.setText(PublicData.userId);
        txtViewUserName.setText(PublicData.username);
        txtViewRealName.setText(PublicData.realName);
        txtViewPhone.setText(PublicData.phone);
        txtViewEmail.setText(PublicData.email);
        txtViewRegisterTime.setText(PublicData.register_time);
        txtViewLastModifyTime.setText(PublicData.last_modify_time);
        layLinearAddress.removeAllViews();
        for (int i = 0; i < PublicData.UserAddresses.length(); i++) {
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView1 = new TextView(this);
            textView1.setText("\t" + (i + 1) + ") ");
            textView1.setTextSize(16);
            linearLayout.addView(textView1, textViewLayoutParams);
            TextView textView2 = new TextView(this);
            try {
                textView2.setText(PublicData.UserAddresses.getJSONObject(i).getString("long_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textView2.setTextSize(16);
            linearLayout.addView(textView2, textViewLayoutParams);
            LinearLayout.LayoutParams linearLayoutLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layLinearAddress.addView(linearLayout, linearLayoutLayoutParams);
        }
    }

    public void setAllView() {
        setTextView();
        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfile_Activity.this);
                alertDialog.setTitle("Enter Your Password");
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.edittext_in_alertbox, null);
                final EditText editText = (EditText) v.findViewById(R.id.edTxt);
                editText.setHint("Password");
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alertDialog.setView(v);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().equals(PublicData.password)) {
                            Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivityForResult(new Intent(getApplicationContext(), EditProfile_Activity.class), 1);
                    }
                });
                alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        btnLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PublicData.resetUserDate();
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.apply();
                PublicData.resetUserDate();
                DataBaseHelper helper = new DataBaseHelper(getApplicationContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                helper.deleteAllRecord(db);
                startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                finish();
            }
        });
    }

}