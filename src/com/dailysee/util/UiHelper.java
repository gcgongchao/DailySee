package com.dailysee.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.dailysee.R;
import com.dailysee.bean.Image;
import com.dailysee.ui.image.BrowseImageActivity;

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
    

    
    public static void toBrowseImage(Context context, String imageUrl) {
    	if (!TextUtils.isEmpty(imageUrl)) {
	    	ArrayList<String> images = new ArrayList<String>();
			images.add(imageUrl);
			
			toBrowseImage(context, images, 0);
    	}
	}
	
	public static void toBrowseImageList(Context context, List<Image> imgs, int position) {
		if (imgs != null && imgs.size() > 0) {
			ArrayList<String> images = new ArrayList<String>();
			for (Image image : imgs) {
				if (image != null) {
					images.add(image.url);
				}
			}
			toBrowseImage(context, images, position);
		}
	}

	public static void toBrowseImage(Context context, ArrayList<String> images, int position) {
		Intent intent = new Intent();
		intent.setClass(context, BrowseImageActivity.class);
		intent.putStringArrayListExtra(BrowseImageActivity.EXTRA_IMAGW_ARRAY, images);
		intent.putExtra(BrowseImageActivity.EXTRA_IMAGW_INDEX, position);
		context.startActivity(intent);
	}

}
