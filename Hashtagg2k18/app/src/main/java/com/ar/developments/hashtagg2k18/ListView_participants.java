package com.ar.developments.hashtagg2k18;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListView_participants extends Activity {
    private ArrayList<Participants> P_array = new ArrayList<Participants>();
    private int count=0;
    private ValueEventListener listener;
    private Dialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_list);
        progress= new Dialog(ListView_participants.this);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.custome_progress_bar);
        LinearLayout cancel=(LinearLayout)progress.findViewById(R.id.cancel_layout);
        LinearLayout prog=(LinearLayout)progress.findViewById(R.id.progress_layout);
        TextView txt=(TextView)progress.findViewById(R.id.cancel_txt);
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
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("participants");
            final Create_list_Adapter adapter = new Create_list_Adapter(ListView_participants.this, P_array,1);
            final ListView listView = (ListView) findViewById(R.id.participant_listView);
//            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                    Log.d("res", "res");
//                    final Dialog dialog = new Dialog(ListView_participants.this);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.custome_backpress_dilalog);
//                    final Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_yes);
//                    final Button dialog_no = (Button) dialog.findViewById(R.id.dialog_no);
//                    dialog_yes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Participants par = new Participants();
//                            par = P_array.get(position);
//                            String id = par.getId();
//                            myRef.child(id).removeValue();
//                            P_array.remove(position);
//                            adapter.notifyDataSetChanged();
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog_no.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//                    return false;
//                }
//            });

            if (count == 0) {
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            Participants p = Snapshot.getValue(Participants.class);
                            P_array.add(p);
                            adapter.notifyDataSetChanged();
                            progress.dismiss();
                            Log.d("TAG", "Value is: " + p.getName() + " " + p.getId() + " " + p.getCollege());
                        }
                        myRef.removeEventListener(listener);
                        if (!dataSnapshot.exists()) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                myRef.addValueEventListener(listener);
                count++;
            }


            listView.setAdapter(adapter);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
