package com.pires.eggandbeacon.util;

import android.util.Log;

import com.pires.eggandbeacon.model.Ibeacon;

/**
 * Created by moled on 13.05.2017.
 */

public class UuidConverter {


public Ibeacon createIbeaconFromRecord(final byte[] scanRecord){

    int startByte = 2;
    boolean patternFound = false;
    while (startByte <= 5)
    {
        if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
        { //Identifies correct data length
            patternFound = true;
            break;
        }
        startByte++;
    }

    if (patternFound)
    {
        //Convert to hex String
        byte[] uuidBytes = new byte[16];
        System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
        String hexString = bytesToHex(uuidBytes);

        //UUID detection
        String uuid =  hexString.substring(0,8) + "-" +
                hexString.substring(8,12) + "-" +
                hexString.substring(12,16) + "-" +
                hexString.substring(16,20) + "-" +
                hexString.substring(20,32);

        // major
        final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

        // minor
        final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

        return new Ibeacon(uuid, major, minor);

    } else {

        //Returns a ghost beacon for avoiding NPE
        return null;
    }

}



    /**
     * bytesToHex method
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
