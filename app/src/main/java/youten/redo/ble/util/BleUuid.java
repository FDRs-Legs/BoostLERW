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
package youten.redo.ble.util;

/**
 * BLE UUID Strings
 */
public class BleUuid {
    // 180A Device Information
    public static final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_DEVICE_INFORMATION2 = "65a8eaa8-c61f-11e5-9912-ba0be0483c18";
    public static final String SERVICE_DEVICE_INFORMATION3 = "7dc55a86-c61f-11e5-9912-ba0be0483c18";
    public static final String SERVICE_IMMEDIATE_ALERT = "7dc55a86-c61f-11e5-9912-ba0be0483c18";  //redundant but need to refactor
    public static final String SERVICE_DEVICE_INFORMATION4 = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_DEVICE_INFORMATION5 = "EA32B817-D410-42E2-848A-1218201468FC";


    public static final String CHAR_BOARD_NAME_STRING = "00002a00-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_SERIAL_NUMBEAR_STRING = "00002a25-0000-1000-8000-00805f9b34fb";


    //ESC Info
    public static final String CHAR_FIRMWARE_ESC_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    //BATTERY
    public static final String CHAR_BATTERY_TYPE_STRING = "65a8f831-c61f-11e5-9912-ba0be0483c18";
    public static final String CHAR_BATTERY_LEVEL_STRING = "65a8eeae-c61f-11e5-9912-ba0be0483c18";
    public static final String CHAR_BATTERY_STATE_STRING = "65a8f5d4-c61f-11e5-9912-ba0be0483c18";
    public static final String CHAR_BATTERY_FIRMWARE_STRING = "65a8f833-c61f-11e5-9912-ba0be0483c18";
//	public static final String CHAR_BATTERY_LEVEL_STRING = "7dc55f22-c61f-11e5-9912-ba0be0483c18";
//	public static final String CHAR_BATTERY_STATE_STRING = "7dc55f22-c61f-11e5-9912-ba0be0483c18";
//	public static final String CHAR_BATTERY_TYPE_STRING = "65a8f831-c61f-11e5-9912-ba0be0483c18";
//	public static final String CHAR_BATTERY_FIRMWARE_STRING = "65a8eaa8-c61f-11e5-9912-ba0be0483c18";


    //Odometer
    public static final String CHAR_ODOMETER_STRING = "7dc56594-c61f-11e5-9912-ba0be0483c18";
    //Ride Mode
    public static final String CHAR_RIDE_MODE_STRING = "7dc55f22-c61f-11e5-9912-ba0be0483c18";

    // Write Ride Mode

    public static final String CHAR_ALERT_LEVEL = "7dc55f22-c61f-11e5-9912-ba0be0483c18";
    // StickNFindではCHAR_ALERT_LEVELに0x01をWriteすると光り、0x02では音が鳴り、0x03では光って鳴る。

    // Read/Write Beams
    public static final String CHAR_LIGHT_STATE= "EA32DCAC-D410-42E2-848A-1218201468FC";
    public static final String CHAR_BATTERY_LEVEL = "EA32B761-D410-42E2-848A-1218201468FC";
    public static final String BLEUUID ="";
}
