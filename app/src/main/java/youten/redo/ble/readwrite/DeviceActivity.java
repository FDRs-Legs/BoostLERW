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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import youten.redo.ble.util.BleUtil;
import youten.redo.ble.util.BleUuid;

import static youten.redo.ble.readwrite.R.drawable.ic_battery_90_black_48dp;

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
    private Button mReadAllStatesButton;
    private Button mReadBoardNameButton;
    private Button mReadBatteryStateButton;
    private Button mReadLightState;
    private Button mReadBatteryLevelButton;
    private Button mReadOdometerButton;
    private Button mReadRideStateButton;
    private Button mWriteTurtleLevelButton;
    private Button mWriteEcoLevelButton;
    private Button mWriteExpertLevelButton;
    private Button mWriteHyperLevelButton;
    private Button mSwitchActivity;
    private Button mWriteToggleLightButton;
   private SeekBar mWriteLightBrightSeeker;

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override


        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            mSwitchActivity.setEnabled(true);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                runOnUiThread(() -> {
                    mReadAllStatesButton.setEnabled(false);
                    mReadBoardNameButton.setEnabled(false);
                    mWriteTurtleLevelButton.setEnabled(false);
                    mWriteEcoLevelButton.setEnabled(false);
                    mWriteExpertLevelButton.setEnabled(false);
                    mWriteHyperLevelButton.setEnabled(false);
   //                 mReadESCFirmwareButton.setEnabled(false);
   //                 mReadBatteryFirmwareButton.setEnabled(false);
                    mReadLightState.setEnabled(false);
                    mReadBatteryStateButton.setEnabled(false);
//                    mReadBatteryTypeButton.setEnabled(false);
                    mReadBatteryLevelButton.setEnabled(false);
                    mReadOdometerButton.setEnabled(false);
                    mReadRideStateButton.setEnabled(false);
                    mWriteToggleLightButton.setEnabled(false);
                    mWriteLightBrightSeeker.setEnabled(false);


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
                if (BleUuid.SERVICE_DEVICE_INFORMATION3.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadOdometerButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ODOMETER_STRING)));
                    mReadRideStateButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_RIDE_MODE_STRING)));

                    runOnUiThread(() -> {
                        mReadOdometerButton.setEnabled(true);
                        mReadRideStateButton.setEnabled(true);

                    });
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION4.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadBoardNameButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BOARD_NAME_STRING)));

                    runOnUiThread(() -> {
                        mReadBoardNameButton.setEnabled(true);
                        mReadAllStatesButton.setEnabled(true);


                    });
                }

                if (BleUuid.SERVICE_DEVICE_INFORMATION5.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    mReadLightState
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_LIGHT_STATE)));
                    mWriteToggleLightButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_LIGHT_STATE)));
                    mWriteLightBrightSeeker.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_LIGHT_BRIGHT_LEVEL)));

                    runOnUiThread(() -> {
                        mReadLightState.setEnabled(true);
                        mWriteToggleLightButton.setEnabled(true);
                        mWriteLightBrightSeeker.setEnabled(true);


                    });
                }

                if (BleUuid.SERVICE_DEVICE_INFORMATION2.equalsIgnoreCase(service
                        .getUuid().toString())) {

                    mReadBatteryStateButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_STATE_STRING)));
                    mReadBatteryLevelButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_BATTERY_LEVEL_STRING)));
                    runOnUiThread(() -> {

                        mReadBatteryStateButton.setEnabled(true);
                        mReadBatteryLevelButton.setEnabled(true);

                    });
                }

                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    runOnUiThread(() -> {
                        mWriteTurtleLevelButton.setEnabled(true);
                        mWriteEcoLevelButton.setEnabled(true);
                        mWriteExpertLevelButton.setEnabled(true);
                        mWriteHyperLevelButton.setEnabled(true);
                    });
                    mWriteTurtleLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                    mWriteEcoLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                    mWriteExpertLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                    mWriteHyperLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }


            }

            runOnUiThread(() -> setProgressBarIndeterminateVisibility(false));
        }

       //Determines what to do with the data in the characteristic



        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                /*//////////////////////////////////////////////////////////////////////////////////
                //                  Read value export for described characteristic
                //
                *//////////////////////////////////////////////////////////////////////////////
  if (BleUuid.CHAR_BOARD_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(() -> {
                        mReadBoardNameButton.setText(name);
                        setProgressBarIndeterminateVisibility(false);
                    });
                }
                ///////////////////////////////////////////////////////////////////////////////////
                //                  Read and convert battery charging state
                //
                //////////////////////////////////////////////////////////////////////////////

                else if (BleUuid.CHAR_BATTERY_STATE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] temp3 = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    String name = Arrays.toString(temp3);
                    if (name.equalsIgnoreCase("[1]")) {
                        name = "Charging";
                        String finalName = name;
                        runOnUiThread(() -> {
                            mReadBatteryStateButton.setText(finalName);
                            mReadBatteryStateButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_power_black_36dp, 0, 0,0);
                            setProgressBarIndeterminateVisibility(false);
                        });

                    } else {
                        name = "Not Charging";
                        String finalName = name;
                        runOnUiThread(() -> {
                            mReadBatteryStateButton.setText(finalName);
                            mReadBatteryStateButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_skateboard_1, 0, 0,0);
                            setProgressBarIndeterminateVisibility(false);
                        });

                    }

                }
                ///////////////////////////////////////////////////////////////////////////////////
                //                  Beams support testing   status is probably on/off/blinking for
                //                   headlights and tail light
                //////////////////////////////////////////////////////////////////////////////
                else if (BleUuid.CHAR_LIGHT_STATE
                            .equalsIgnoreCase(characteristic.getUuid().toString())) {
                        //final String name = characteristic.getStringValue(0);
                        byte[] temp3 = characteristic.getValue();
                        //final String name = characteristic.getStringValue(0);
                        String name = Arrays.toString(temp3);
                        if (name.equalsIgnoreCase("[0]")) {
                            name = "1. Lights Off";
                        } else  if (name.equalsIgnoreCase("[1]")){
                            name = "2. Lights On";
                        }else  if (name.equalsIgnoreCase("[2]")){
                            name = "3. Lights WTF 1?";
                        }else  if (name.equalsIgnoreCase("[3]")){
                            name = "4. Lights WTF 2?";
                        }else if (name.equalsIgnoreCase("[4]")) {
                            name = "5. Headlights On";
                        }else  if (name.equalsIgnoreCase("[5]")) {
                            name = "6. Headlights Off ";
                        }
                        else  if (name.equalsIgnoreCase("[6]")) {
                            name = "No Beams for you!";
                        }
                        else  {
                            name = "sweet Beams...";
                        }

                        String finalName = name;
                        runOnUiThread(() -> {
                            mReadLightState.setText(finalName);
                            setProgressBarIndeterminateVisibility(false);
                        });
                }
                ///////////////////////////////////////////////////////////////////////////////////
                //                   Battery level outputs 0-100
                //
                //////////////////////////////////////////////////////////////////////////////

                else if (BleUuid.CHAR_BATTERY_LEVEL_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    byte[] batteryLevelByteValue = characteristic.getValue();

                    String batteryLevelStringValue= Arrays.toString(batteryLevelByteValue);

                   int  batteryLevelStringValueLength =  batteryLevelStringValue.length();
                    if (batteryLevelStringValueLength == 5)
                        batteryLevelStringValue =  batteryLevelStringValue.substring(1,4);
                    else
                        batteryLevelStringValue =  batteryLevelStringValue.substring(1,3);

final int  batteryLevelIntValue = Integer.parseInt (batteryLevelStringValue);
Log.d("jkj5kl4j5","this is the value "+ batteryLevelIntValue);
      String finalBatteryLevelStringValue = batteryLevelStringValue;
      runOnUiThread(() -> {
          if(batteryLevelIntValue == 100) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_full_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>90) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_90_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>80) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_80_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>60) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_60_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>50) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_50_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>30) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_30_black_48dp, 0, 0,0);
          }
          else if(batteryLevelIntValue>20) {
              mReadBatteryLevelButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_battery_20_black_48dp, 0, 0,0);

          }
          mReadBatteryLevelButton.setText(finalBatteryLevelStringValue);

          setProgressBarIndeterminateVisibility(false);
      });
                }

                ///////////////////////////////////////////////////////////////////////////////////
                //          read in as 3 byte signed values and converted to a hexstring prior
                //           decimal. Then divide by 4 for a full rotation.
                //////////////////////////////////////////////////////////////////////////////

                else if (BleUuid.CHAR_ODOMETER_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {

//                   characteristic.notify();
                    byte[] formattedRevolutions = characteristic.getValue();

                    //reverse the order of the bytes and null the first after reordering

                    formattedRevolutions[3] = formattedRevolutions[0];
                    formattedRevolutions[0] = formattedRevolutions[1];
                    formattedRevolutions[1] = formattedRevolutions[2];
                    formattedRevolutions[2] = formattedRevolutions[0];
                    formattedRevolutions[0] = 0;   // should you ever need to fill this value, you will have travelled over 3,000,000 kilometers or 1,500,000 miles




                    StringBuilder hexFormattedRevolutions =  new StringBuilder();



                   for( byte b : formattedRevolutions)
                      hexFormattedRevolutions.append(String.format("%02X", b));

                    int odometerDecimalValue=Integer.parseInt(String.valueOf(hexFormattedRevolutions), 16);


                    double eightyMMWheels=(( Math.PI * 80)/1000000) ;
                    double eightyFiveMMWheels = (( Math.PI * 85)/1000000);
                    double ninetyMMWheels = (( Math.PI * 90)/1000000);

                    double odometer80 = ((odometerDecimalValue * eightyMMWheels)/4.32);   //Calculated this ratio backwards from the Ride App
                   double odometer85 = ((odometerDecimalValue * eightyFiveMMWheels)/4.32);
                    double odometer90 = ((odometerDecimalValue * ninetyMMWheels)/4.32);

                    double odometer80inMiles = odometer80 /1.60934;
                    double odometer85inMiles = odometer85 /1.60934;
                    double odometer90inMiles = odometer90 /1.60934;


                 String output80 = String.format("%.2f", odometer80) + " km | " + String.format("%.2f", odometer80inMiles) + " miles ";
               String output85 = String.format("%.2f", odometer85) + " km | " + String.format("%.2f", odometer85inMiles) + " miles ";
                String output90 = String.format("%.2f", odometer90) + " km | " + String.format("%.2f", odometer90inMiles) + " miles ";

                  //  String finalOdometerDecimalValue = odometerDecimalValue;

                  //  long finalOdometerDecimalValue1 = odometerDecimalValue2;
                    final String finalOutput80 = output80;
                    final String finalOutput85 = output85;
                    final String finalOutput90 = output90;
                    runOnUiThread(() -> mReadOdometerButton.setText( finalOutput80 + " @ 80mm 56T\n"+ finalOutput85 +" @ 85mm 56T\n"+ finalOutput90 +" @ 90mm 56T"));
                } else if (BleUuid.CHAR_RIDE_MODE_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    //final String name = characteristic.getStringValue(0);
                    byte[] rideMode = characteristic.getValue();
                    //final String name = characteristic.getStringValue(0);
                    final String name;
                    String name1 = null;
                    if (Arrays.toString(rideMode).equalsIgnoreCase("[3]"))
                    name1 = "Hyper";
                    if (Arrays.toString(rideMode).equalsIgnoreCase("[2]"))
                        name1 = "Expert";
                    if (Arrays.toString(rideMode).equalsIgnoreCase("[1]"))
                        name1 = "Eco";
                    if (Arrays.toString(rideMode).equalsIgnoreCase("[0]"))
                        name1 = "Turtle";


                    name = name1;
                    runOnUiThread(() -> {
                        mReadRideStateButton.setText(name);
                        setProgressBarIndeterminateVisibility(false);
                    });
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
        setContentView(R.layout.activity_device);


        mReadAllStatesButton= findViewById(R.id.update_all_button);
        mReadAllStatesButton.setOnClickListener(this);
        //Activity switcher

        mSwitchActivity = findViewById(R.id.switch_activity_id);
        mSwitchActivity.setOnClickListener(this);
        // state
        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mReadBoardNameButton = findViewById(R.id.read_board_name_button);
        mReadBoardNameButton.setOnClickListener(this);
       mReadLightState = findViewById(R.id.read_light_state_button);
       mReadLightState.setOnClickListener(this);
        mReadBatteryStateButton = findViewById(R.id.read_battery_state_button);
        mReadBatteryStateButton.setOnClickListener(this);
        mReadBatteryLevelButton = findViewById(R.id.read_battery_level_button);
        mReadBatteryLevelButton.setOnClickListener(this);
         mWriteTurtleLevelButton = findViewById(R.id.write_turtle_level_button);
        mWriteTurtleLevelButton.setOnClickListener(this);
        mWriteEcoLevelButton = findViewById(R.id.write_eco_level_button);
        mWriteEcoLevelButton.setOnClickListener(this);
        mWriteExpertLevelButton = findViewById(R.id.write_alert_level_button);
        mWriteExpertLevelButton .setOnClickListener(this);
        mWriteHyperLevelButton = findViewById(R.id.write_hyper_level_button);
        mWriteHyperLevelButton.setOnClickListener(this);
        mReadOdometerButton = findViewById(R.id.read_odometer_button);
        mReadOdometerButton.setOnClickListener(this);
        mReadRideStateButton = findViewById(R.id.read_ride_state_button);
        mReadRideStateButton.setOnClickListener(this);
        mWriteToggleLightButton = findViewById(R.id.write_toggle_light_button);
        mWriteToggleLightButton.setOnClickListener(this);
       mWriteLightBrightSeeker = findViewById(R.id.read_light_bright_state);
       mWriteLightBrightSeeker.setMax(255);


        mWriteLightBrightSeeker .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {


                if ((mWriteLightBrightSeeker.getTag() != null)
                        && (mWriteLightBrightSeeker.getTag() instanceof BluetoothGattCharacteristic)) {
                    BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) mWriteLightBrightSeeker.getTag();


                    StringBuilder hexFormattedState =  new StringBuilder();




                    ByteBuffer b = ByteBuffer.allocate(4);
                    b.putInt(progressChangedValue);
                    ch.setValue(new byte[]{(byte) progressChangedValue});
                    if (mConnGatt.writeCharacteristic(ch)) {
                        setProgressBarIndeterminateVisibility(true);
                    }
                }

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                if ((mWriteLightBrightSeeker.getTag() != null)
                        && (mWriteLightBrightSeeker.getTag() instanceof BluetoothGattCharacteristic)) {
                    BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) mWriteLightBrightSeeker.getTag();

                      ByteBuffer b = ByteBuffer.allocate(4);
                    b.putInt(progressChangedValue);
                    ch.setValue(new byte[]{(byte) progressChangedValue});
                    if (mConnGatt.writeCharacteristic(ch)) {
                        setProgressBarIndeterminateVisibility(true);
                    }
                }

            }
        });


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

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
    if (v.getId() == R.id.read_board_name_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        }else if (v.getId() == R.id.read_battery_state_button) {
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

        }else if (v.getId() == R.id.read_ride_state_button) {
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

        } else if (v.getId() == R.id.read_light_state_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        }else if (v.getId() == R.id.write_toggle_light_button) {

            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {

                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();

//mConnGatt.readCharacteristic(init);
                StringBuilder hexFormattedState = new StringBuilder();


                byte[] checkState = ch.getValue();
                if (checkState != null) {


                    for (byte b : checkState)
                        hexFormattedState.append(String.format("%02X", b));


                }
                else{

                    ((BluetoothGattCharacteristic) mReadLightState.getTag()).getValue();
                }


                switch (String.valueOf(hexFormattedState)) {
                    case "01":

                        ch.setValue(new byte[]{(byte) 0x02});

                        break;
                    case "02":

                        ch.setValue(new byte[]{(byte) 0x03});
                        break;
                    case "03":

                        ch.setValue(new byte[]{(byte) 0x01});
                        break;
                    case "00":

                        ch.getService().getCharacteristic(UUID.fromString(BleUuid.CHAR_LIGHT_STATE));
                        ch.setValue(new byte[]{(byte) 0x01});
                        break;
                }
                    if (mConnGatt.writeCharacteristic(ch)) {
                        setProgressBarIndeterminateVisibility(true);
                    }
                }

        } else if (v.getId() == R.id.write_turtle_level_button) {
        if ((v.getTag() != null)
                && (v.getTag() instanceof BluetoothGattCharacteristic)) {

            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                    .getTag();
            ch.setValue(new byte[]{(byte) 0x00});


            if (mConnGatt.writeCharacteristic(ch)) {




                runOnUiThread(() -> {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mReadRideStateButton.performClick();

                    setProgressBarIndeterminateVisibility(false);
                });


            }



        }
    } else if (v.getId() == R.id.write_eco_level_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {

                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[]{(byte) 0x01});


                if (mConnGatt.writeCharacteristic(ch)) {

                    runOnUiThread(() -> {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mReadRideStateButton.performClick();
                        setProgressBarIndeterminateVisibility(false);
                    });


                }

            }
        } else if (v.getId() == R.id.write_alert_level_button) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {

                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[]{(byte) 0x02});

                if (mConnGatt.writeCharacteristic(ch)) {

                    runOnUiThread(() -> {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mReadRideStateButton.performClick();


                        setProgressBarIndeterminateVisibility(false);
                    });


                }

            }
        }
    else if (v.getId() == R.id.write_hyper_level_button) {
        if ((v.getTag() != null)
                && (v.getTag() instanceof BluetoothGattCharacteristic)) {

            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                    .getTag();
            ch.setValue(new byte[]{(byte) 0x03});

            if (mConnGatt.writeCharacteristic(ch)) {
                runOnUiThread(() -> {
                    mReadRideStateButton.performClick();
                    setProgressBarIndeterminateVisibility(false);
                });

            }

        }
    }else if (v.getId() == R.id.switch_activity_id) {

        Intent intent = new Intent(v.getContext(), DeviceActivityTesting.class);
        BluetoothDevice selectedDevice = mDevice;
        intent.putExtra(DeviceActivityTesting.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
        startActivity(intent);

    }else if (v.getId() == R.id.update_all_button) {

            runOnUiThread(() -> {


                try {
                    mReadBatteryStateButton.performClick();
                    Thread.sleep(500);
                    mReadBatteryLevelButton.performClick();
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mReadLightState.performClick();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mReadOdometerButton.performClick();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mReadRideStateButton.performClick();
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mReadBoardNameButton.performClick();
            });



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
        mWriteExpertLevelButton.setEnabled(false);
        mWriteEcoLevelButton.setEnabled(false);
        mWriteTurtleLevelButton.setEnabled(false);
        mWriteHyperLevelButton.setEnabled(false);
         mReadBatteryStateButton.setEnabled(false);

        mReadLightState.setEnabled(false);
        mReadBatteryLevelButton.setEnabled(false);
        mReadRideStateButton.setEnabled(false);
        mReadOdometerButton.setEnabled(false);
        mWriteToggleLightButton.setEnabled(false);
        mReadAllStatesButton.setEnabled(false);

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
