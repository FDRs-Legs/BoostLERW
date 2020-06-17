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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import youten.redo.ble.util.BleUtil;
import youten.redo.ble.util.BleUuid;

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

import javax.crypto.spec.IvParameterSpec;

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
                runOnUiThread(new Runnable() {
                    public void run() {
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

                    }

                    ;
                });
            }
        }
//playing with how to read the odometer

        public int toUnsignedInt(byte x) {
            return ((int) x) & 0xff;
        }
        public String toHex(byte[] arg) {
            return String.format("%02x", arg);
        }

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
                    byte[] temp3 = characteristic.getValue();
                    int size = temp3.length;
                    int index = size - 1;
                    int i;

                    //String odotemp1 = toHex(temp3);
                    byte[] temp = new byte[size - 1];
                    //for(i = 0; i <size; i++){
                    //		temp[i] = temp3 [index-i];
                    //}
                    byte[] odometer1 = new byte[1];
                    odometer1[0] = temp3[0];
                    byte[] odometer2 = new byte[1];
                    odometer2[0] = temp3[3];
                    byte[] odometer3 = new byte[1];
                    odometer3[0] = temp3[2];
                    byte[] odometer4 = new byte[1];
                    odometer4[0] = temp3[1];

                    temp[0] = temp3[3];
                    temp[1] = temp3[2];
                    temp[2] = temp3[1];
                    //temp[3] = temp3[0];
                    int tempint = toUnsignedInt(temp[0]);
                    temp[1] = temp3[2];
                    temp[2] = temp3[1];
                    //temp[3] = temp3[0];
                    //temp3 = characteristic.getValue();
                    //Integer temp3 = characteristic.getIntValue(0,0);
                    //final String name2 =  name.toString();
                    String temp2 = byteArrayToHexString(temp);
                    //String odotemp1 = odometer4[0]+"|"+ odometer3[0] +"|"+ odometer2[0];
                    //final String name2 = Arrays.toString(temp3);
                    long n = Long.parseLong(String.valueOf(temp2), 16);
                    long n4 = Long.parseLong(String.valueOf(odometer4[0]), 16);
                    long n3 = Long.parseLong(String.valueOf(odometer3[0]), 16);
                    long n2 = Long.parseLong(String.valueOf(odometer2[0]), 16);
                    long n1 = Long.parseLong(String.valueOf(odometer1[0]), 16);
                    double no = (n * (2 * 3.141592653589793 * 42.5));
                    //Log.d("YOLO__420__XXX", String.format("%s", n));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //mReadOdometerButton.setText(no + " km @ 80mm" + odometer4[0]+temp[0]+"|"+ odometer3[0] +temp[1]+"|"+ odometer2[0]+temp[2] +"|"+odometer1[0] +"   "+odotemp1);
                            mReadOdometerButton.setText(no + " km @ 80mm usig:" + temp3[0] + "|" + temp3[1] + "|" + temp3[2] + "|" + temp3[3]+" HEX: " + temp2);
                            //	mReadOdometerButton.setText( " miles @ 80mm" + temp3);
                            setProgressBarIndeterminateVisibility(false);
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
        mReadBoardNameButton = (Button) findViewById(R.id.read_board_name_button);
        mReadBoardNameButton.setOnClickListener(this);
        mReadManufacturerNameButton = (Button) findViewById(R.id.read_manufacturer_name_button);
        mReadManufacturerNameButton.setOnClickListener(this);
        mReadSerialNumberButton = (Button) findViewById(R.id.read_serial_number_button);
        mReadSerialNumberButton.setOnClickListener(this);
        mReadESCFirmwareButton = (Button) findViewById(R.id.read_esc_firmware_button);
        mReadESCFirmwareButton.setOnClickListener(this);
        mReadBatteryFirmwareButton = (Button) findViewById(R.id.read_battery_firmware_button);
        mReadBatteryFirmwareButton.setOnClickListener(this);
        mReadBatteryStateButton = (Button) findViewById(R.id.read_battery_state_button);
        mReadBatteryStateButton.setOnClickListener(this);
        mReadBatteryLevelButton = (Button) findViewById(R.id.read_battery_level_button);
        mReadBatteryLevelButton.setOnClickListener(this);
        mReadBatteryTypeButton = (Button) findViewById(R.id.read_battery_type_button);
        mReadBatteryTypeButton.setOnClickListener(this);
        mWriteEcoLevelButton = (Button) findViewById(R.id.write_eco_level_button);
        mWriteEcoLevelButton.setOnClickListener(this);
        mWriteAlertLevelButton = (Button) findViewById(R.id.write_alert_level_button);
        mWriteAlertLevelButton.setOnClickListener(this);
        mReadOdometerButton = (Button) findViewById(R.id.read_odometer_button);
        mReadOdometerButton.setOnClickListener(this);
        mReadRideStateButton = (Button) findViewById(R.id.read_ride_state_button);
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
