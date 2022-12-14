package be.hvwebsites.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.fragments.ProductListFragment;
import be.hvwebsites.shopping.fragments.ShopListFragment;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ListActivity extends AppCompatActivity {
    private ShopEntitiesViewModel viewModel;
    private String listType;
    private String baseDir;
    private String baseSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_list);

        // Intent definieren
        Intent listIntent = getIntent();

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Basis directory definitie
        baseSwitch = listIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_SELECTION);
        if (baseSwitch == null){
            // Als baseSwitch = null, neem dan default
            baseSwitch = SpecificData.BASE_DEFAULT;
        }
        if (baseSwitch.equals(SpecificData.BASE_INTERNAL)){
            // Internal Files
            baseDir = getBaseContext().getFilesDir().getAbsolutePath();
        }else {
            // External files
            baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        }
        Toast.makeText(A4ListActivity.this,
                "baseSwitch is " + baseSwitch,
                Toast.LENGTH_SHORT).show();

        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
            viewModel.setBaseSwitch(baseSwitch);
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(A4ListActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(A4ListActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // ProductinShops clearen uit commentaar zetten indien nodig
        int temp = 0;
        if (temp == 1){
            viewModel.clearAllProductInShop();
        }
        int highestShop = viewModel.determineHighestShopID();
        int highestProd = viewModel.determineHighestProductID();

        // Data uit intent halen als die er is
        CookieRepository cookieRepository = new CookieRepository(baseDir);
        if (listIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_TYPE)){
            listType = listIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
            // listtype in cookie steken
            cookieRepository.addCookie(new Cookie(SpecificData.LIST_TYPE, listType));
        }else {
            // er is geen intent, listtype ophalen als Cookie
            listType = cookieRepository.getCookieValueFromLabel(SpecificData.LIST_TYPE);
        }

        // Creeer fragment vr gepaste recyclerview
        if (savedInstanceState == null ){
            switch (listType){
                case SpecificData.LIST_TYPE_1:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentRecyclerV, ShopListFragment.class, null)
                            .commit();
                    break;
                case SpecificData.LIST_TYPE_2:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentRecyclerV, ProductListFragment.class, null)
                            .commit();
                    break;
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(A4ListActivity.this,
                        ManageItemActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, listType);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, baseSwitch);
                startActivity(intent);
            }
        });

        // data zit nu in ViewModel, tonen op scherm afhankelijk vn type
        switch (listType) {
            case SpecificData.LIST_TYPE_1:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T1);
                break;
            case SpecificData.LIST_TYPE_2:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + listType);
        }
    }
}