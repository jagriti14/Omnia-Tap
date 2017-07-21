package in.desireplace.waytogo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import in.desireplace.waytogo.Constants;
import in.desireplace.waytogo.R;
import in.desireplace.waytogo.adapters.SavedAddressAdapter;
import in.desireplace.waytogo.models.SavedAddresses;

public class SavedAddressesActivity extends AppCompatActivity implements SavedAddressAdapter.Callback {

    private SavedAddressAdapter mAdapter;

    private String identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_addresses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        identifier = getIntent().getStringExtra("identifier");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEditDeleteDialog(null);
            }
        });

        if (identifier == null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView addressList = (RecyclerView) findViewById(R.id.address_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        addressList.setLayoutManager(manager);
        mAdapter = new SavedAddressAdapter(this);
        addressList.setAdapter(mAdapter);
    }

    private void showAddEditDeleteDialog(final SavedAddresses addresses) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Address");
        View view = getLayoutInflater().inflate(R.layout.dailog_save_address, null, false);
        builder.setView(view);

        final EditText fullNameEditText = (EditText) view.findViewById(R.id.full_name_input);
        final EditText mobileNumberTextView = (EditText) view.findViewById(R.id.mobile_number_input);
        final EditText pinCodeTextView = (EditText) view.findViewById(R.id.pincode_input);
        final EditText houseNumberTextView = (EditText) view.findViewById(R.id.house_no_input);
        final EditText localityTextView = (EditText) view.findViewById(R.id.locality_input);
        final EditText cityTextView = (EditText) view.findViewById(R.id.city_input);
        final EditText stateTextView = (EditText) view.findViewById(R.id.state_input);

        if (addresses != null) {
            fullNameEditText.setText(addresses.getFullName());
            mobileNumberTextView.setText(addresses.getMobileNumber());
            pinCodeTextView.setText(addresses.getPinCode());
            houseNumberTextView.setText(addresses.getHouseNumber());
            localityTextView.setText(addresses.getLocality());
            cityTextView.setText(addresses.getCity());
            stateTextView.setText(addresses.getState());

            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String fullName = fullNameEditText.getText().toString();
                    String mobileNumber = mobileNumberTextView.getText().toString();
                    String pinCode = pinCodeTextView.getText().toString();
                    String houseNumber = houseNumberTextView.getText().toString();
                    String locality = localityTextView.getText().toString();
                    String city = cityTextView.getText().toString();
                    String state = stateTextView.getText().toString();
                    mAdapter.firebaseUpdate(addresses, fullName, mobileNumber, pinCode, houseNumber, locality, city, state);
                }
            });
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAdapter.firebaseDelete(addresses);
                }
            });
        } else {
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String fullName = fullNameEditText.getText().toString();
                    String mobileNumber = mobileNumberTextView.getText().toString();
                    String pinCode = pinCodeTextView.getText().toString();
                    String houseNumber = houseNumberTextView.getText().toString();
                    String locality = localityTextView.getText().toString();
                    String city = cityTextView.getText().toString();
                    String state = stateTextView.getText().toString();

                    SavedAddresses addresses = new SavedAddresses(fullName, mobileNumber, pinCode, houseNumber, locality, city, state);
                    mAdapter.firebaseAdd(addresses);
                }
            });
        }
        builder.create().show();
    }

    @Override
    public void onEditButtonClick(SavedAddresses addresses) {
        showAddEditDeleteDialog(addresses);
    }

    @Override
    public void onItemClick(SavedAddresses addresses) {
        if (identifier == null) {
            Intent intent;
            Bundle bundle = getIntent().getExtras();

            String service_type = bundle.getString(Constants.SERVICE_TYPE);

            bundle = new Bundle();
            intent = new Intent(SavedAddressesActivity.this, ConfirmDetailsActivity.class);

            bundle.putString(Constants.SERVICE_TYPE, service_type);

            bundle.putString(Constants.SELECTED_NAME, addresses.getFullName());
            bundle.putString(Constants.SELECTED_MOBILE_NUMBER, addresses.getMobileNumber());
            bundle.putString(Constants.SELECTED_PIN_CODE, addresses.getPinCode());
            bundle.putString(Constants.SELECTED_HOUSE_NUMBER, addresses.getHouseNumber());
            bundle.putString(Constants.SELECTED_LOCALITY, addresses.getLocality());
            bundle.putString(Constants.SELECTED_CITY, addresses.getCity());
            bundle.putString(Constants.SELECTED_STATE, addresses.getState());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
