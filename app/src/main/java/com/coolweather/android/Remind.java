package com.coolweather.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import static com.coolweather.android.R.layout.activity_remind;

public class Remind extends AppCompatActivity {


    private String[]name;
    private String[]Number;

    ArrayAdapter<String>adapter;
    private List<String> contactsList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_remind);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactsList);
        final ListView contactsView=(ListView)findViewById(R.id.contacts_view);
        contactsView.setAdapter(adapter);
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_CONTACTS},1);
        }else{
            readContacts();
        }
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent,View view,int position,long id){
                Uri send=Uri.parse("smsto:10010");
                Intent intent=new Intent(Intent.ACTION_SENDTO,send);
                intent.putExtra("sms_body","cxll");
                startActivity(intent);
            }
        });
    }
    private void readContacts(){
        int i=0;
        Cursor cursor=null;
        try{
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String displayName=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactsList.add(displayName+"\n"+number);
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requsetCode,String[] permissions,int[] granResults){
        switch (requsetCode){
            case 1:
                if(granResults.length>0&&granResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
