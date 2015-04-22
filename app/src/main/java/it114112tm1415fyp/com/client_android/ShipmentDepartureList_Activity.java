package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import it114112tm1415fyp.com.util.PublicData;

public class ShipmentDepartureList_Activity extends Activity {

    private ListView lstDeparture;

    private String previousActivity = "";
    String departure;
    int departureId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shipment_choose_user_address);
        previousActivity = getIntent().getStringExtra("activity");
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && previousActivity.equals("EditOrder")) {
            EditShipment_Activity.departure = departure;
            EditShipment_Activity.departureId = departureId;
            EditShipment_Activity.departureType = "SpecifyAddress";
            EditShipment_Activity.departureChange = true;
            setResult(RESULT_OK);
            finish();
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void findAllView() {
        lstDeparture = (ListView) findViewById(R.id.lstAddress);
    }

    private void setAllView() {
        final ShipmentLocationList_Adapter adapter = new ShipmentLocationList_Adapter(this);
        if (PublicData.UserAddresses.length() > 0) {
            adapter.addSectionHeaderItem("Sender Address");
            for (int i = 0; i < PublicData.UserAddresses.length(); i++)
                try {
                    adapter.addItem(PublicData.UserAddresses.getJSONObject(i).getString("long_name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            adapter.addSectionHeaderItem("Shop Address");
        }
        for (int i = 0; i < PublicData.shopsList.size(); i++)
            adapter.addItem(PublicData.shopsList.get(i).getShortAddress());
        lstDeparture.setAdapter(adapter);
        lstDeparture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == 0) {
                    Intent intent = new Intent();
                    departure = adapter.getItem(position);
                    Log.e("departure", departure);
                    for (int i = 0; i < PublicData.shopsList.size(); i++) {
                        if (PublicData.shopsList.get(i).getShortAddress().equals(departure)) {
                            departureId = PublicData.shopsList.get(i).getAddressId();
                            if (previousActivity.equals("EditOrder")) {
                                EditShipment_Activity.departure = departure;
                                EditShipment_Activity.departureId = departureId;
                                EditShipment_Activity.departureType = "Shop";
                                EditShipment_Activity.departureChange = true;
                                setResult(RESULT_OK);
                                finish();
                                return;
                            } else if (previousActivity.equals("NewOrder")) {
                                NewShipment_Activity.departure = departure;
                                NewShipment_Activity.departureId = departureId;
                                NewShipment_Activity.departureType = "Shop";
                                intent = new Intent(getApplicationContext(), NewShipmentConfirm_Activity.class);
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < PublicData.UserAddresses.length(); i++) {
                        try {
                            if (PublicData.UserAddresses.getJSONObject(i).getString("long_name").equals(departure)) {
                                departureId = PublicData.UserAddresses.getJSONObject(i).getInt("id");
                                if (previousActivity.equals("EditOrder")) {
                                    intent = new Intent(getApplicationContext(), ShipmentChooseReceiveTime_Activity.class);
                                    intent.putExtra("activity", "EditOrder");
                                } else if (previousActivity.equals("NewOrder")) {
                                    NewShipment_Activity.departure = departure;
                                    NewShipment_Activity.departureId = departureId;
                                    NewShipment_Activity.departureType = "SpecifyAddress";
                                    intent = new Intent(getApplicationContext(), ShipmentChooseReceiveTime_Activity.class);
                                    intent.putExtra("activity", "NewOrder");
                                }
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

}