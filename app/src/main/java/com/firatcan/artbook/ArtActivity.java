package com.firatcan.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.firatcan.artbook.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;//Galeriye gitmek için
    ActivityResultLauncher<String> permissionLauncher;  // izin istemek için

    Bitmap selectedimage;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_art);
        binding= ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        
        registerLauncher();
    }

    public void save(View view){
            String name=binding.arttext.getText().toString();
            String artisname=binding.artisttext.getText().toString();
            String year=binding.yeartext.getText().toString();

            Bitmap smallimage=makeSmallerImage(selectedimage,300);

            //Burada sqllite veritabanına kaydetmek için 0 ve 1 lere çevirme işlemi yapıcam.
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        smallimage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] byteArray= outputStream.toByteArray(); //byte dizisine çevirdim.byteArray'de tutuyorum.



        try {
            database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");

            String sqlString="INSERT INTO arts(artname,paintername,year,image) VALUES (?,?,?,?)";

            //SqliteStatmemnt  sonradan bağlam işlemlerinde(binding) kolaylık sağlar.database.compileStatement demek sqlString'i alıp database de çalıştırır fakat
            // yukardaki database.execSQL de aynısını yapar bunun darkı sqLiteStatement. dediğim zaman farklı değerleri bağlayabileceğim bir metod çıkıyor karşıma.
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artisname);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        //finish();
        Intent intent=new Intent(ArtActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //önceden birikmiş olana bütün aktiviteleri kapat demek.
        startActivity(intent);

    }

    public Bitmap makeSmallerImage(Bitmap image,int maximumSize){

        int width=image.getWidth();
        int height=image.getHeight();

        float bitmapRatio=(float) width /(float) height;

        if(bitmapRatio>1){
            width=maximumSize;
            height=(int)(width/bitmapRatio);
        }else {
            height=maximumSize;
            width=(int) (height*bitmapRatio);
        }
        return image.createScaledBitmap(image,width,height,true);
    }
    public void selectimage(View view){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            //ANDROID 33+ -> READ_MEDIA_IMAGES


            //chechkselfpremission ın cevabı ya kontrol edildi ya da edilmedi oluyor.
            //read_external_storage izni varmı diye bakıyor != ın sol tarafı, bunun cevabı packagemanager içinde verilir.PREMISSION_DENIED izin verilmemiş demek.
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    //Burda snackbar yapcaz izini zorunlu olarak istememiz gerektiğini söyliyicez.
                    //INDEFINITE belirsiz süre göster kullanıcı başka tuşa basana kadar gösterir.
                    //setAction ile gösterilcek buton yapılır
                    Snackbar.make(view,"We need permission to show your gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin istiyicez.
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else {

                    //direkt izin istiyicez
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }

                //YUKARIDAN (izin verilmemiş) yerden registerLauncher() fonksyonuna geçiş yapıcaz

            }else{
                //izin verilmiş galeriye gidicez.
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }else{
                //ANDROID 32 - -> READ_EXTERNAL_STORAGE


            //chechkselfpremission ın cevabı ya kontrol edildi ya da edilmedi oluyor.
            //read_external_storage izni varmı diye bakıyor != ın sol tarafı, bunun cevabı packagemanager içinde verilir.PREMISSION_DENIED izin verilmemiş demek.
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //Burda snackbar yapcaz izini zorunlu olarak istememiz gerektiğini söyliyicez.
                    //INDEFINITE belirsiz süre göster kullanıcı başka tuşa basana kadar gösterir.
                    //setAction ile gösterilcek buton yapılır
                    Snackbar.make(view,"We need permission to show your gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin istiyicez.
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else {

                    //direkt izin istiyicez
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                //YUKARIDAN (izin verilmemiş) yerden registerLauncher() fonksyonuna geçiş yapıcaz

            }else{
                //izin verilmiş galeriye gidicez.
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }


    }
    //resigter etme activityresultlauncher ın ne olacağını ne yapacagını burada tanımlarım.Sonra oncreate altında çağırıcam.Tanımlamak izin istemek veya galeriye gitmek
    // değil sadece bunların en baştan ne yapacağını tanımlamak demek.Bunların ne yapacağını diğer düğmelere tıklayınca tanımlayamıyorum.
    public void registerLauncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                     Intent intentFromResult=result.getData();
                     if(intentFromResult!=null){
                         Uri imageData=intentFromResult.getData(); // image in nerede kayıtlı olduğunu söyler.

                         //Aldığım veriyi bitmap'e çeviriyorum.
                         try {
                             if(Build.VERSION.SDK_INT>=28){
                                 ImageDecoder.Source source=ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                                 selectedimage=ImageDecoder.decodeBitmap(source);
                                 binding.imageview.setImageBitmap(selectedimage);
                             }else {
                                 MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                                 binding.imageview.setImageBitmap(selectedimage);
                             }

                         }catch (Exception e){

                         }
                     }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(),new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
              if (result){
                  Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  activityResultLauncher.launch(intentToGallery);
              }else {
                  Toast.makeText(ArtActivity.this, "Permission Needed!!", Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

}