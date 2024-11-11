package co.aospa.sense.vendor.impl;

import android.content.Context;
import android.util.Log;

import co.aospa.sense.util.PreferenceHelper;
import co.aospa.sense.vendor.Vendor;
import co.aospa.sense.vendor.util.ConUtil;
import co.aospa.sense.vendor.util.VendorUnlockEncryptor;

import java.io.File;

/* loaded from: vendorImplPrebuilt.jar:co/aospa/sense/vendor/impl/FacePPImpl.class */
public class FacePPImpl extends Vendor {
    private static final String TAG = FacePPImpl.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String SDK_VERSION = "1";
    private final Context mContext;
    private SERVICE_STATE mCurrentState = SERVICE_STATE.INITING;
    private final PreferenceHelper mPreferenceHelper;

    /* loaded from: vendorImplPrebuilt.jar:co/aospa/sense/vendor/impl/FacePPImpl$SERVICE_STATE.class */
    public enum SERVICE_STATE {
        INITING,
        IDLE,
        ENROLLING,
        UNLOCKING,
        ERROR
    }

    public FacePPImpl(Context context) {
        this.mContext = context;
        this.mPreferenceHelper = new PreferenceHelper(context);
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void init() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.INITING) {
                Log.d(TAG, " Has been init, ignore");
                return;
            }
            String str = TAG;
            Log.i(str, "init start");
            boolean z =
                    !SDK_VERSION.equals(this.mPreferenceHelper.getStringValueByKey("sdk_version"));
            File dir = this.mContext.getDir("faceunlock_data", 0);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String raw = ConUtil.getRaw(this.mContext, "model_file", "model", "model_file", z);
            if (raw == null) {
                Log.e(str, "Unavalibale memory, init failed, stop self");
                return;
            }
            String raw2 = ConUtil.getRaw(this.mContext, "panorama_mgb", "model", "panorama_mgb", z);
            MegviiFaceUnlockImpl.getInstance()
                    .initHandle(dir.getAbsolutePath(), new VendorUnlockEncryptor());
            Log.i(str, "init stop");
            if (MegviiFaceUnlockImpl.getInstance().initAllWithPath(raw2, "", raw) != 0) {
                Log.e(str, "init failed, stop self");
                return;
            }
            if (z) {
                restoreFeature();
                this.mPreferenceHelper.saveStringValue("sdk_version", SDK_VERSION);
            }
            this.mCurrentState = SERVICE_STATE.IDLE;
        }
    }

    private void restoreFeature() {
        Log.i(TAG, "RestoreFeature");
        synchronized (this) {
            MegviiFaceUnlockImpl.getInstance().prepare();
            MegviiFaceUnlockImpl.getInstance().restoreFeature();
            MegviiFaceUnlockImpl.getInstance().reset();
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void compareStart() {
        synchronized (this) {
            if (this.mCurrentState == SERVICE_STATE.INITING) {
                init();
            }
            if (this.mCurrentState == SERVICE_STATE.UNLOCKING) {
                return;
            }
            if (this.mCurrentState != SERVICE_STATE.IDLE) {
                Log.e(TAG, "unlock start failed: current state: " + this.mCurrentState);
                return;
            }
            Log.i(TAG, "compareStart");
            MegviiFaceUnlockImpl.getInstance().prepare();
            this.mCurrentState = SERVICE_STATE.UNLOCKING;
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public int compare(byte[] bArr, int i, int i2, int i3, boolean z, boolean z2, int[] iArr) {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.UNLOCKING) {
                Log.e(TAG, "compare failed: current state: " + this.mCurrentState);
                return -1;
            }
            int compare = MegviiFaceUnlockImpl.getInstance().compare(bArr, i, i2, i3, z, z2, iArr);
            Log.i(TAG, "compare finish: " + compare);
            if (compare == 0) {
                compareStop();
            }
            return compare;
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void compareStop() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.UNLOCKING) {
                Log.e(TAG, "compareStop failed: current state: " + this.mCurrentState);
                return;
            }
            Log.i(TAG, "compareStop");
            MegviiFaceUnlockImpl.getInstance().reset();
            this.mCurrentState = SERVICE_STATE.IDLE;
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void saveFeatureStart() {
        synchronized (this) {
            if (this.mCurrentState == SERVICE_STATE.INITING) {
                init();
            } else if (this.mCurrentState == SERVICE_STATE.UNLOCKING) {
                Log.e(TAG, "save feature, stop unlock");
                compareStop();
            }
            if (this.mCurrentState != SERVICE_STATE.IDLE) {
                Log.e(TAG, "saveFeatureStart failed: current state: " + this.mCurrentState);
            }
            Log.i(TAG, "saveFeatureStart");
            MegviiFaceUnlockImpl.getInstance().prepare();
            this.mCurrentState = SERVICE_STATE.ENROLLING;
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public int saveFeature(
            byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int[] iArr) {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.ENROLLING) {
                Log.e(TAG, "save feature failed , current state : " + this.mCurrentState);
                return -1;
            }
            Log.i(TAG, "saveFeature");
            return MegviiFaceUnlockImpl.getInstance()
                    .saveFeature(bArr, i, i2, i3, z, bArr2, bArr3, iArr);
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void saveFeatureStop() {
        synchronized (this) {
            if (this.mCurrentState != SERVICE_STATE.ENROLLING) {
                Log.d(TAG, "saveFeatureStop failed: current state: " + this.mCurrentState);
            }
            Log.i(TAG, "saveFeatureStop");
            MegviiFaceUnlockImpl.getInstance().reset();
            this.mCurrentState = SERVICE_STATE.IDLE;
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void setDetectArea(int i, int i2, int i3, int i4) {
        synchronized (this) {
            Log.i(TAG, "setDetectArea start");
            MegviiFaceUnlockImpl.getInstance().setDetectArea(i, i2, i3, i4);
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void deleteFeature(int i) {
        synchronized (this) {
            String str = TAG;
            Log.i(str, "deleteFeature start");
            MegviiFaceUnlockImpl.getInstance().deleteFeature(i);
            Log.i(str, "deleteFeature stop");
            release();
        }
    }

    @Override // co.aospa.sense.vendor.Vendor
    public int getFeatureCount() {
        return 0;
    }

    @Override // co.aospa.sense.vendor.Vendor
    public void release() {
        synchronized (this) {
            if (this.mCurrentState == SERVICE_STATE.INITING) {
                Log.i(TAG, "has been released, ignore");
                return;
            }
            String str = TAG;
            Log.i(str, "release start");
            MegviiFaceUnlockImpl.getInstance().release();
            this.mCurrentState = SERVICE_STATE.INITING;
            Log.i(str, "release stop");
        }
    }
}
