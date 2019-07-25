package com.ar.developments.hashtagg2k18;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;


import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QR_code_reader extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String txt;
    private String from;
    private String event;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        Intent intent = getIntent();
        from=intent.getStringExtra("from_activity");
        if(from.equals("co_ord")){
            event=intent.getStringExtra("event");
        }
        
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d("result", rawResult.getText());
        txt=rawResult.getText();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("participants");
        Query query = reference.child("participants").orderByChild("qr").equalTo(txt);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if(from.equals("register")){
                        Intent data = new Intent();
                        data.putExtra("qr",txt);
                        setResult(RESULT_CANCELED, data);
                        finish();
                    }
                    else if(from.equals("co_ord")){
                        Participants p = new Participants();
                        Log.d("dataSnapshot", dataSnapshot.toString());
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            p = Snapshot.getValue(Participants.class);
                        }
                        String id=p.getId();
                        int current_event=0;
                        switch (event){
                            case "event1":
                                current_event=p.getEvent1();
                                break;
                            case "event2":
                                current_event=p.getEvent2();
                                break;
                            case "event3":
                                current_event=p.getEvent3();
                                break;
                            case "event4":
                                current_event=p.getEvent4();
                                break;
                            case "event5":
                                current_event=p.getEvent5();
                                break;
                            case "event6":
                                current_event=p.getEvent6();
                                break;
                            case "event7":
                                current_event=p.getEvent7();
                                break;
                            case "event8":
                                current_event=p.getEvent8();
                                break;
                            case "event9":
                                current_event=p.getEvent9();
                                break;
                            case "event10":
                                current_event=p.getEvent10();
                                break;
                            case "event11":
                                current_event=p.getEvent11();
                                break;
                            case "event12":
                                current_event=p.getEvent12();
                                break;
                        }
                        if(current_event==1){
                            switch (event){
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
                            Intent data = new Intent();
                            data.putExtra("result","in");
                            setResult(RESULT_OK, data);
                            finish();
                        }

                        else if(current_event==0){
                            Intent data = new Intent();
                            data.putExtra("result","no");
                            setResult(RESULT_OK, data);
                            finish();
                        }

                        else if(current_event==2){
                            Intent data = new Intent();
                            data.putExtra("result","already");
                            setResult(RESULT_OK, data);
                            finish();
                        }

                    }

                }
                else
                {
                    if(from.equals("register")){
                        Intent data = new Intent();
                        data.putExtra("qr",txt);
                        setResult(RESULT_OK, data);
                        finish();

                    }
                    else {
                        Intent data = new Intent();
                        data.putExtra("result","nil");
                        setResult(RESULT_CANCELED, data);
                        finish();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


}