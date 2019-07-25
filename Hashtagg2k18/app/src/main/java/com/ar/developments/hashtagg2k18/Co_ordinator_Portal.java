package com.ar.developments.hashtagg2k18;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Admin on 7/21/2018.
 */

public class Co_ordinator_Portal extends Activity {
    private ArrayList<Participants> P_array = new ArrayList<Participants>();
    private ArrayList<Participants> W_array = new ArrayList<Participants>();
    private ValueEventListener listener1;
    private ValueEventListener listener2;
    private TextView title_bar;
    private String title="";
    private int count1=0;
    private int count2=0;
    int event_no=0;
    int drawer_count=0;
    int type=1;
    String event;
    Dialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.co_ordinator_layout);
        progress= new Dialog(Co_ordinator_Portal.this);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.custome_progress_bar);
        title_bar=(TextView)findViewById(R.id.co_ord_title);
        Intent intent=getIntent();
        event=intent.getStringExtra("event_no");
        set_actionbar_title(event);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if(event.equals("event11") || event.equals("event12")){
            LinearLayout winner_draw=(LinearLayout)findViewById(R.id.winner_draw);
            winner_draw.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
            if (!hasPermissions(this, PERMISSIONS)){
                final Dialog dialog = new Dialog(Co_ordinator_Portal.this);
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
    public void menu_scan_fun(View view){
        count1=0;
        count2=0;
        set_actionbar_title(event);
        LinearLayout scan_layout=(LinearLayout)findViewById(R.id.scan_layout);
        LinearLayout partc_list_layout=(LinearLayout)findViewById(R.id.partc_list_layout);
        LinearLayout winner_list_layout=(LinearLayout)findViewById(R.id.winner_list_layout);
        scan_layout.setVisibility(View.VISIBLE);
        partc_list_layout.setVisibility(View.GONE);
        winner_list_layout.setVisibility(View.GONE);
        ham_fun(view);
    }

    public void menu_partc_fun(View view){
        count2=0;
        title_bar.setText("Participants");
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        ok.setVisibility(View.GONE);
        LinearLayout scan_layout=(LinearLayout)findViewById(R.id.scan_layout);
        LinearLayout partc_list_layout=(LinearLayout)findViewById(R.id.partc_list_layout);
        LinearLayout winner_list_layout=(LinearLayout)findViewById(R.id.winner_list_layout);
        scan_layout.setVisibility(View.GONE);
        partc_list_layout.setVisibility(View.VISIBLE);
        winner_list_layout.setVisibility(View.GONE);
        ham_fun(view);

        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        if (!isNetworkConnected()){
            prog.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            txt.setText("Network Error");
            progress.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                }
            }, 800);
        }
        else {
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("participants");
            final Create_list_Adapter adapter = new Create_list_Adapter(Co_ordinator_Portal.this, P_array,type);
            final ListView listView = (ListView) findViewById(R.id.co_p_list);
            if(event.equals("event11") || event.equals("event12")) {
            }
            else{
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        final Dialog dialog = new Dialog(Co_ordinator_Portal.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.custome_backpress_dilalog);
                        final Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_yes);
                        final Button dialog_no = (Button) dialog.findViewById(R.id.dialog_no);
                        final TextView dialog_txt = (TextView) dialog.findViewById(R.id.dialog_txt);
                        dialog_txt.setText("Move the Participant to Winners List");
                        dialog_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Participants p = new Participants();
                                p = P_array.get(position);
                                String id = p.getId();
                                switch (event) {
                                    case "event1":
                                        p.setEvent1(3);
                                        break;
                                    case "event2":
                                        p.setEvent2(3);
                                        break;
                                    case "event3":
                                        p.setEvent3(3);
                                        break;
                                    case "event4":
                                        p.setEvent4(3);
                                        break;
                                    case "event5":
                                        p.setEvent5(3);
                                        break;
                                    case "event6":
                                        p.setEvent6(3);
                                        break;
                                    case "event7":
                                        p.setEvent7(3);
                                        break;
                                    case "event8":
                                        p.setEvent8(3);
                                        break;
                                    case "event9":
                                        p.setEvent9(3);
                                        break;
                                    case "event10":
                                        p.setEvent10(3);
                                        break;
                                    case "event11":
                                        p.setEvent11(3);
                                        break;
                                    case "event12":
                                        p.setEvent12(3);
                                        break;
                                }
                                myRef.child(id).setValue(p);
                                P_array.remove(position);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return false;
                    }
                });
            }
            if (count1 == 0) {
                progress.show();
                P_array.clear();
                listener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            Participants p = Snapshot.getValue(Participants.class);
                            int current_event = 0;
                            switch (event) {
                                case "event1":
                                    current_event = p.getEvent1();
                                    break;
                                case "event2":
                                    current_event = p.getEvent2();
                                    break;
                                case "event3":
                                    current_event = p.getEvent3();
                                    break;
                                case "event4":
                                    current_event = p.getEvent4();
                                    break;
                                case "event5":
                                    current_event = p.getEvent5();
                                    break;
                                case "event6":
                                    current_event = p.getEvent6();
                                    break;
                                case "event7":
                                    current_event = p.getEvent7();
                                    break;
                                case "event8":
                                    current_event = p.getEvent8();
                                    break;
                                case "event9":
                                    current_event = p.getEvent9();
                                    break;
                                case "event10":
                                    current_event = p.getEvent10();
                                    break;
                                case "event11":
                                    current_event = p.getEvent11();
                                    break;
                                case "event12":
                                    current_event = p.getEvent12();
                                    break;
                            }
                            if (current_event == 2) {
                                P_array.add(p);

                            }
                        }
                        progress.dismiss();
                        adapter.notifyDataSetChanged();
                        myRef.removeEventListener(listener1);
                        if (!dataSnapshot.exists()) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                myRef.addValueEventListener(listener1);
                count1++;

            }
            listView.setAdapter(adapter);
        }
    }

    public void menu_winner_fun(View view){
        count1=0;
        title_bar.setText("Winners");
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
        cancel.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        LinearLayout scan_layout=(LinearLayout)findViewById(R.id.scan_layout);
        LinearLayout partc_list_layout=(LinearLayout)findViewById(R.id.partc_list_layout);
        LinearLayout winner_list_layout=(LinearLayout)findViewById(R.id.winner_list_layout);
        scan_layout.setVisibility(View.GONE);
        partc_list_layout.setVisibility(View.GONE);
        winner_list_layout.setVisibility(View.VISIBLE);
        ham_fun(view);

        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        if (!isNetworkConnected()){
            prog.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            txt.setText("Network Error");
            progress.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                }
            }, 800);
        }
        else {
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("participants");
            final Create_list_Adapter adapter = new Create_list_Adapter(Co_ordinator_Portal.this, W_array,type);
            final ListView listView = (ListView) findViewById(R.id.co_w_list);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Dialog dialog = new Dialog(Co_ordinator_Portal.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custome_backpress_dilalog);
                    final Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_yes);
                    final Button dialog_no = (Button) dialog.findViewById(R.id.dialog_no);
                    final TextView dialog_txt = (TextView) dialog.findViewById(R.id.dialog_txt);
                    dialog_txt.setText("Remove from Winners List");
                    dialog_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Participants p = new Participants();
                            p = W_array.get(position);
                            String id = p.getId();
                            switch (event) {
                                case "event1":
                                    p.setEvent1(2);
                                    break;
                                case "event2":
                                    p.setEvent2(2);
                                    break;
                                case "event3":
                                    p.setEvent3(2);
                                    break;
                                case "event4":
                                    p.setEvent4(2);
                                    break;
                                case "event5":
                                    p.setEvent5(2);
                                    break;
                                case "event6":
                                    p.setEvent6(2);
                                    break;
                                case "event7":
                                    p.setEvent7(2);
                                    break;
                                case "event8":
                                    p.setEvent8(2);
                                    break;
                                case "event9":
                                    p.setEvent9(2);
                                    break;
                                case "event10":
                                    p.setEvent10(2);
                                    break;
                                case "event11":
                                    p.setEvent11(2);
                                    break;
                                case "event12":
                                    p.setEvent12(2);
                                    break;
                            }
                            myRef.child(id).setValue(p);
                            W_array.remove(position);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    dialog_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });

            if (count2 == 0) {
                progress.show();
                W_array.clear();
                listener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            Participants p = Snapshot.getValue(Participants.class);
                            int current_event = 0;
                            switch (event) {
                                case "event1":
                                    current_event = p.getEvent1();
                                    break;
                                case "event2":
                                    current_event = p.getEvent2();
                                    break;
                                case "event3":
                                    current_event = p.getEvent3();
                                    break;
                                case "event4":
                                    current_event = p.getEvent4();
                                    break;
                                case "event5":
                                    current_event = p.getEvent5();
                                    break;
                                case "event6":
                                    current_event = p.getEvent6();
                                    break;
                                case "event7":
                                    current_event = p.getEvent7();
                                    break;
                                case "event8":
                                    current_event = p.getEvent8();
                                    break;
                                case "event9":
                                    current_event = p.getEvent9();
                                    break;
                                case "event10":
                                    current_event = p.getEvent10();
                                    break;
                                case "event11":
                                    current_event = p.getEvent11();
                                    break;
                                case "event12":
                                    current_event = p.getEvent12();
                                    break;
                            }
                            if (current_event == 3) {
                                W_array.add(p);
                                Log.d("warry", p.getName() + " ");
                            }
                        }
                        progress.dismiss();
                        adapter.notifyDataSetChanged();
                        myRef.removeEventListener(listener2);
                        if (!dataSnapshot.exists()) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                myRef.addValueEventListener(listener2);
                count2++;
            }

            listView.setAdapter(adapter);
        }
    }

    public void ham_fun(View view) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView imageView=(ImageView)findViewById(R.id.ham_icon);
        if(drawer_count==1){
            drawer_count=0;
            imageView.setImageResource(R.drawable.ham);
            Log.d("in","in");
            mDrawerLayout.setVisibility(View.GONE);
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        else{
            drawer_count=1;
            Log.d("in","out");
            imageView.setImageResource(R.drawable.ic_cross);
            mDrawerLayout.setVisibility(View.VISIBLE);
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    public void event_fun(View view){
        switch (view.getId()){
            case R.id.event1:
                event_no=1;
                break;
            case R.id.event2:
                event_no=2;
                break;
            case R.id.event3:
                event_no=3;
                break;
            case R.id.event4:
                event_no=4;
                break;
            case R.id.event5:
                event_no=5;
                break;
            case R.id.event6:
                event_no=6;
                break;
            case R.id.event7:
                event_no=7;
                break;
            case R.id.event8:
                event_no=8;
                break;
            case R.id.event9:
                event_no=9;
                break;
            case R.id.event10:
                event_no=10;
                break;
        }

        Log.d("event no","  "+event_no);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 112233: {
                Log.i("Camera", "G : " + grantResults[0]);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {
                    } else {

                    }
                }
                return;
            }
        }
    }

    public void scan_fun(View view){
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
        ok.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        prog.setVisibility(View.VISIBLE);
        if (!isNetworkConnected()){
            prog.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            txt.setText("Network Error");
            progress.show();
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
            intent.putExtra("from_activity","co_ord");
            intent.putExtra("event",event);
            startActivityForResult(intent, 1234);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout ok=(LinearLayout)progress.findViewById(R.id.ok_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        TextView cancel_txt=(TextView)progress.findViewById(R.id.cancel_txt);
        TextView ok_txt=(TextView)progress.findViewById(R.id.ok_txt);
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                String result=data.getStringExtra("result");
                if(result.equals("in")){
                    prog.setVisibility(View.GONE);
                    ok.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    ok_txt.setText("Check In");
                }
                else if(result.equals("already")){
                    prog.setVisibility(View.GONE);
                    ok.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                    cancel_txt.setText("Already Participated");
                }
                else {
                    prog.setVisibility(View.GONE);
                    ok.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                    cancel_txt.setText("Cannot Participate");
                }
                progress.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 2000);
            }
            else{
                prog.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                cancel_txt.setText("Invalid QR Code");
                progress.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void set_actionbar_title(String event_no) {
        switch (event_no){
            case "event1":
                title="BlackIce - Code Hunt";
                break;
            case "event2":
                title="Calicwiz - Tech Quiz";
                break;
            case "event3":
                title="Hydelineate - Web Designing";
                break;
            case "event4":
                title="Defrost - Debugging";
                break;
            case "event5":
                title="Incendio - Paper Presentation";
                break;
            case "event6":
                title="Floe - Connections";
                break;
            case "event7":
                title=" Fiery Hunt - Treasure Hunt";
                break;
            case "event8":
                title=" Spark_It_Up - Adzap";
                break;
            case "event9":
                title="Scintillare - Short Film";
                type=2;
                break;
            case "event10":
                title="Blistrolic - Gaming";
                break;
            case "event11":
                title="Robotics";
                break;
            case "event12":
                title="R-Language";
                break;
        }
        title_bar.setText(title);
    }
}

