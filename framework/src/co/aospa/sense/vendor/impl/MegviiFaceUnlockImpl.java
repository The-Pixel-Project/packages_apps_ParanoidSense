package co.aospa.sense.vendor.impl;

import com.megvii.facepp.sdk.Lite;

/* loaded from: vendorImplPrebuilt.jar:co/aospa/sense/vendor/impl/MegviiFaceUnlockImpl.class */
public class MegviiFaceUnlockImpl extends Lite {
    private static MegviiFaceUnlockImpl sInstance;

    private MegviiFaceUnlockImpl() {}

    public static MegviiFaceUnlockImpl getInstance() {
        if (sInstance == null) {
            sInstance = new MegviiFaceUnlockImpl();
        }
        return sInstance;
    }
}
