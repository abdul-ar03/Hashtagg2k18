package com.ar.developments.hashtagg2k18;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    private SharedPreferences.Editor editor;
    private int screen_no=0;
    private String event_no="";
    private String event_name="";
    private Dialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        progress= new Dialog(MainActivity.this);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.custome_progress_bar);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean check=pref.getBoolean("key_commit", false);
                if (check){
                    String post=pref.getString("user_post", "null");
                    if (post.equals("registration")){
                        Intent intent = new Intent(getApplicationContext(), Registration_Portal.class);
                        startActivityForResult(intent ,1111);
                    }
                    else if (post.equals("co_ordinator")){
                        String event_no=pref.getString("event_no", "null");
                        Intent intent = new Intent(getApplicationContext(), Co_ordinator_Portal.class);
                        intent.putExtra("event_no",event_no);
                        startActivityForResult(intent ,2222);
                    }
                    else{
                        setContentView(R.layout.home);
                    }
                }
                else{
                    setContentView(R.layout.home);
                }

            }
        }, 1000);



        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
            if (!hasPermissions(this, PERMISSIONS)){
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.permission_dialog);
                final Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_yes);
                dialog_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},112233);
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    final String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 2);

                }
            }
        }

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void admin_click_fun(View view){
        setContentView(R.layout.login_page);
        TextView text_title=(TextView)findViewById(R.id.text_title);
        text_title.setText("Registration Portal");
        screen_no=1;
        final EditText uid_edt=(EditText)findViewById(R.id.uid_txt);
        uid_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    uid_edt.setText(s);
                }
                uid_edt.setSelection(uid_edt.getText().length());
//                if(s.toString().length()!=0){
//                    //uid_edt.setCompoundDrawables(null,null,null,null);
//                }else{
//                    //uid_edt.setCompoundDrawables(null,null,error_icon,null);
//                }
            }
        });

    }

    public void co_ordinator_click_fun(View view){
        screen_no=2;
        setContentView(R.layout.events_layout);
    }

    public void login_fun(View view){
        final EditText uid_edt=(EditText)findViewById(R.id.uid_txt);
        final EditText pass_edit=(EditText)findViewById(R.id.pass_txt);
        final String pass2=(pass_edit.getText()).toString();
        final String u_id2=(uid_edt.getText()).toString();
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        if(login_error()){
            progress.show();
            if(!isNetworkConnected()){
                prog.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                txt.setText("Network Error");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 800);
            }
            else {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference.child("authorise").orderByChild("u_id").equalTo(u_id2);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Authorise a = new Authorise();
                            for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                                a = Snapshot.getValue(Authorise.class);

                            }
                            String password = a.getPassword();
                            if (password.equals(pass2)) {
                                progress.dismiss();
                                editor.putBoolean("key_commit", true);
                                if (screen_no == 1) {
                                    editor.putString("u_id", u_id2);
                                    editor.putString("user_post","registration");
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), Registration_Portal.class);
                                    startActivityForResult(intent, 1111);
                                } else {
                                    editor.putString("u_id", u_id2);
                                    editor.putString("event_no", event_no);
                                    editor.putString("user_post","co_ordinator");
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), Co_ordinator_Portal.class);
                                    intent.putExtra("event_no", event_no);
                                    startActivityForResult(intent, 2222);
                                }
                            } else {
                                LinearLayout cancel = (LinearLayout) progress.findViewById(R.id.cancel_layout);
                                LinearLayout prog = (LinearLayout) progress.findViewById(R.id.progress_layout);
                                TextView txt = (TextView) progress.findViewById(R.id.cancel_txt);
                                txt.setText("Invalid Password ");
                                prog.setVisibility(View.GONE);
                                cancel.setVisibility(View.VISIBLE);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                    }
                                }, 800);

                            }
                        } else {
                            LinearLayout cancel = (LinearLayout) progress.findViewById(R.id.cancel_layout);
                            LinearLayout prog = (LinearLayout) progress.findViewById(R.id.progress_layout);
                            TextView txt = (TextView) progress.findViewById(R.id.cancel_txt);
                            txt.setText("Invalid User id ");
                            prog.setVisibility(View.GONE);
                            cancel.setVisibility(View.VISIBLE);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                }
                            }, 800);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        LinearLayout cancel = (LinearLayout) progress.findViewById(R.id.cancel_layout);
                        LinearLayout prog = (LinearLayout) progress.findViewById(R.id.progress_layout);
                        prog.setVisibility(View.GONE);
                        cancel.setVisibility(View.VISIBLE);
                        TextView txt = (TextView) progress.findViewById(R.id.cancel_txt);
                        txt.setText("Error Occured ");

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        }, 800);
                    }

                });
            }

        }
    }

    public void event_fun(View view){
        screen_no=21;
        setContentView(R.layout.login_page);
        EditText u_id=(EditText)findViewById(R.id.uid_txt);
        TextView text_title=(TextView)findViewById(R.id.text_title);
        u_id.setInputType(InputType.TYPE_NULL);
        u_id.setFocusable(false);
        u_id.setClickable(false);
        String e_name="";
        switch (view.getId()){
            case R.id.event1:
                event_no="event1";
                event_name="BlackIce";
                e_name="BlackIce - Code Hunt";
                break;
            case R.id.event2:
                event_no="event2";
                event_name="Calicwiz";
                e_name="Calicwiz - Tech Quiz";
                break;
            case R.id.event3:
                event_no="event3";
                event_name="Hydelineate";
                e_name="Hydelineate - Web Designing";
                break;
            case R.id.event4:
                event_no="event4";
                event_name="Defrost";
                e_name="Defrost - Debugging";
                break;
            case R.id.event5:
                event_no="event5";
                event_name="Incendio";
                e_name="Incendio - Paper Presentation";
                break;
            case R.id.event6:
                event_no="event6";
                event_name="Floe";
                e_name="Floe - Connections";
                break;
            case R.id.event7:
                event_no="event7";
                event_name="Fiery Hunt";
                e_name="Fiery Hunt - Treasure Hunt";
                break;
            case R.id.event8:
                event_no="event8";
                event_name="Spark_It_Up";
                e_name="Spark_It_Up - Adzap";
                break;
            case R.id.event9:
                event_no="event9";
                event_name="Scintillare";
                e_name="Scintillare - Short Film";
                break;
            case R.id.event10:
                event_no="event10";
                event_name="Blistrolic";
                e_name="Blistrolic - Gaming";
                break;
            case R.id.event11:
                event_no="event11";
                event_name="Robotics";
                e_name="Robotics";
                break;
            case R.id.event12:
                event_no="event12";
                event_name="R-Language";
                e_name="R-Language";
                break;
        }
        u_id.setText(event_name);
        text_title.setText(e_name);
    }

    public boolean login_error(){
        final EditText uid_edt=(EditText)findViewById(R.id.uid_txt);
        final EditText pass_edit=(EditText)findViewById(R.id.pass_txt);
        Boolean erros=false;
        String uid_txt=uid_edt.getText().toString();
        String pass=pass_edit.getText().toString();
        final Drawable error_icon = getResources().getDrawable(R.drawable.error);
        error_icon.setBounds(0, 0,40,40);

        if(TextUtils.isEmpty(uid_txt)){
            uid_edt.setCompoundDrawables(null,null,error_icon,null);
            erros=true;
        }else{
            uid_edt.setCompoundDrawables(null,null,null,null);
        }
        uid_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()!=0){
                    uid_edt.setCompoundDrawables(null,null,null,null);
                }else{
                    uid_edt.setCompoundDrawables(null,null,error_icon,null);
                }
            }
        });

        if(TextUtils.isEmpty(pass)){
            pass_edit.setCompoundDrawables(null,null,error_icon,null);
            erros=true;
        }else{
            pass_edit.setCompoundDrawables(null,null,null,null);
        }
        pass_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()!=0){
                    pass_edit.setCompoundDrawables(null,null,null,null);
                }else{
                    pass_edit.setCompoundDrawables(null,null,error_icon,null);
                }
            }
        });

        if (!erros){
            return true;
        }
        return false;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (resultCode == RESULT_CANCELED) {
               finish();
            }
        }
        else if (requestCode == 2222) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (screen_no){
            case 0:
                finish();
                break;
            case 1:
                setContentView(R.layout.home);
                screen_no=0;
                break;
            case 2:
                setContentView(R.layout.home);
                screen_no=0;
                break;
            case 21:
                setContentView(R.layout.events_layout);
                screen_no=2;
                break;

        }
    }
}
