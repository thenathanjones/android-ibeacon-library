package com.thenathanjones.ibeacon;

import java.util.Arrays;
import java.util.List;

/**
 * Created by thenathanjones on 31/01/2014.
 */
public class IBeaconConstants {
    protected static final List<Integer> IBEACON_HEADER = Arrays.asList(0x4c, 0x00, 0x02, 0x15);
    protected static final int IBEACON_HEADER_INDEX = 5;
    protected static final int MAJOR_INDEX = 25;
    protected static final int PROXIMITY_UUID_INDEX = 9;
    protected static final int MINOR_INDEX = 27;
    protected static final int TXPOWER_INDEX = 29;

    protected static final double FILTER_FACTOR = 0.1;

    protected static final char[] HEX_ARRAY = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    protected static final int UPDATE_PERIOD = 1000;
    protected static final int CULL_DELAY = 3000;
}
