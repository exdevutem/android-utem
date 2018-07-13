package cl.inndev.miutem.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import cl.inndev.miutem.R;

public class DonacionActivity extends AppCompatActivity {

    ImageButton mButtonFlow;
    ImageButton mButtonMach;
    ImageButton mButtonPaypal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donacion);

        mButtonFlow = findViewById(R.id.button_flow);
        mButtonMach = findViewById(R.id.button_mach);
        mButtonPaypal = findViewById(R.id.button_paypal);

        mButtonFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.flow.cl/btn.php?token=dvdpqo2";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mButtonPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.paypal.me/mapacheverdugo";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mButtonMach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getPackageManager();
                String url;
                if (isPackageInstalled("cl.bci.sismo.mach", pm)) {
                    url = "https://mach.app.link/FmMEinc4bN";
                } else {
                    url = "https://bajamach.com/FmMEinc4bN";
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mButtonPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getPackageManager();
                String url = "https://www.paypal.me/mapacheverdugo";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
