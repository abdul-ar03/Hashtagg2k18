package com.ar.developments.hashtagg2k18;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration_Portal extends Activity {
    public DatabaseReference myRef;
    public  Participants participants;
    private int screen_no=1;
    private Dialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        progress= new Dialog(Registration_Portal.this);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.custome_progress_bar);
        getWindow().setBackgroundDrawableResource(R.drawable.logo2);
        myRef = FirebaseDatabase.getInstance().getReference("Farm_products");

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
            if (!hasPermissions(this, PERMISSIONS)){
                final Dialog dialog = new Dialog(Registration_Portal.this);
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
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},112233);
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    final String[] PERMISSIONS_STORAGE = {android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 2);

                }
            }
        }

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference.child("participants").orderByChild("qr").equalTo("ar123");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    Log.d("dataSnapshot",dataSnapshot.toString());
//                    // dataSnapshot is the "issue" node with all children with id 0
//                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
//                        // do something with the individual "issues"
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




//        FirebaseDatabase ref= FirebaseDatabase.getInstance();
//        //Query queryRef = myRef.orderByChild("college").equalTo("gccgyc");
//        Query queryRef=myRef.child("participants").orderByChild("college").equalTo("college").on("value", function(snapshot) {
////            console.log(snapshot.val());
////            snapshot.forEach(function(data) {
////                console.log(data.key);
////            });
////        });
//        queryRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Participants p = dataSnapshot.getValue(Participants.class);
//
//                Log.d("query",p.getName()+" ");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot Snapshot:dataSnapshot.getChildren() ){
//                    Participants p = Snapshot.getValue(Participants.class);
//                    Log.d("TAG", "Value is: " + p.getName()+" "+p.getId()+" "+p.getCollege());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.d("TAG", "Failed to read value.", error.toException());
//            }
//        });
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
    @Override
    public void onBackPressed() {
        if(screen_no==1){
            Intent intent = getIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        else {
            screen_no=1;
            setContentView(R.layout.registration_layout);
        }

    }

    public void scan_fun(View view){
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        if(validate_fields()){
            if(!isNetworkConnected()) {
                progress.show();
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
                Intent intent = new Intent(getApplicationContext(), QR_code_reader.class);
                intent.putExtra("from_activity","register");
                startActivityForResult(intent, 1234);
            }

        }
    }

    public void list_icn_fun(View view){
        Intent intent = new Intent(getApplicationContext(), ListView_participants.class);
        startActivityForResult(intent ,1111);
    }

    public void wrkshp_click_fun(View view){
        CheckBox wkshp1=(CheckBox)findViewById(R.id.wrkshp1);
        CheckBox wkshp2=(CheckBox)findViewById(R.id.wrkshp2);
        CheckBox ppt=(CheckBox)findViewById(R.id.ppt);
        ppt.setSelected(false);
        ppt.setPressed(false);
        ppt.setButtonDrawable(R.drawable.unchecked);
        view.setSelected(!view.isSelected());
        if(view.getId()==R.id.wrkshp1){
            if(view.isSelected()){
                wkshp2.setPressed(false);
                wkshp2.setSelected(false);
                wkshp1.setButtonDrawable(R.drawable.checked);
                wkshp2.setButtonDrawable(R.drawable.unchecked);
                wkshp2.setSelected(false);
            }
            else{
                wkshp1.setButtonDrawable(R.drawable.unchecked);
            }
        }
        else if(view.getId()==R.id.wrkshp2){
            if(view.isSelected()){
                wkshp1.setPressed(false);
                wkshp1.setSelected(false);
                wkshp2.setButtonDrawable(R.drawable.checked);
                wkshp1.setButtonDrawable(R.drawable.unchecked);
                wkshp1.setSelected(false);
            }
            else{
                wkshp2.setButtonDrawable(R.drawable.unchecked);
            }
        }
    }

    public void ppt_click_fun(View view){
        CheckBox wkshp1=(CheckBox)findViewById(R.id.wrkshp1);
        CheckBox wkshp2=(CheckBox)findViewById(R.id.wrkshp2);
        view.setSelected(!view.isSelected());
        CheckBox ppt=(CheckBox)findViewById(R.id.ppt);
        if(view.isSelected()){
            ppt.setButtonDrawable(R.drawable.checked);
            wkshp1.setSelected(false);
            wkshp2.setSelected(false);
            wkshp1.setPressed(false);
            wkshp2.setPressed(false);
            wkshp1.setButtonDrawable(R.drawable.unchecked);
            wkshp2.setButtonDrawable(R.drawable.unchecked);
        }
        else {
            ppt.setButtonDrawable(R.drawable.unchecked);

        }
    }

    public void movie_fun(View view){
        screen_no=2;
        setContentView(R.layout.short_flim_layout);
    }

    public boolean validate_fields(){
        final EditText name_edt=(EditText)findViewById(R.id.name_edit);
        final EditText clg_edit=(EditText)findViewById(R.id.clg_edit);
        final EditText mob_edit=(EditText)findViewById(R.id.mob_edit);
        CheckBox wkshp1=(CheckBox)findViewById(R.id.wrkshp1);
        CheckBox wkshp2=(CheckBox)findViewById(R.id.wrkshp2);
        CheckBox ppt=(CheckBox)findViewById(R.id.ppt);

        final Boolean[] erros = {false};

        String name=name_edt.getText().toString();
        String clg=clg_edit.getText().toString();
        String mob=mob_edit.getText().toString();
        final Drawable error_icon = getResources().getDrawable(R.drawable.error);
        error_icon.setBounds(0, 0,40,40);

        if(TextUtils.isEmpty(name)){
            name_edt.setCompoundDrawables(null,null,error_icon,null);
            erros[0] =true;
        }else{
            name_edt.setCompoundDrawables(null,null,null,null);
        }
        name_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()!=0){
                    name_edt.setCompoundDrawables(null,null,null,null);
                }else{
                    name_edt.setCompoundDrawables(null,null,error_icon,null);
                }
            }
        });

        if(TextUtils.isEmpty(clg)){
            clg_edit.setCompoundDrawables(null,null,error_icon,null);
            erros[0] =true;
        }else{
            clg_edit.setCompoundDrawables(null,null,null,null);
        }
        clg_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()!=0){
                    clg_edit.setCompoundDrawables(null,null,null,null);
                }else{
                    clg_edit.setCompoundDrawables(null,null,error_icon,null);
                }
            }
        });

        if(mob.length()!=10){
            mob_edit.setCompoundDrawables(null,null,error_icon,null);
            erros[0] =true;
        }else{
            mob_edit.setCompoundDrawables(null,null,null,null);
        }
        mob_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==10){
                    mob_edit.setCompoundDrawables(null,null,null,null);
                }else{
                    mob_edit.setCompoundDrawables(null,null,error_icon,null);
                    erros[0]=true;
                }
            }
        });

        if(!erros[0]){
            participants=new Participants();
            participants.setName(name);
            participants.setCollege(clg);
            participants.setMobile(mob);

            int s=0;
            if(screen_no==2){
                participants.setEvent9(1);
            }
            else {
                if (wkshp1.isSelected()){
                    participants.setEvent11(1);
                    s=1;
                }
                else if(wkshp2.isSelected()){
                    participants.setEvent12(1);
                    s=1;
                }

                if (ppt.isSelected()){
                    participants.setEvent5(1);
                }

                if (s==0){
                    participants.setEvent1(1);
                    participants.setEvent2(1);
                    participants.setEvent3(1);
                    participants.setEvent4(1);
                    participants.setEvent6(1);
                    participants.setEvent7(1);
                    participants.setEvent8(1);
                    participants.setEvent10(1);
                }
            }


//            else{
//                participants.setEvent12(1);
//            }
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
        if(requestCode==1234){

            if(resultCode==RESULT_OK){
                LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
                LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
                LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
                TextView txt=(TextView)progress.findViewById(R.id.ok_txt);
                cancel.setVisibility(View.GONE);
                prog.setVisibility(View.GONE);
                ok.setVisibility(View.VISIBLE);
                progress.show();
                txt.setText("Participant Added");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 800);

                String qr=data.getStringExtra("qr");
                String id=myRef.push().getKey();
                participants.setId(id);
                participants.setQr(qr);
                myRef.child(id).setValue(participants);
                setContentView(R.layout.registration_layout);
            }
            else {
                LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
                LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
                LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
                TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
                cancel.setVisibility(View.VISIBLE);
                prog.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
                progress.show();
                txt.setText("Try other QR code");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 800);
            }

        }
    }

}
