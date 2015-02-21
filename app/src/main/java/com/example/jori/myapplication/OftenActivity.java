package com.example.jori.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OftenActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private NotesDbAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_often);

        final ArrayList<Bookmark> bookList = new ArrayList<Bookmark>();

        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();

        Cursor result = dbAdapter.fetchAllNotes();
        result.moveToFirst();
        while (!result.isAfterLast()) {

            String companyName = result.getString(1);
            String companyAddr = result.getString(2);
            bookList.add(new Bookmark(companyName, companyAddr));

            result.moveToNext();
        }
        result.close();

        ListView listview = (ListView)findViewById(R.id.listView);

        listview.setAdapter(new ArrayAdapter<Bookmark>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                bookList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                Bookmark book = bookList.get(position);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(book.comName);
                text2.setText(book.comAddr);
                return view;
            }

        });

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_often);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_often,
                (DrawerLayout) findViewById(R.id.drawer_layout_often));

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position==0) {
            Intent i = new Intent(this,CheckActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }
        else if(position ==1){
            Intent i = new Intent(this,CorrectionActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }
        else if(position ==2){
            Intent i = new Intent(this,ExamActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);
        }
        else if(position ==3){
            Intent i = new Intent(this,MaterialActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }else{
            Intent i = new Intent(this,OftenActivity.class);
            Bundle b = new Bundle();
            i.putExtras(b);
            startActivity(i);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_often, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_button) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
