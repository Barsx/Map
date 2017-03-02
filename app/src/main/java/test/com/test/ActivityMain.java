package test.com.test;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import test.com.test.ui.adapters.AdapterUsers;
import test.com.test.net.AsyncGetUsers;
import test.com.test.net.AsyncListener;
import test.com.test.model.CustomData;
import test.com.test.model.User;

public class ActivityMain extends AppCompatActivity {
    private  ArrayList<User> users;
    private  AdapterUsers adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }


    void initView(){
        final RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        users=new ArrayList<User>();
        list.setLayoutManager(mLayoutManager);
        AdapterUsers.ViewHolderClickListener listener=new AdapterUsers.ViewHolderClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                Intent intent = new Intent(ActivityMain.this, ActivityMap.class); //starting ActivityMAp for selected user
                intent.putExtra("user", users.get(position));
                startActivity(intent);
                finish();

            }

            @Override
            public void onItemLongClick(int position)
            {

            }


        };


        adapter=new AdapterUsers(users,listener,getApplicationContext());
        list.setAdapter(adapter);
        updateData();
    }

    public void alert(String s,String des){
        LayoutInflater mLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mLayoutInflater.inflate(R.layout.alert_error, null);
        final boolean[] isShown={false};

        final AlertDialog ad = new AlertDialog.Builder(this)
                .setView(v)
                .setCancelable(true).show();

        TextView text=(TextView)v.findViewById(R.id.text);
        text.setText(s);
        final TextView description=(TextView)v.findViewById(R.id.description);
        description.setText(des);
        final ImageButton show=(ImageButton)v.findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //show or hide error stack info
                isShown[0]=!isShown[0];
                if (isShown[0]){
                    description.setVisibility(View.VISIBLE);
                    show.setBackgroundResource(R.drawable.ic_down);
                }else{
                    description.setVisibility(View.GONE);
                    show.setBackgroundResource(R.drawable.ic_up);
                }

            }
        });

        Button refresh=(Button)v.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //refreshing data after clicking "refresh"
                ad.dismiss();
                updateData();
            }
        });
        Button ok=(Button)v.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });

    }


    private void updateData(){
        AsyncListener asyncListener= new AsyncListener(){

            @Override
            public void onTaskCompleted(CustomData o) {
                closeDiallog();
                if (o.error==null) {
                    users.clear();
                    users.addAll((ArrayList<User>) o.data); //updating data in list

                    adapter.notifyDataSetChanged();



                }else{
                      alert("Error:"+o.error.getShortDescription(),o.error.getDescription()); //alert if we have any CustomError
                }

            }

            @Override
            public void onTaskPrepared() {
                openDiallog();

            }};

        new AsyncGetUsers(asyncListener).execute();
    }

    public void closeDiallog(){
        ProgressBar progressBar=(ProgressBar)findViewById(R.id.progress);
        if (progressBar!=null){
            try {
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
            }
        }
    }
    public void openDiallog(){
        ProgressBar progressBar=(ProgressBar)findViewById(R.id.progress);
        if (progressBar!=null){
            try {
                progressBar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
        }

    }
}
