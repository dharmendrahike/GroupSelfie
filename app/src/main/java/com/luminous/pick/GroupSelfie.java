package com.luminous.pick;

/**
 * Created by dharmendraverma on 25/03/18.
 */

@SuppressWarnings("JniMissingFunction")
public class GroupSelfie {
    public static boolean isLoaded;

    static {
        try {
            System.loadLibrary("OpenCv");
            System.loadLibrary("GroupSelfie");
            /*System.load("/Users/dharmendraverma/StudioProjects/MultipleImagePick/app/libs/libOpenCv.so");
            System.load("/Users/dharmendraverma/StudioProjects/MultipleImagePick/app/libs/libGroupSelfie.so");*/
            isLoaded = true;
            android.util.Log.d("GroupSelfie", "success");
        } catch (Throwable ex) {
            isLoaded = false;
            android.util.Log.d("GroupSelfie", "Failed to load libGroupSelfie", ex);
            ex.printStackTrace();
        }
    }

    public GroupSelfie() {

    }

    public native boolean groupSelfie(byte[] leftInputBuffer, byte[] centerInputBuffer, byte[] rightInputBuffer, int imageWidth, int imageHeight);

   // public native byte[] getoutPutBufferData();
}
