package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import it114112tm1415fyp.com.util.PublicData;

public class ShipmentDestinationList_Activity extends Activity {

    private ListView lstDestination;

    private String previousActivity = "";

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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void findAllView() {
        lstDestination = (ListView) findViewById(R.id.lstAddress);
    }

    private void setAllView() {
        final ShipmentLocationList_Adapter adapter = new ShipmentLocationList_Adapter(this);
        if (PublicData.receiverAddressList.size() > 0)
            adapter.addSectionHeaderItem("Receiver Address");
        for (int i = 0; i < PublicData.receiverAddressList.size(); i++)
            adapter.addItem(PublicData.receiverAddressList.get(i).getLongAddress());
        adapter.addSectionHeaderItem("Shop Address");
        for (int i = 0; i < PublicData.shopsList.size(); i++)
            adapter.addItem(PublicData.shopsList.get(i).getShortAddress());
        lstDestination.setAdapter(adapter);
        lstDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == 0) {
                    String destination = adapter.getItem(position);
                    Log.e("destination", destination);
                    for (int i = 0; i < PublicData.shopsList.size(); i++) {
                        if (PublicData.shopsList.get(i).getShortAddress().equals(destination)) {
                            if (previousActivity.equals("EditOrder")) {
                                EditShipment_Activity.destination = destination;
                                EditShipment_Activity.destinationId = PublicData.shopsList.get(i).getAddressId();
                                EditShipment_Activity.destinationType = "Shop";
                                EditShipment_Activity.destinationChange = true;
                                setResult(RESULT_OK);
                                finish();
                                return;
                            } else if (previousActivity.equals("NewOrder")) {
                                NewShipment_Activity.destination = destination;
                                NewShipment_Activity.destinationId =  PublicData.shopsList.get(i).getAddressId();
                                NewShipment_Activity.destinationType = "Shop";
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < PublicData.receiverAddressList.size(); i++) {
                        if (PublicData.receiverAddressList.get(i).getLongAddress().equals(destination)) {
                            if (destination.matches(PublicData.receiverAddressList.get(i).getLongAddress())) {
                                if (previousActivity.equals("EditOrder")) {
                                    EditShipment_Activity.destination = destination;
                                    EditShipment_Activity.destinationId = PublicData.receiverAddressList.get(i).getAddressId();
                                    EditShipment_Activity.destinationType = "SpecifyAddress";
                                    EditShipment_Activity.destinationChange = true;
                                    setResult(RESULT_OK);
                                    finish();
                                    return;
                                } else if (previousActivity.equals("NewOrder")) {
                                    NewShipment_Activity.destination = destination;
                                    NewShipment_Activity.destinationId = PublicData.receiverAddressList.get(i).getAddressId();
                                    NewShipment_Activity.destinationType = "SpecifyAddress";
                                    break;
                                }
                            }
                        }
                    }
                    Intent i = new Intent(getApplicationContext(), ShipmentDepartureList_Activity.class);
                    i.putExtra("activity", "NewOrder");
                    startActivityForResult(i, 1);
                }
            }
        });
    }

}