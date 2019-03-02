package com.example.stage_2;

public enum  lModelObject {

    Heartbeat(R.string.heartbeat, R.layout.index_of_heartbeat),
    BodtTemperature(R.string.body_temperature, R.layout.index_of_body_temperature);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
