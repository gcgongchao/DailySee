package com.dailysee.ui.base;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.alexbbb.uploadservice.ContentType;
import com.alexbbb.uploadservice.UploadRequest;
import com.alexbbb.uploadservice.UploadService;
import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Member;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectPicDialog;

public class EditProfileActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {

	protected static final String TAG = EditProfileActivity.class.getSimpleName();
	
	private LinearLayout llAvatar;
	private ImageView ivAvatar;
	
	private EditText etName;
	
	private TextView tvSexBoy;
	private TextView tvSexGirl;

	private LinearLayout llBirthday;
	private TextView tvBirthday;
	
	private EditText etEmail;
	
	private Button btnCommit;

	private SelectPicDialog mSelectPicDialog;
	protected Uri takeImageUri;
	private String mAvatarUploadUrl;

	private String sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		initDialog();

		// Set your application namespace to avoid conflicts with other apps using this library
		UploadService.NAMESPACE = "com.dailysee";
		uploadReceiver.register(this);
	}

	@Override
	public void onInit() {
		setTitle("完善资料");
		setUp();
	}

	@Override
	public void onFindViews() {
		llAvatar = (LinearLayout) findViewById(R.id.ll_avatar);
		ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
		
		etName = (EditText) findViewById(R.id.et_name);
		
		tvSexBoy = (TextView) findViewById(R.id.tv_sex_boy);
		tvSexGirl = (TextView) findViewById(R.id.tv_sex_girl);
		
		llBirthday = (LinearLayout) findViewById(R.id.ll_birthday);
		tvBirthday = (TextView) findViewById(R.id.tv_birthday);

		etEmail = (EditText) findViewById(R.id.et_email);
		
		btnCommit = (Button) findViewById(R.id.btn_commit);
	}

	@Override
	public void onInitViewData() {
		Member member = mSpUtil.getMember();
		if (member != null) {
			etName.setText(member.name);
			
			sex = member.sex;
			onRefreshSex();

			etEmail.setText(member.email);
			
			tvBirthday.setText(member.birthday);

			mAvatarUploadUrl = member.logoUrl;
			if (!TextUtils.isEmpty(mAvatarUploadUrl)) {
				AppController.getInstance().getImageLoader().get(mAvatarUploadUrl, ImageLoader.getImageListener(ivAvatar, R.drawable.ic_image, R.drawable.ic_image));
			}
		}
	}

	private void onRefreshSex() {
		if (Constants.Sex.BOY.equals(sex)) {
			tvSexBoy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			tvSexGirl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
		} else if (Constants.Sex.GIRL.equals(sex)) {
			tvSexBoy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvSexGirl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
		} else {
			tvSexBoy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvSexGirl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
		}
	}
	
	@Override
	public void onBindListener() {
		llAvatar.setOnClickListener(this);
		llBirthday.setOnClickListener(this);

		tvSexBoy.setOnClickListener(this);
		tvSexGirl.setOnClickListener(this);

		btnCommit.setOnClickListener(this);

		etName.setOnFocusChangeListener(this);
		etEmail.setOnFocusChangeListener(this);

		ivAvatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UiHelper.toBrowseImage(getActivity(), mSpUtil.getAvatar());
			}
		});
	}

	private void initDialog() {
		mSelectPicDialog = new SelectPicDialog(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.btn_select_pic) {
					UiHelper.pickImage(EditProfileActivity.this);
				} else if (v.getId() == R.id.btn_camera_pic) {
					takeImageUri = Uri.fromFile(Utils.getOutputFile(EditProfileActivity.this));
					UiHelper.takeImage(EditProfileActivity.this, takeImageUri);
				}
				mSelectPicDialog.dismiss();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_avatar:
			showSelectPicDialog();
			break;
		case R.id.tv_sex_boy:
			sex = Constants.Sex.BOY;
			onRefreshSex();
			break;
		case R.id.tv_sex_girl:
			sex = Constants.Sex.GIRL;
			onRefreshSex();
			break;
		case R.id.ll_birthday:
			showSelectBirthdayDialog();
			break;
		case R.id.btn_commit:
			if (checkAvatar() && checkName() && checkEmail()) {
				requestUpdateProfile();
			}
			break;
		}
	}

	private void showSelectBirthdayDialog() {
		String birthday = getBirthday();
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		if (!TextUtils.isEmpty(birthday)) {
			String[] b = birthday.split("-");
			if (b != null && b.length >= 3) {
				year = Integer.parseInt(b[0]);
				month = Integer.parseInt(b[1]);
				dayOfMonth = Integer.parseInt(b[2]);
			}
		}
		
		DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                        tvBirthday.setText(year + "-" + (month+1) + "-" + dayOfMonth);
                    }
                }, 
                year, // 传入年份
                month, // 传入月份
                dayOfMonth // 传入天数
            );
		dialog.show();
	}

	private boolean checkAvatar() {
		boolean check = false;
		if (TextUtils.isEmpty(mAvatarUploadUrl)) {
			showToast("请上传头像");
		} else {
			check = true;
		}
		return check;
	}

	private void showSelectPicDialog() {
		if (mSelectPicDialog != null) {
			mSelectPicDialog.show();
		}
	}

	public String getName() {
		return etName.getText().toString();
	}
	
	private boolean checkName() {
		String name = getName();

		boolean check = false;
		if (TextUtils.isEmpty(name)) {
			showToast("请输入会员名");
		} else {
			check = true;
		}

		return check;
	}

	private boolean checkEmail() {
		String email = getEmail();

		boolean check = false;
		if (!TextUtils.isEmpty(email) && !Utils.checkEmail(email)) {
			showToast("邮件格式不正确请重新输入");
		} else {
			check = true;
		}

		return check;
	}

	public String getEmail() {
		return etEmail.getText().toString();
	}

	public String getBirthday() {
		return tvBirthday.getText().toString();
	}
	
	public String getSex() {
		if (TextUtils.isEmpty(sex)) {
			sex = "";
		}
		return sex;
	}

	private void requestUpdateProfile() {
		final String name = getName();
		final String sex = getSex();
		final String birthday = getBirthday();
		final String email = getEmail();

		// Tag used to cancel the request
		String tag = "tag_request_register";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				showToast("保存成功");
				
				Member member = mSpUtil.getMember();
				if (member == null) {
					member = new Member();
				}
				member.logoUrl = mAvatarUploadUrl;
				member.name = name;
				member.sex = sex;
				member.birthday = birthday;
				member.email = email;
				
				mSpUtil.setMember(member);
				
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在提交...");
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.updateMemberDetail");
				params.put("informationId", mSpUtil.getBelongObjIdStr());
				params.put("logoUrl", mAvatarUploadUrl);
				params.put("name", name);
				if (!TextUtils.isEmpty(sex)) {
					params.put("sex", sex);
				}
				if (!TextUtils.isEmpty(birthday)) {
					params.put("birthday", birthday);
				}
				if (!TextUtils.isEmpty(email)) {
					params.put("email", email);
				}
				return params;
			}
		}, tag);
	}

	@Override
	public void onFocusChange(View view, boolean focus) {
		if (focus) {
			if (view instanceof EditText) {
				EditText et = (EditText) view;
				Selection.setSelection(et.getEditableText(), et.getEditableText().length());
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == UiHelper.REQUEST_PICK && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();

				Uri pickedUri = uri;

				String path = Utils.getPath(this, uri);
				if (!TextUtils.isEmpty(path)) {
					pickedUri = Uri.fromFile(new File(path));
				}
				setImageUriInType(pickedUri);
			}
		} else if (requestCode == UiHelper.REQUEST_TAKE && resultCode == RESULT_OK) {
			setImageUriInType(takeImageUri);
		}
	}

	private void setImageUriInType(Uri uri) {
		String uploadId = UUID.randomUUID().toString();
//		ivAvatar.setImageURI(uri);
		uploadImage(uri, uploadId);
	}

	private void uploadImage(Uri cropUri, String uploadId) {
		long belongObjId = mSpUtil.getBelongObjId();
		
		final String serverUrlString = NetRequest.SERVER_URL;
		final String fileToUploadPath = cropUri.getPath().toString();
		final String paramNameString = "file";
		final String fileName = Utils.getFileName(fileToUploadPath);
		final UploadRequest request = new UploadRequest(this, uploadId, serverUrlString);

		request.addFileToUpload(fileToUploadPath, paramNameString, fileName, ContentType.APPLICATION_OCTET_STREAM);
//		request.setNotificationConfig(R.drawable.ic_launcher, getString(R.string.app_name), getString(R.string.uploading), getString(R.string.upload_success),
//				getString(R.string.upload_error), false);
		request.addParameter("mtd", "com.guocui.tty.api.web.FileController.upFilesSimple");
		request.addParameter("app", NetRequest.APP);
		request.addParameter("memberId", Long.toString(belongObjId));

		Map<String, String> params = new HashMap<String, String>();
		params.put("memberId", Long.toString(belongObjId));
		params.put("mtd", "com.guocui.tty.api.web.FileController.upFilesSimple");
		params.put("app", NetRequest.APP);

		request.addParameter("sign", NetRequest.genSign(params));
		try {
			UploadService.startUpload(request);

			toShowProgressMsg("正在上传...");
		} catch (Exception exc) {
			Toast.makeText(this, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

		@Override
		public void onProgress(String uploadId, int progress) {
			Log.i(TAG, "The progress of the upload with ID " + uploadId + " is: " + progress);
		}

		@Override
		public void onError(String uploadId, Exception exception) {
			toCloseProgressMsg();
			String message = "Error in upload with ID: " + uploadId + ". " + (exception != null ? exception.getLocalizedMessage() : "");
			Log.e(TAG, message, exception);
			mAvatarUploadUrl = null;
			showToast("头像上传失败");
		}

		@Override
		public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage, final String serverResponseContent) {
			toCloseProgressMsg();
			Log.i(TAG, "Upload with ID " + uploadId + " is completed: " + serverResponseCode + ", " + serverResponseMessage + ", " + serverResponseContent);

			String url = null;
			try {
				JSONObject jsonObj = new JSONObject(serverResponseContent);
				String code = jsonObj.optString("code");
				String message = jsonObj.optString("message");

				if ("0000".equals(code)) {
					JSONArray dataArr = jsonObj.optJSONArray("data");
					if (dataArr != null && dataArr.length() > 0) {
						JSONObject data = dataArr.getJSONObject(0);
						if (data != null) {
							url = data.optString("url");
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (TextUtils.isEmpty(url)) {
				onError(uploadId, null);
				return;
			}

			mAvatarUploadUrl = url;
			showToast("头像上传成功");
			AppController.getInstance().getImageLoader().get(url, ImageLoader.getImageListener(ivAvatar, R.drawable.ic_avatar, R.drawable.ic_avatar));
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uploadReceiver.unregister(this);
	}

}
