package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;

public class MainActivity extends AppCompatActivity {
    // Device
    private String deviceModel = Build.MODEL;
    // Basis Directory waar de bestanden worden bewaard op het toestel: internal of external switch
    private String basisSwitch = SpecificData.BASE_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent definieren
        Intent newItemIntent = getIntent();
        if (newItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_SELECTION)){
            basisSwitch = newItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_SELECTION);
        }

        TextView basisSwitchView = findViewById(R.id.basisSwitch);
        basisSwitchView.setText(basisSwitch);
        Button buttonShops = findViewById(R.id.shops);
        buttonShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_1);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, basisSwitch);
                startActivity(intent);
            }
        });
        Button buttonProducts = findViewById(R.id.products);
        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_2);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, basisSwitch);
                startActivity(intent);

            }
        });
        Button buttonShopping = findViewById(R.id.shopping);
        buttonShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ShoppingListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, basisSwitch);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        /* Voor GT-I9100, base switch opties invisible zetten geeft nullpointerexception op setVisible !
        if (deviceModel.equals("GT-I9100")){
            MenuItem mBU = findViewById(R.id.menu_bu);
            mBU.setVisible(false);
            MenuItem mEX = findViewById(R.id.menu_set_base_switch_external);
            mEX.setVisible(false);
            MenuItem mIN = findViewById(R.id.menu_set_base_switch_internal);
            mIN.setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // View om toestand basisswitch te laten zien
        TextView basisSwitchView = findViewById(R.id.basisSwitch);

        // Welke menu optie is er gekozen ?
        switch (item.getItemId()) {
            case R.id.menu_bu:
                // Maak een copy vn je internal files nr external files (werkt niet voor oud model !)
                if (!deviceModel.equals("GT-I9100")){
                    Intent mainIntent = new Intent(MainActivity.this, BackUpToExternal.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel ondersteunt geen external files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_set_base_switch_external:
                // Zet BASE_SWITCH to external
                if (!deviceModel.equals("GT-I9100")) {
                    basisSwitch = SpecificData.BASE_EXTERNAL;
                    basisSwitchView.setText(basisSwitch);
                    Toast.makeText(MainActivity.this,
                            "External files geactiveerd !",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel ondersteunt geen external files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_set_base_switch_internal:
                // Zet BASE_SWITCH to internal
                if (!deviceModel.equals("GT-I9100")) {
                    basisSwitch = SpecificData.BASE_INTERNAL;
                    basisSwitchView.setText(basisSwitch);
                    Toast.makeText(MainActivity.this,
                            "Internal files geactiveerd !",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel werkt altijd met internal files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_initiate_bt:
                // Initiate bluetooth communication
                Intent mainIntent = new Intent(MainActivity.this, BluetoothCom.class);
                startActivity(mainIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
}