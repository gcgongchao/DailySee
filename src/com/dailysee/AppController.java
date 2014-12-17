package com.dailysee;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.alexbbb.uploadservice.UploadService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.frontia.FrontiaApplication;
import com.dailysee.bean.Product;
import com.dailysee.util.LruBitmapCache;

/*
 * 如果您的工程中实现了Application的继承类，那么，您需要将父类改为com.baidu.frontia.FrontiaApplication。
 * 如果您没有实现Application的继承类，那么，请在AndroidManifest.xml的Application标签中增加属性： 
 * <application android:name="com.baidu.frontia.FrontiaApplication"
 * 。。。
 */
public class AppController extends FrontiaApplication {

	public static final String TAG = AppController.class.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static AppController mInstance;
	
	private Map<Long, Product> mShoppingCartMap = new HashMap<Long, Product>(); 

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		UploadService.NAMESPACE = getPackageName();
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public void addToShoppingCart(Product product) {
		long productId = product.productId;
		mShoppingCartMap.put(productId, product);
	}
	
	public void removeFromShoppingCart(Product product) {
		if (product == null) {
			return ;
		}
		
		long productId = product.productId;
		int count = product.count;
		
		if (count > 0) {
			mShoppingCartMap.put(productId, product);
		} else {
			mShoppingCartMap.remove(productId);
		}
	}
	
	public int findCountInShoppingCart(long productId) {
		Product product = mShoppingCartMap.get(productId);
		return product != null ? product.count : 0;
	}

	public Product findProductInShoppingCart(long productId) {
		return mShoppingCartMap.get(productId);
	}
	
	public Map<Long, Product> getShoppingCart() {
		return mShoppingCartMap;
	}
	
	public void clearShoppingCart() {
		mShoppingCartMap.clear();
	}
}
