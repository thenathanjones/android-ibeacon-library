package com.thenathanjones.ibeacon;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thenathanjones on 16/02/2014.
 */
public class KalmanFilter {
    public static double KALMAN_VALUE = 2D;
    private static final Map<String, Double> mPredictedSignals = new HashMap<String, Double>();
    private static final Map<String, Double> mPredictedVelocities = new HashMap<String, Double>();

    public static double filter(double newSignalValue, String identifier) {
        Double predictedSignal = mPredictedSignals.get(identifier);
        if (predictedSignal == null) {
            predictedSignal = -70D;
        }

        Double predictedVelocity = mPredictedVelocities.get(identifier);
        if (predictedVelocity == null) {
            predictedVelocity = 1D;
        }

        double k = predictedVelocity / (predictedVelocity + KALMAN_VALUE);

        double measuredSignal = predictedSignal + k * (newSignalValue - predictedSignal);
        mPredictedSignals.put(identifier, measuredSignal);

        double measuredVelocity = (1 - k) * predictedVelocity;
        mPredictedVelocities.put(identifier, measuredVelocity);

        return measuredSignal;
    }
}
