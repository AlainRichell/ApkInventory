package uci.cisol.apkinventory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import uci.cisol.apkinventory.ui.login.LoginActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logo = findViewById(R.id.splash_screen_logo);
        TextView name = findViewById(R.id.splash_screen_name);
        Animation fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);

        logo.setAnimation(fromBottom);
        name.setAnimation(fadeIn);
        fadeIn.setStartOffset(1000);
        int SPLASH_TIME_OUT = 2000;

        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
