package com.godspeed.oasis;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.listview);
        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> mysongs=importsongs(Environment.getExternalStorageDirectory());
                        String [] items=new String[mysongs.size()];
                        for(int i=0;i<mysongs.size();i++){
                            items[i]=mysongs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adp=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        lv.setAdapter(adp);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent=new Intent(MainActivity.this, Playsong.class);
                                String currsong=lv.getItemAtPosition(i).toString();
                                intent.putExtra("Songlist", mysongs);
                                intent.putExtra("CurrentSong", currsong);
                                intent.putExtra("Position", i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> importsongs(File file){
        ArrayList al=new ArrayList();
        File [] songs = file.listFiles();
        if(songs != null){
            for(File myfiles: songs){
                if(!myfiles.isHidden() && myfiles.isDirectory()){
                    al.addAll(importsongs(myfiles));
                }
                else{
                    if(myfiles.getName().endsWith(".mp3") && !myfiles.getName().startsWith(".")){
                        al.add(myfiles);
                    }
                }
            }
        }
        return al;
    }
}