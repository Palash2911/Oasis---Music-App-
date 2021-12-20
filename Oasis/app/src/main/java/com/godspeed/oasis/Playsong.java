package com.godspeed.oasis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Playsong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
        updseek.interrupt();
    }

    TextView tv;
    ImageView play,next,prev;
    ArrayList<File> songs;
    MediaPlayer mp;
    String textcontent;
    int pos;
    Thread updseek;
    SeekBar seek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        seek=findViewById(R.id.seekBar);
        tv=findViewById(R.id.textView);
        play=findViewById(R.id.play);
        prev=findViewById(R.id.previous);
        next=findViewById(R.id.next);

        Intent intent=getIntent();
        Bundle bund=intent.getExtras();
        songs=(ArrayList) bund.getParcelableArrayList("Songlist");
        textcontent=intent.getStringExtra("CurrentSong");
        tv.setText(textcontent);
        tv.setSelected(true);
        pos=intent.getIntExtra("Position", 0);
        Uri uri = Uri.parse(songs.get(pos).toString());
        mp=MediaPlayer.create(this, uri);
        mp.start();
        seek.setMax(mp.getDuration());

        //SeekBar
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        updseek=new Thread(){
            @Override
            public void run() {
                int curr=0;
                try {
                    while(curr<mp.getDuration()){
                        curr=mp.getCurrentPosition();
                        seek.setProgress(curr);
                        sleep(500);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updseek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp.isPlaying()){
                    mp.pause();
                    play.setImageResource(R.drawable.play);
                }
                else{
                    mp.start();
                    play.setImageResource(R.drawable.pause);
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                mp.release();
                if(pos!=0){
                    pos=pos-1;
                }
                else{
                    pos=songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(pos).toString());
                mp=MediaPlayer.create(getApplicationContext(), uri);
                mp.start();
                play.setImageResource(R.drawable.pause);
                seek.setMax(mp.getDuration());
                textcontent=songs.get(pos).getName().toString();
                tv.setText(textcontent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                mp.release();
                if(pos!=songs.size()-1){
                    pos=pos+1;
                }
                else{
                    pos=0;
                }
                Uri uri = Uri.parse(songs.get(pos).toString());
                mp=MediaPlayer.create(getApplicationContext(), uri);
                mp.start();
                play.setImageResource(R.drawable.pause);
                seek.setMax(mp.getDuration());
                textcontent=songs.get(pos).getName().toString();
                tv.setText(textcontent);
            }
        });
    }
}