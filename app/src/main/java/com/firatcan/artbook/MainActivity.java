package com.firatcan.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firatcan.artbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

     private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
    }
    //menuyu buraya baglamak icin 2 metod override edilmeli.


    public boolean onCreateOptionsMenu(Menu menu){
        //içersinde baglama işlemi yapılcak
        //menu yu(layout) koda bağlıyıcaz

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_art,menu);

        return super.onCreateOptionsMenu(menu);

    }

    //menuye basılırsa ne olacak
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.add_art){
            Intent intent=new Intent(this,ArtActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}