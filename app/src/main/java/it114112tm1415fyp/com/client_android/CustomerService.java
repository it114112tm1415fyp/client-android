package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class CustomerService extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_service);
        String[] service = new String[]{"About Us", "FAQ", getString(R.string.user_licence_agreement), "Contact us"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1, service) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                return view;
            }
        };
        final ListView lstService = (ListView) findViewById(R.id.ls_customer_service);
        lstService.setAdapter(adapter);
        lstService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0: {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerService.this);
                        alertDialog.setTitle("About As");
                        alertDialog.setMessage("Leader Teacher : \nKenneth Sizto\nGroup Member : \nLuk Wai\nLo Ki Fung\nLi Kin Lun\nHo Yuen San");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        break;
                    }
                    case 1:
                        break;
                    case 2: {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerService.this);
                        alertDialog.setTitle("Terms & Conditions of Use");
                        TextView textView = new TextView(CustomerService.this);
                        textView.setTextSize(14);
                        textView.setPadding(3, 3, 3, 3);
                        textView.setTextColor(Color.BLACK);
                        textView.setText(
                                "Access to and use of this Logistic System site and Logistic System’ official sites in social media networks such as Facebook, Twitter, blogs and wikis (collectively \"Sites\") is subject to these terms and conditions. Use of the Sites constitutes acceptance of these terms and conditions in full.\n" +
                                        "\n" +
                                        "\n" +
                                        "Access to and use of the information, services and products provided on the Sites by Logistic System Network Limited and its subsidiaries or affiliated companies (collectively \"Logistic System\") are subject to the following terms and conditions:\n" +
                                        "\n" +
                                        "\n" +
                                        "The Sites and the information, names, images, pictures, logos and icons regarding or relating to Logistic System or its products and services is provided \"AS IS\" without any representation or endorsement made and without warranty of any kind, whether expressed or implied, including, but not limited to the implied warranties of  merchantability, fitness for a particular purpose or non-infringement. \n" +
                                        "\n" +
                                        "\n" +
                                        "The texts, images, audio/video clips, information, services, software and other materials made available to you on the Sites (\"Contents\") are protected by copyrights or other intellectual property rights and laws. You also agree not to adopt, alter or create a derivative work from any of the Contents or reproduce, republish, download, distribute or exploit the Contents for any use other than your personal non-commercial use. Any other use of the Contents requires the prior written permission of Logistic System or the relevant third party owner. \n" +
                                        "\n" +
                                        "\n" +
                                        "Logistic System does not verify nor warrant the correctness and accuracy of the Contents and assume no liabilities whatsoever with respect to any of your loss or damage incurred directly or indirectly due to the use of the Contents. Moreover, the information provided on the Sites has not been written to meet your individual requirements and it is your sole responsibility to satisfy yourself prior to using the Contents in any way that it is suitable for your purposes. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree that Logistic System is not liable for any liability you incur as a result of using the Contents. In no event shall Logistic System or any of its contractors or employees be liable for any damages whatsoever, including direct, special, indirect or consequential damages, resulting from or in connection with the access to or use of the Sites or the use and dissemination of the Contents.\n" +
                                        "\n" +
                                        "\n" +
                                        "The Contents contained on the Sites may be periodically updated. Logistic System reserves the rights at any time and without notice to make modifications, improvements and/or changes to the Contents, these conditions, names, images, pictures, logos and icons or the products and services relating to Logistic System referred to on the Sites. As the Internet is maintained independently at thousands of sites around the world, the Contents or any part thereof that may be accessed through the Sites may originate outside the boundaries of the Sites. Therefore, Logistic System excludes any obligation or responsibility regarding any Contents or part thereof which is/are derived, obtained, accessed within, through or outside the Sites.\n" +
                                        "\n" +
                                        "\n" +
                                        "The products and services mentioned in the Sites are at all times subject to Logistic System' Standard Terms and Conditions for Services and other relevant conditions. Local variations may exist and apply depending on the country of origin of the shipment. Please contact the nearest Logistic System' local office to obtain a copy of the local terms and conditions. \n" +
                                        "\n" +
                                        "\n" +
                                        "Whilst Logistic System makes all reasonable attempts to exclude viruses from the Sites, it cannot ensure such exclusion and no liability is accepted for viruses. Thus, you are recommended to take all appropriate safeguards before downloading information from the Sites. In other words, it is up to you to take precautions to ensure that whatever you select for your use from the Site is free of such items as viruses, worms, trojan horses and other forms of malware. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree to neither disturb the normal operation of the Sites nor infringe the integrity of the Sites by hacking, altering the information contained in the Sites, prevent or limit access to the Sites to other users, or otherwise. \n" +
                                        "\n" +
                                        "\n" +
                                        "You are responsible for complying with the laws of the jurisdiction from which you are accessing the Sites and you agree that you will not access or use the information on the Sites in violation of such laws. Unless expressly stated otherwise herein, any information submitted by you through the Sites shall be deemed non-confidential and non-proprietary. You represent that you have the lawful right to submit such information and agree that you will not submit any information unless you are legally entitled to do so. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree to indemnify and hold Logistic System, its directors and officers from and against all claims, losses, damage, liabilities and expenses resulting from your breach of these Terms & Conditions. \n" +
                                        "\n" +
                                        "\n" +
                                        "Any legal action or proceeding relating to the Sites shall be governed by the laws of The Hong Kong SAR. If you attempt to bring any legal proceedings against Logistic System you specifically acknowledge that Logistic System is free to choose the jurisdiction of our preference as to where such action against us may be held. \n" +
                                        "\n" +
                                        "\n" +
                                        "Logistic System may suspend, block, and/ or terminate your use of the Sites at any time, for any reason or for no reason at all. You are personally liable for any orders that you place or charges that you incur prior to termination. Logistic System hereby expressly reserves its rights to revise and amend these terms and conditions or to change, suspend, or discontinue all or any aspects of the Sites at its absolute discretion without prior notice."
                        );
                        ScrollView scrollView = new ScrollView(getApplicationContext());
                        scrollView.addView(textView);
                        alertDialog.setView(scrollView);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        break;
                    }
                    case 3:
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("text/plain");
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"it114112.2013@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "Help");
                        email.putExtra(Intent.EXTRA_TEXT, "testtest");
                        startActivity(Intent.createChooser(email, "Choose Email Client"));
                        break;
//                    case 4:
//                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                        i.setType("image/*");
//                        startActivityForResult(Intent.createChooser(i, "Select Picture"), 1000);
//                        break;
                    default:
                        break;
                }

            }
        });
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if ((requestCode == 1000) && (resultCode == RESULT_OK) && (data != null)) {
//            Uri uri = data.getData();
//            Intent i = new Intent(Intent.ACTION_SEND);
//            i.setType("image/*");
//            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"it114112.2013@gmail.com"});
//            i.putExtra(Intent.EXTRA_SUBJECT, "Help");
//            i.putExtra(Intent.EXTRA_TEXT, "testtest");
//            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + uri.getPath()));
//            startActivity(Intent.createChooser(i, "Select "));
//        } else
//            Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
//    }
}