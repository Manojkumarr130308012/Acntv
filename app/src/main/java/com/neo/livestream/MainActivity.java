package com.neo.livestream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
ImageView fb,web,watsapp,youtube;
    GifImageView image;
    TextView url;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=findViewById(R.id.image);
        fb=findViewById(R.id.fb);
        web=findViewById(R.id.web);
        watsapp=findViewById(R.id.watsapp);
        youtube=findViewById(R.id.youtube);
        url=findViewById(R.id.url);


        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://neophrontech.com/"));
                startActivity(intent);
            }
        });
        watsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String digits = "\\d+";
                String mob_num = "9715806000";
                if (mob_num.matches(digits)) {
                    try {
                        //linking for whatsapp
                        Uri uri = Uri.parse("whatsapp://send?phone=+91" + mob_num);
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        //if you're in anonymous class pass context like "YourActivity.this"
//                        Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/erodeacntv/community/?ref=page_internal"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/erodeacntv/community/?ref=page_internal")));
                }
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://acntverode.business.site/"));
                startActivity(intent);
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC8OzpbP11lX2isaXc0aZKqw"));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/channel/UC8OzpbP11lX2isaXc0aZKqw"));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Stream.class);
                startActivity(i);
            }
        });


    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}