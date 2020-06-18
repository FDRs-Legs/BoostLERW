/*
 * Copyright (C) 2013 youten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package youten.redo.ble.readwrite;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

import youten.redo.ble.util.BleUtil;
import youten.redo.ble.util.BleUuid;

/**
 * BLEデバイスへのconnect・Service
 * Discoveryを実施し、Characteristicsのread/writeをハンドリングするActivity
 */
public class DeviceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "BLEDevice";

    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private int mStatus;

    private Button mReadBoardNameButton;
    private Button mReadManufacturerNameButton;
    private Button mReadSerialNumberButton;
    private Button mReadESCFirmwareButton;
    private Button mReadBatteryStateButton;
    private Button mReadBatteryFirmwareButton;
    private Button mReadBatteryLevelButton;
    private Button mReadBatteryTypeButton;
    private Button mReadOdometerButton;
    private Button mReadRideStateButton;
    private Button mWriteEcoLevelButton;
    private Button mWriteAlertLevelButton;

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override


        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                runOnUiThread(() -> {
                    mReadBoardNameButton.setEnabled(false);
                    mReadManufacturerNameButton.setEnabled(false);
                    mReadSerialNumberButton.setEnabled(false);
                    mWriteAlertLevelButton.setEnabled(false);
                    mWriteEcoLevelButton.setEnabled(false);
                    mReadESCFirmwareButton.setEnabled(false);
                    mReadBatteryFirmwareButton.setEnabled(false);
                    mReadBatteryStateButton.setEnabled(false);
                    mReadBatteryTypeButton.setEnabled(false);
                    mReadBatteryLevelButton.setEnabled(false);
                    mReadOdometerButton.setEnabled(false);
                    mReadRideStateButton.setEnabled(false);

                });
            }
        }
//playing with how to read the odometer

 

        public String byteArrayToHexString(final byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.reverse().append(String.format("%02x", b & 0xff));

            }
            return sb.toString();
        }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattCharacteristic temp;

            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadManufacturerNameButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING)));

                    mReadSerialNumberButton.setTag(service.getCharacteristic(UUID
                            .fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING)));
                    mReadESCFirmwareButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_FIRMWARE_ESC_STRING)));
                    runOnUiThread(new Runnable() {
                        public void run() {

                            mReadManufacturerNameButton.setEnabled(true);
                            mReadSerialNumberButton.setEnabled(true);
                            mReadESCFirmwareButton.setEnabled(true);
                        }

                    });
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION3.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadOdometerButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ODOMETER_STRING)));
                    mReadRideStateButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_RIDE_MODE_STRING)));

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadOdometerButton.setEnabled(true);
                            mReadRideStateButton.setEnabled(true);


                        }

                        ;
                    });
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION4.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadBoardNameButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BOARD_NAME_STRING)));

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBoardNameButton.setEnabled(true);



                        }

                        ;
                    });
                }

                if (BleUuid.SERVICE_DEVICE_INFORMATION2.equalsIgnoreCase(service
                        .getUuid().toString())) {


                    mReadBatteryFirmwareButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_FIRMWARE_STRING)));
                    mReadBatteryStateButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_STATE_STRING)));
                    mReadBatteryTypeButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_TYPE_STRING)));


                    mReadBatteryLevelButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_LEVEL_STRING)));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryFirmwareButton.setEnabled(true);
                            mReadBatteryStateButton.setEnabled(true);
                            mReadBatteryTypeButton.setEnabled(true);
                            mReadBatteryLevelButton.setEnabled(true);

                        }

                        ;
                    });
                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mWriteEcoLevelButton.setEnabled(true);
                        }

                        ;
                    });
                    mWriteEcoLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mWriteAlertLevelButton.setEnabled(true);
                        }

                        ;
                    });
                    mWriteAlertLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }

                ;
            });
        }

        ;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BleUuid.CHAR_MANUFACTURER_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadManufacturerNameButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_BOARD_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBoardNameButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                }else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadSerialNumberButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_FIRMWARE_ESC_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadESCFirmwareButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_BATTERY_TYPE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    String name2 = null;
                    if (Arrays.toString(temp3).equalsIgnoreCase("[2]"))
                        name2 = "XR";
                    else
                        name2 = "Standard";
                    final String name = name2;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryTypeButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_BATTERY_STATE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    String name = Arrays.toString(temp3);
                    if (name.equalsIgnoreCase("[1]")) {
                        name = "Charging";
                    } else {
                        name = "Not Charging";
                    }
                    String finalName = name;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryStateButton.setText(finalName);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_BATTERY_LEVEL_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    final String name = Arrays.toString(temp3);
                    //Log.d("YOLO__420__XXX", String.format("%s", Arrays.toString(temp3)));


                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryLevelButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_BATTERY_FIRMWARE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    final String name = byteArrayToHexString(temp3);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryFirmwareButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_ODOMETER_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {

//final String name2 = characteristic.getStringValue(0);
                    byte[] ascendingOdometer = characteristic.getValue();
                    int size = ascendingOdometer.length;
                    int index = size - 1;
                    int i;


                    byte[] formattedOdometer = new byte[size];
                    byte[] cell10s = new byte[1];
                    byte[] cell100s = new byte[1];
                    byte[] cell1000s = new byte[1];
                    byte[] cell10000s = new byte[1];



                    cell10000s [0]= formattedOdometer[0] = ascendingOdometer[3];
                   // formattedOdometer[0] = (byte) (formattedOdometer[0] & 0xffffffffl);
                    cell1000s [0]=formattedOdometer[1] = ascendingOdometer[2];
                    //formattedOdometer[1] = (byte) (formattedOdometer[1] & 0xffffffffl);
                    cell100s [0]=formattedOdometer[2] = ascendingOdometer[1];
                    cell10s [0]=formattedOdometer[3] = ascendingOdometer[0];


                    String finalOdometerDecimalValue =    byteArrayToHexString(ascendingOdometer);
                    String finalOdometerDecimalValue1 =    byteArrayToHexString(cell10s);
                    String finalOdometerDecimalValue2 =    byteArrayToHexString(cell100s);
                    String finalOdometerDecimalValue3 =    byteArrayToHexString(cell1000s);
                    String finalOdometerDecimalValue4 =    byteArrayToHexString(cell10000s);





                  // formattedOdometer[2] = (byte) (formattedOdometer[2] & 0xffffffffl);
                  StringBuilder cell0String =  new StringBuilder();
                    StringBuilder cell1String =  new StringBuilder();
                    StringBuilder cell2String =  new StringBuilder();
                    StringBuilder cell3String =  new StringBuilder();
                    StringBuilder cellString =  new StringBuilder();


                    cell0String.append(String.format("%02X", cell10s[0]));
                    cell1String.append(String.format("%02X", cell100s[0]));
                    cell2String.append(String.format("%02X", cell1000s[0]));
                   cell3String.append(String.format("%02X", cell10000s[0]));

                   cellString.append(cell3String);
                    cellString.append(cell2String);
                    cellString.append(cell1String);
                    cellString.append(cell0String);

                    int odometerDecimalValue=Integer.parseInt(String.valueOf(cellString), 16);




                    double eightyMMWheels=((2 * 3.141592653589793 * 40)/1000000) ;
                    double eightyFiveMMWheels = ((2 * 3.141592653589793 * 42.5)/1000000);
                    double ninetyMMWheels = ((2 * 3.141592653589793 * 45)/1000000);
//odometerDecimalValue = asun(odometerDecimalValue2);
                    double odometer80 = ((odometerDecimalValue * eightyMMWheels))/4;
                   double odometer85 = ((odometerDecimalValue * eightyFiveMMWheels))/4;
                    double odometer90 = ((odometerDecimalValue * ninetyMMWheels)/4);
                 String output80 = String.format("%.2f", odometer80);
                    String output85 = String.format("%.2f", odometer85);
                    String output90 = String.format("%.2f", odometer90);
                  //  String finalOdometerDecimalValue = odometerDecimalValue;

                  //  long finalOdometerDecimalValue1 = odometerDecimalValue2;
                    runOnUiThread(new Runnable() {
                        public void run() {
                             mReadOdometerButton.setText( output80+" km @ 80mm\n"+output85+" km @ 85mm\n"+output90+" km @ 90m");
//                           mReadOdometerButton.setText( finalOdometerDecimalValue1+"    cells2 "+finalOdometerDecimalValue2+"   cell3 "+finalOdometerDecimalValue3+" cell 4  "+finalOdometerDecimalValue4+"  formatted  "+ OdometerDecimalValue);
                           // mReadOdometerButton.setText( bi1+"    cells2 "+bi2+"   cell3 "+bi3+" cell 4  "+bi4+"  formatted  ");
                            //mReadOdometerButton.setText( cell3String +":"+cell2String +":"+cell1String +":"+cell0String +"  formatted  "+decimal);



                        }

                        ;
                    });
                } else if (BleUuid.CHAR_RIDE_MODE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    final String name;
                    String name1 = null;
                    if (Arrays.toString(temp3).equalsIgnoreCase("[2]"))
                        name1 = "Expert";
                    if (Arrays.toString(temp3).equalsIgnoreCase("[1]"))
                        name1 = "Eco";
                    if (Arrays.toString(temp3).equalsIgnoreCase("[0]"))
                        name1 = "Turtle";


                    name = name1;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadRideStateButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {

            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }

                ;
            });
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);

        // state
        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mReadBoardNameButton = findViewById(R.id.read_board_name_button);
        mReadBoardNameButton.setOnClickListener(this);
        mReadManufacturerNameButton = findViewById(R.id.read_manufacturer_name_button);
        mReadManufacturerNameButton.setOnClickListener(this);
        mReadSerialNumberButton = findViewById(R.id.read_serial_number_button);
        mReadSerialNumberButton.setOnClickListener(this);
        mReadESCFirmwareButton = findViewById(R.id.read_esc_firmware_button);
        mReadESCFirmwareButton.setOnClickListener(this);
        mReadBatteryFirmwareButton = findViewById(R.id.read_battery_firmware_button);
        mReadBatteryFirmwareButton.setOnClickListener(this);
        mReadBatteryStateButton = findViewById(R.id.read_battery_state_button);
        mReadBatteryStateButton.setOnClickListener(this);
        mReadBatteryLevelButton = findViewById(R.id.read_battery_level_button);
        mReadBatteryLevelButton.setOnClickListener(this);
        mReadBatteryTypeButton = findViewById(R.id.read_battery_type_button);
        mReadBatteryTypeButton.setOnClickListener(this);
        mWriteEcoLevelButton = findViewById(R.id.write_eco_level_button);
        mWriteEcoLevelButton.setOnClickListener(this);
        mWriteAlertLevelButton = findViewById(R.id.write_alert_level_button);
        mWriteAlertLevelButton.setOnClickListener(this);
        mReadOdometerButton = (Button) findViewById(R.id.read_odometer_button);
        mReadOdometerButton.setOnClickListener(this);
        mReadRideStateButton = findViewById(R.id.read_ride_state_button);
        mReadRideStateButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnGatt != null) {
            if ((mStatus != BluetoothProfile.STATE_DISCONNECTING)
                    && (mStatus != BluetoothProfile.STATE_DISCONNECTED)) {
                mConnGatt.disconnect();
            }
            mConnGatt.close();
            mConnGatt = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_manufacturer_name_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.read_board_name_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.read_serial_number_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.read_esc_firmware_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_battery_state_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_battery_level_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_battery_firmware_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_battery_type_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_ride_state_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.read_odometer_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.write_eco_level_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[]{(byte) 0x01});
                if (mConnGatt.writeCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.write_alert_level_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[]{(byte) 0x02});
                if (mConnGatt.writeCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        }
    }

    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        // check BluetoothDevice
        if (mDevice == null) {
            mDevice = getBTDeviceExtra();
            if (mDevice == null) {
                finish();
                return;
            }
        }

        // button disable
        mReadBoardNameButton.setEnabled(false);
        mReadManufacturerNameButton.setEnabled(false);
        mReadSerialNumberButton.setEnabled(false);
        mWriteAlertLevelButton.setEnabled(false);
        mWriteEcoLevelButton.setEnabled(false);
        mReadESCFirmwareButton.setEnabled(false);
        mReadBatteryFirmwareButton.setEnabled(false);
        mReadBatteryStateButton.setEnabled(false);
        mReadBatteryTypeButton.setEnabled(false);
        mReadBatteryLevelButton.setEnabled(false);
        mReadRideStateButton.setEnabled(false);
        mReadOdometerButton.setEnabled(false);

        // connect to Gatt
        if ((mConnGatt == null)
                && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            // try to connect
            mConnGatt = mDevice.connectGatt(this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                Log.e(TAG, "state error");
                finish();
                return;
            }
        }
        setProgressBarIndeterminateVisibility(true);
    }

    private BluetoothDevice getBTDeviceExtra() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }

        return extras.getParcelable(EXTRA_BLUETOOTH_DEVICE);
    }

}
