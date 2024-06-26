package com.dosse.airpods.pods.models;

import com.dosse.airpods.pods.Pod;

public class AirPodsPro2 extends AirPodsPro {
    public AirPodsPro2(Pod leftPod, Pod rightPod, Pod casePod) {
        super(leftPod, rightPod, casePod);
    }

    @Override
    public String getModel() {
        return Constants.MODEL_AIRPODS_PRO_2;
    }
}