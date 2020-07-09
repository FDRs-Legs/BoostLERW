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

import android.annotation.SuppressLint;
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

import java.util.Arrays;
import java.util.UUID;

import youten.redo.ble.util.BleUtil;
import youten.redo.ble.util.BleUuid;

/**
 starting to test additional views
 */
public class DeviceActivityTesting extends Activity implements View.OnClickListener {
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

    private Button mSwitchActivityButton;

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


                          mReadESCFirmwareButton.setEnabled(false);
                         mReadBatteryFirmwareButton.setEnabled(false);

                          mReadBatteryStateButton.setEnabled(false);
                          mReadBatteryTypeButton.setEnabled(false);
                           mReadBatteryLevelButton.setEnabled(false);


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


//determines if the Characteristic is available for the service
        @Override

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


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

                if (BleUuid.SERVICE_DEVICE_INFORMATION4.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadBoardNameButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BOARD_NAME_STRING)));

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBoardNameButton.setEnabled(true);

                        }

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
            }
        }

       //Determines what to do with the data in the characteristic



        @SuppressLint("SetTextI18n")
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


                    });
                }else if (BleUuid.CHAR_BOARD_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(() -> {
                        mReadBoardNameButton.setText(name);
                        setProgressBarIndeterminateVisibility(false);
                    });
                }else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadSerialNumberButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                    });
                } else if (BleUuid.CHAR_FIRMWARE_ESC_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadESCFirmwareButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } else if (BleUuid.CHAR_BATTERY_TYPE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    String name2 = null;
                    if (Arrays.toString(temp3).equalsIgnoreCase("[2]"))
                        name2 = "XR";
                    else if (Arrays.toString(temp3).equalsIgnoreCase("[1]"))
                        name2 = "SR";
                    else
                        name2 =  "No Battery";

                    final String name = name2;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryTypeButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }


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


                    });
                }   else if (BleUuid.CHAR_BATTERY_LEVEL_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    final String name = Arrays.toString(temp3);



                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryLevelButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }


                    });
                } else if (BleUuid.CHAR_BATTERY_FIRMWARE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {

                    byte[] temp3 = characteristic.getValue();
                    final String name = byteArrayToHexString(temp3);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadBatteryFirmwareButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }


                    });
                    ///Odometer calculation is a mess. Looks like the values are updated left to right and the values measured in quarter rotations
                    //This could stand a lot of clean up and still does not jive with other apps reporting mileage
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {

            runOnUiThread(() -> setProgressBarIndeterminateVisibility(false));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_devicetesting);

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


        mSwitchActivityButton = findViewById(R.id.switch_activity_id);
        mSwitchActivityButton.setOnClickListener(this);


        mReadBatteryStateButton = findViewById(R.id.read_battery_state_button);
        mReadBatteryStateButton.setOnClickListener(this);
        mReadBatteryLevelButton = findViewById(R.id.read_battery_level_button);
        mReadBatteryLevelButton.setOnClickListener(this);
        mReadBatteryTypeButton = findViewById(R.id.read_battery_type_button);
        mReadBatteryTypeButton.setOnClickListener(this);




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

        }  else if (v.getId() == R.id.switch_activity_id) {

            Intent intent = new Intent(v.getContext(), DeviceActivity.class);
            BluetoothDevice selectedDevice = mDevice;
            intent.putExtra(DeviceActivity.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
            startActivity(intent);



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
