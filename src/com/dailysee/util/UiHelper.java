package com.dailysee.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.dailysee.R;

public class UiHelper {
	
    public static final int REQUEST_PICK = 9162;
    public static final int REQUEST_TAKE = 9163;

	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	
    /**
     * Utility method that starts an image picker since that often precedes a crop
     *
     * @param activity Activity that will receive result
     */
    public static void pickImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            activity.startActivityForResult(intent, REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    public static void takeImage(Activity activity, Uri photoUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        try {
            activity.startActivityForResult(intent, REQUEST_TAKE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    public static boolean isSoldOut(String status) {
    	return !"ENABLE".equalsIgnoreCase(status);
    }

}
