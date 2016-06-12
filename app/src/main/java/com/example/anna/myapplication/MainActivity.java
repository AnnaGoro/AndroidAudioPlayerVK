package com.example.anna.myapplication;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.anna.myapplication.models.Audio;
import com.example.anna.myapplication.rest.ApiService;
import com.example.anna.myapplication.rest.responses.AudioGetResponse;
import com.example.anna.myapplication.session.Session;
import com.example.anna.myapplication.session.SessionStore;

import java.io.IOException;
import java.util.Collection;

import java.util.LinkedList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    // List<Audio>  audios;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ApiService service;
    private Session session;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public interface OnAudioReadyListener{
        void onAudioReady(List<Audio> audios);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.anna.myapplication.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.example.anna.myapplication.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.example.anna.myapplication.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        // using linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        getAudiosFFF(new OnAudioReadyListener() {
            @Override
            public void onAudioReady(List<Audio> audios) {
                Log.d("aaaa", "" +audios.size());
                mAdapter = new RecyclerAdapter(audios);
                mRecyclerView.setAdapter(mAdapter);
            }
        }); // get list of Audio
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.anna.myapplication.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.example.anna.myapplication.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // get list of audios from vk using retrofit
    public List<Audio> getAudiosFFF(final OnAudioReadyListener listenern) {
        final List<Audio> songsSongs = new LinkedList<>();

        Call<AudioGetResponse> audiosCall =
                service.getAudios(session.getAccessToken());

        audiosCall.enqueue(new Callback<AudioGetResponse>() {

            @Override
            public void onResponse(Call<AudioGetResponse> call, Response<AudioGetResponse> response) {

                AudioGetResponse audioGetResponse = response.body();
                //songsSongs.addAll(audioGetResponse.getResponse());
                if(listenern != null){
                    listenern.onAudioReady(audioGetResponse.getResponse());
                }
                Log.d(TAG, "onResponse: ok");
            }

            @Override
            public void onFailure(Call<AudioGetResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: fail", t);
            }
        });
        return songsSongs;
    }

    // set up retrofit
    public void setUp() throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

        session = SessionStore.restore(getApplication());

    }
}
