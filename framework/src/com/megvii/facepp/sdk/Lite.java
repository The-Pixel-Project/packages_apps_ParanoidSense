package com.megvii.facepp.sdk;

import android.media.Image;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import co.aospa.sense.vendor.util.UnlockEncryptor;

import com.megvii.facepp.sdk.jni.LiteApi;

import java.nio.ByteBuffer;

public class Lite {
    public static final int FEATURE_SIZE = 10000;
    public static final int IMAGE_SIZE = 40000;
    public static final int RESULT_SIZE = 20;
    private static Lite sInstance;
    private long handle = 0;
    private final FeatureRestoreHelper mFeatureRestoreHelper = new FeatureRestoreHelper();
    private String mPath;

    public enum MGULKPowerMode {
        MG_UNLOCK_POWER_NONE,
        MG_UNLOCK_POWER_LOW,
        MG_UNLOCK_POWER_HIGH
    }

    public int setConfig(float f, float f2, float f3) {
        return 0;
    }

    public static Lite getInstance() {
        if (sInstance == null) {
            sInstance = new Lite();
        }
        return sInstance;
    }

    public void initHandle(String str, UnlockEncryptor unlockEncryptor) {
        initHandle(str);
        this.mFeatureRestoreHelper.setUnlockEncryptor(unlockEncryptor);
    }

    public void initHandle(String str) {
        if (this.handle == 0) {
            this.handle = LiteApi.nativeInitHandle(str);
            this.mPath = str;
        }
    }

    public int initAll(String str, String str2, byte[] bArr) {
        return (int) LiteApi.nativeInitAll(this.handle, str, str2, bArr);
    }

    public int initAllWithPath(String str, String str2, String str3) {
        return (int) LiteApi.nativeInitAllWithPath(this.handle, str, str2, str3);
    }

    public int initLive(String str, String str2) {
        return (int) LiteApi.nativeInitLive(this.handle, str, str2);
    }

    public int initDetect(byte[] bArr) {
        return (int) LiteApi.nativeInitDetect(this.handle, bArr);
    }

    public int initDetectWithPath(String str) {
        return (int) LiteApi.nativeInitDetectWithPath(this.handle, str);
    }

    public int releaseLive() {
        return (int) LiteApi.nativeReleaseLive(this.handle);
    }

    public int releaseDetect() {
        return (int) LiteApi.nativeReleaseDetect(this.handle);
    }

    public void release() {
        LiteApi.nativeRelease(this.handle);
        this.handle = 0L;
    }

    public int compare(byte[] bArr, int i, int i2, int i3, boolean z, boolean z2, int[] iArr) {
        if (iArr.length < 20) {
            return 1;
        }
        return LiteApi.nativeCompare(this.handle, bArr, i, i2, i3, z, z2, iArr);
    }

    public int compare(byte[] bArr, int i, int i2, int i3, int[] iArr) {
        if (iArr.length < 20) {
            return 1;
        }
        return LiteApi.nativeCompare(this.handle, bArr, i, i2, i3, false, false, iArr);
    }

    public int compareMultiImages(MGULKImage[] mGULKImageArr, int[] iArr) {
        if (iArr.length < 20) {
            return 1;
        }
        return LiteApi.nativeCompareMultiImages(this.handle, mGULKImageArr, iArr);
    }

    public int saveFeature(
            byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3) {
        return updateFeature(bArr, i, i2, i3, z, bArr2, bArr3, 0);
    }

    public int saveFeature(byte[] bArr, int i, int i2, int i3, byte[] bArr2, byte[] bArr3) {
        return updateFeature(bArr, i, i2, i3, true, bArr2, bArr3, 0);
    }

    public int saveFeature(
            byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int[] iArr) {
        if (new StatFs(Environment.getDataDirectory().getPath()).getAvailableBlocksLong() < 256) {
            return 33;
        }
        if (bArr3.length < 40000 || bArr2.length < 10000) {
            return 1;
        }
        int nativeSaveFeature =
                LiteApi.nativeSaveFeature(
                        this.handle, bArr, i, i2, i3, z ? 1 : 0, bArr2, bArr3, iArr);
        if (nativeSaveFeature == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr3, this.mPath, iArr[0]);
        }
        return nativeSaveFeature;
    }

    public int saveFeature(
            byte[] bArr, int i, int i2, int i3, byte[] bArr2, byte[] bArr3, int[] iArr) {
        if (bArr3.length < 40000 || bArr2.length < 10000) {
            return 1;
        }
        int nativeSaveFeature =
                LiteApi.nativeSaveFeature(this.handle, bArr, i, i2, i3, 1, bArr2, bArr3, iArr);
        if (nativeSaveFeature == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr3, this.mPath, iArr[0]);
        }
        return nativeSaveFeature;
    }

    public int saveFeatureMultiImages(
            MGULKImage[] mGULKImageArr, byte[] bArr, byte[] bArr2, int[] iArr) {
        int nativeSaveFeatureMultiImages =
                LiteApi.nativeSaveFeatureMultiImages(this.handle, mGULKImageArr, bArr, bArr2, iArr);
        if (nativeSaveFeatureMultiImages == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr2, this.mPath, iArr[0]);
        }
        return nativeSaveFeatureMultiImages;
    }

    public int updateFeature(
            byte[] bArr, int i, int i2, int i3, boolean z, byte[] bArr2, byte[] bArr3, int i4) {
        if (bArr3.length < 40000 || bArr2.length < 10000) {
            return 1;
        }
        int nativeUpdateFeature =
                LiteApi.nativeUpdateFeature(
                        this.handle, bArr, i, i2, i3, z ? 1 : 0, bArr2, bArr3, i4);
        if (nativeUpdateFeature == 0) {
            this.mFeatureRestoreHelper.saveRestoreImage(bArr3, this.mPath, i4);
        }
        return nativeUpdateFeature;
    }

    public int deleteFeature() {
        return deleteFeature(0);
    }

    public int deleteFeature(int i) {
        int nativeDeleteFeature = LiteApi.nativeDeleteFeature(this.handle, i);
        this.mFeatureRestoreHelper.deleteRestoreImage(this.mPath, i);
        return nativeDeleteFeature;
    }

    public int restoreFeature() {
        return this.mFeatureRestoreHelper.restoreAllFeature(this.mPath);
    }

    public int setConfig(float f, float f2, float f3, float f4, boolean z, boolean z2) {
        return LiteApi.nativeSetConfig(this.handle, f, f2, f3, f4, z, z2);
    }

    public int setConfig(float f, float f2, float f3, float f4) {
        return LiteApi.nativeSetConfig(this.handle, f, f2, f3, f4, false, false);
    }

    public int setConfig(LiteConfig liteConfig) {
        if (liteConfig == null) {
            return -1;
        }
        return LiteApi.nativeSetConfigV2(this.handle, liteConfig);
    }

    public int reset() {
        return LiteApi.nativeReset(this.handle);
    }

    public int prepare(MGULKPowerMode mGULKPowerMode) {
        int ordinal = mGULKPowerMode.ordinal();
        int i = 2;
        if (ordinal != 1) {
            if (ordinal == 2) {
                i = 1;
            }
            return LiteApi.nativePrepareWithPower(this.handle, i);
        }
        return LiteApi.nativePrepareWithPower(this.handle, 0);
    }

    public int prepare() {
        MGULKPowerMode.MG_UNLOCK_POWER_HIGH.ordinal();
        return LiteApi.nativePrepare(this.handle);
    }

    public int setDetectArea(int i, int i2, int i3, int i4) {
        return LiteApi.nativeSetDetectArea(this.handle, i, i2, i3, i4);
    }

    public String getVersion() {
        return LiteApi.nativeGetVersion(this.handle);
    }

    public int getFeature(byte[] bArr, int i, int i2, int i3, byte[] bArr2) {
        if (bArr2.length < 10000) {
            return 1;
        }
        return LiteApi.nativeGetFeature(this.handle, bArr, i, i2, i3, bArr2);
    }

    public int compareFeatures(byte[] bArr, float[] fArr, int i, boolean z) {
        return LiteApi.nativeCompareFeatures(this.handle, bArr, fArr, i, z);
    }

    public int checkFeatureValid(int i) {
        return LiteApi.nativeCheckFeatureValid(this.handle, i);
    }

    public int getFeatureCount() {
        return LiteApi.nativeGetFeatureCount();
    }

    public long setLogLevel(int i) {
        return LiteApi.nativeSetLogLevel(i);
    }

    public LiteConfig getConfig() {
        LiteConfig liteConfig = new LiteConfig(this, this, this, null);
        LiteApi.nativeGetConfig(this.handle, liteConfig);
        return liteConfig;
    }

    public static int image2NV21(Image image, byte[] bArr) {
        int readImageIntoBuffer = readImageIntoBuffer(image, bArr);
        if (readImageIntoBuffer == 1) {
            return 1;
        }
        revertHalf(bArr);
        return readImageIntoBuffer;
    }

    private static int readImageIntoBuffer(Image image, byte[] bArr) {
        int i;
        int i2;
        if (image == null) {
            Log.e("NULL Image", "image is null");
            return 1;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        int i3 = 0;
        for (int i4 = 0; i4 < planes.length; i4++) {
            ByteBuffer buffer = planes[i4].getBuffer();
            int rowStride = planes[i4].getRowStride();
            int pixelStride = planes[i4].getPixelStride();
            if (i4 == 0) {
                i = width;
            } else {
                i = width / 2;
            }
            if (i4 == 0) {
                i2 = height;
            } else {
                i2 = height / 2;
            }
            if (pixelStride == 1 && rowStride == i) {
                int i5 = i * i2;
                buffer.get(bArr, i3, i5);
                i3 += i5;
            } else {
                byte[] bArr2 = new byte[rowStride];
                for (int i6 = 0; i6 < i2 - 1; i6++) {
                    buffer.get(bArr2, 0, rowStride);
                    int i7 = 0;
                    while (i7 < i) {
                        bArr[i3] = bArr2[i7 * pixelStride];
                        i7++;
                        i3++;
                    }
                }
                buffer.get(bArr2, 0, Math.min(rowStride, buffer.remaining()));
                int i8 = 0;
                while (i8 < i) {
                    bArr[i3] = bArr2[i8 * pixelStride];
                    i8++;
                    i3++;
                }
            }
        }
        return 0;
    }

    private static void revertHalf(byte[] bArr) {
        int length = bArr.length;
        int i = length / 3;
        byte[] bArr2 = new byte[i];
        int i2 = length / 6;
        int i3 = i2 * 4;
        int i4 = i2 * 5;
        int i5 = 0;
        while (i5 < i - 1) {
            bArr2[i5] = bArr[i4];
            bArr2[i5 + 1] = bArr[i3];
            i5 += 2;
            i4++;
            i3++;
        }
        int i6 = i * 2;
        int i7 = length - i6;
        if (i7 >= 0) {
            System.arraycopy(bArr2, 0, bArr, i6, i7);
        }
    }

    public static class MGULKImage {
        public static int MG_UNLOCK_IMG_2PD = 1;
        public static int MG_UNLOCK_IMG_BGR = 2;
        public static int MG_UNLOCK_IMG_DEPTH = 5;
        public static int MG_UNLOCK_IMG_IR = 3;
        public static int MG_UNLOCK_IMG_IR_PATTERN = 4;
        public static int MG_UNLOCK_IMG_NV21;
        int angle;
        int height;
        byte[] imageData;
        int imageSize;
        int imageType;
        int width;

        public MGULKImage(int i, byte[] bArr, int i2, int i3, int i4, int i5) {
            this.imageType = i;
            this.imageData = bArr;
            this.imageSize = i2;
            this.width = i3;
            this.height = i4;
            this.angle = i5;
        }
    }

    public class LiteConfig {
        public static final int MG_UNLOCK_BIG_CPU_CORE_HIGH = 4;
        public static final int MG_UNLOCK_BIG_CPU_CORE_LOW = 0;
        public static final int MG_UNLOCK_COMPARE_ALL = 0;
        public static final int MG_UNLOCK_COMPARE_LIVE = 1;
        public static final int MG_UNLOCK_COMP_DEVICE_CPU = 1;
        public static final int MG_UNLOCK_COMP_DEVICE_NONE = 0;
        public static final int MG_UNLOCK_COMP_DEVICE_OPENCL = 3;
        public static final int MG_UNLOCK_COMP_DEVICE_SNPE = 2;
        public static final int MG_UNLOCK_EXTRACT_APU = 4;
        public static final int MG_UNLOCK_EXTRACT_DOUBLE_CORE_NORMAL = 1;
        public static final int MG_UNLOCK_EXTRACT_DSP = 3;
        public static final int MG_UNLOCK_EXTRACT_OPENCL = 2;
        public static final int MG_UNLOCK_EXTRACT_SINALE_CORE_NORMAL = 0;
        public static final int MG_UNLOCK_STORE_DEBUG_IMAGE_NONE = 0;
        public static final int MG_UNLOCK_STORE_DEBUG_IMAGE_NV21 = 1;
        public static final int MG_UNLOCK_STORE_DEBUG_IMAGE_NV21_LANDMARK = 2;
        public float ComparePitchDownThreshold;
        public float ComparePitchTopThreshold;
        public float CompareYawLeftThreshold;
        public float CompareYawRightThreshold;
        public int bigCpuCore;
        public boolean blurness;
        public int compDeviceType;
        public boolean compareBlurness;
        public int compareType;
        public int extractConfig;
        public boolean eyeOcclusion;
        public boolean eyeStatus;
        public boolean faceIntact;
        public boolean light;
        public boolean mouthOcclusion;
        public String nativeLibraryPath;
        public String openclCachePath;
        public float pitchDownThreshold;
        public float pitchTopThreshold;
        public int rectBottom;
        public int rectLeft;
        public int rectRight;
        public int rectTop;
        public String saveImagePath;
        public String snpeCachePath;
        public int storeDebugImgMode;
        public boolean useModelToCheck3dPose;
        public float yawLeftThreshold;
        public float yawRightThreshold;

        LiteConfig(Lite lite, Lite lite2, Lite lite3, MGULKPowerMode mGULKPowerMode) {
            this();
        }

        private LiteConfig() {}

        public String toString() {
            return "LiteConfig{compDeviceType="
                    + this.compDeviceType
                    + ", bigCpuCore="
                    + this.bigCpuCore
                    + ", useModelToCheck3dPose="
                    + this.useModelToCheck3dPose
                    + ", eyeOcclusion="
                    + this.eyeOcclusion
                    + ", mouthOcclusion="
                    + this.mouthOcclusion
                    + ", eyeStatus="
                    + this.eyeStatus
                    + ", light="
                    + this.light
                    + ", blurness="
                    + this.blurness
                    + ", compareBlurness="
                    + this.compareBlurness
                    + ", faceIntact="
                    + this.faceIntact
                    + ", yawLeftThreshold="
                    + this.yawLeftThreshold
                    + ", yawRightThreshold="
                    + this.yawRightThreshold
                    + ", pitchTopThreshold="
                    + this.pitchTopThreshold
                    + ", pitchDownThreshold="
                    + this.pitchDownThreshold
                    + ", CompareYawLeftThreshold="
                    + this.CompareYawLeftThreshold
                    + ", CompareYawRightThreshold="
                    + this.CompareYawRightThreshold
                    + ", ComparePitchTopThreshold="
                    + this.ComparePitchTopThreshold
                    + ", ComparePitchDownThreshold="
                    + this.ComparePitchDownThreshold
                    + ", rectLeft="
                    + this.rectLeft
                    + ", rectTop="
                    + this.rectTop
                    + ", rectRight="
                    + this.rectRight
                    + ", rectBottom="
                    + this.rectBottom
                    + ", storeDebugImgMode="
                    + this.storeDebugImgMode
                    + ", saveImagePath='"
                    + this.saveImagePath
                    + "', compareType="
                    + this.compareType
                    + ", extractConfig="
                    + this.extractConfig
                    + ", nativeLibraryPath='"
                    + this.nativeLibraryPath
                    + "', openclCachePath='"
                    + this.openclCachePath
                    + "', snpeCachePath='"
                    + this.snpeCachePath
                    + "'}";
        }
    }
}
