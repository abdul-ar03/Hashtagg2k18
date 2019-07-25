package com.ar.developments.hashtagg2k18;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class Create_list_Adapter extends ArrayAdapter {
    private Context context;
    private int type;
    private ArrayList<Participants> NT_array = new ArrayList<Participants>();

    public Create_list_Adapter(Context context, ArrayList<Participants> values,int type) {
        super(context, 0, values);
        this.context = context;
        this.NT_array = values;
        this.type=type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = convertView;
        if (v == null) {

        } else {
            v = convertView;
        }
        v = Inflater.inflate(R.layout.participent_list_item, null);
        TextView name_space = (TextView) v.findViewById(R.id.name_space);
        TextView clg_space = (TextView) v.findViewById(R.id.college_space);
        TextView mob_space = (TextView) v.findViewById(R.id.mobile_space);
        TextView list_item_no = (TextView) v.findViewById(R.id.list_item_no);

        if(type==2){
            TextView i_name = (TextView) v.findViewById(R.id.i_name);
            TextView i_clg = (TextView) v.findViewById(R.id.i_clg);
            i_name.setText("Team");
            i_clg.setText("Members");
        }
        Participants p = new Participants();
        p = NT_array.get(position);
        list_item_no.setText("" + (position + 1));
        name_space.setText(p.getName());
        clg_space.setText(p.getCollege());
        mob_space.setText(p.getMobile());
        final Participants finalP = p;
        mob_space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + finalP.getMobile()));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });
        return v;
    }


}

