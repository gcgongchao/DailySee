
package com.dailysee.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dailysee.R;

public class ConfirmDialog extends BaseDialog {
    public static final String CONTENT = "content";

    public static final String TITLE = "title";

    public static Bundle getBundle(String title, String content) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(CONTENT, content);
        return bundle;
    }

    private String confirmMsg;

    private View.OnClickListener onOKListener;

    private View.OnClickListener onCancelListener;

    private TextView tvMsg;

    private Button btnCancel;

    private View vMiddleLine;

    private Button btnOK;

    private String cancelText;

    private int cancelTextColor;

    private String okText;

    private int okTextColor;

    private boolean cancelEnable;

    public ConfirmDialog(Context context, String confirmMsg, boolean cancelEnable,
            String cancelText, int cancelTextColor, String okText, int okTextColor,
            View.OnClickListener onOKListener, View.OnClickListener cancelListener) {
        super(context);
        init(confirmMsg, cancelEnable, cancelText, cancelTextColor, okText, okTextColor,
                onOKListener, cancelListener);
    }

    public ConfirmDialog(Context context, String confirmMsg, boolean cancelEnable,
            String cancelText, String okText, View.OnClickListener onOKListener,
            View.OnClickListener cancelListener) {
        this(context, confirmMsg, cancelEnable, cancelText, 0, okText, 0, onOKListener,
                cancelListener);
    }

    public ConfirmDialog(Context context, String confirmMsg, boolean cancelEnable,
            String cancelText, String okText, View.OnClickListener onOKListener) {
        this(context, confirmMsg, cancelEnable, cancelText, okText, onOKListener, null);
    }

    public ConfirmDialog(Context context, String confirmMsg, String cancelText, String okText,
            View.OnClickListener onOKListener) {
        this(context, confirmMsg, true, cancelText, okText, onOKListener);
    }

    public ConfirmDialog(Context context, String confirmMsg, View.OnClickListener onOKListener) {
        this(context, confirmMsg, true, "取消", "确定", onOKListener);
    }

    public ConfirmDialog(Context context) {
        this(context, null, true, "取消", "确定", null);
    }
    
    public ConfirmDialog(Context context , boolean cancelEnable) {
        this(context, null, cancelEnable, "取消", "确定", null);
    }

    protected void btnCancel(View v) {
        if (onCancelListener != null)
            onCancelListener.onClick(v);
        dismiss();
    }

    protected void btnOK(View v) {
        if (onOKListener != null)
            onOKListener.onClick(v);
        dismiss();
    }

    protected int getLayoutId() {
        return R.layout.dialog_confirm;
    }

    protected void init(String confirmMsg, boolean cancelEnable, String cancelText,
            int cancelTextColor, String okText, int okTextColor, View.OnClickListener onOKListener,
            View.OnClickListener onCancelListener) {
        this.cancelEnable = cancelEnable;
        this.confirmMsg = confirmMsg;
        this.cancelText = cancelText;
        this.cancelTextColor = cancelTextColor;
        this.okText = okText;
        this.okTextColor = okTextColor;
        this.onOKListener = onOKListener;
        this.onCancelListener = onCancelListener;
    }

    protected void init(String confirmMsg, boolean cancelEnable, String cancelText, String okText,
            View.OnClickListener onOKListener, View.OnClickListener onCancelListener) {
        init(confirmMsg, cancelEnable, cancelText, 0, okText, 0, onOKListener, null);
    }

    public void init(String confirmMsg, boolean cancelEnable, String cancelText, String okText,
            View.OnClickListener onOKListener) {
        init(confirmMsg, cancelEnable, cancelText, okText, onOKListener, null);
    }

    protected void initCancelButton() {
        btnCancel = (Button)findViewById(R.id.btnCancel);
        vMiddleLine = (View)findViewById(R.id.vMiddleLine);
        if (cancelEnable) {
            btnCancel.setText(cancelText);
            if(cancelTextColor != 0){
                btnCancel.setTextColor(cancelTextColor);
            }
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnCancel(v);
                }
            });
        } else {
            btnCancel.setVisibility(View.GONE);
            vMiddleLine.setVisibility(View.GONE);
        }
    }

    protected void initOkButton() {
        btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setText(okText);
        if(okTextColor != 0){
            btnOK.setTextColor(okTextColor);
        }
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnOK(v);
            }
        });
    }

    protected void initViewContent() {
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        if (!TextUtils.isEmpty(confirmMsg)) {
            setContent(confirmMsg);
        }
    }

    protected void initViews() {
        initViewContent();
        initCancelButton();
        initOkButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(getLayoutId(), null);
        setContentView(view);
        initViews();
    }

	protected void init(Context context) {
		View view=getLayoutInflater().inflate(getLayoutId(), null);
		setContentView(view);
		getWindow().setGravity(Gravity.CENTER);
		setCanceledOnTouchOutside(true);
		initDialogViews();
		afterDialogViews();
		 Window win = getWindow();
	    WindowManager m = win.getWindowManager();
		DisplayMetrics  dm = new DisplayMetrics();    
	    m.getDefaultDisplay().getMetrics(dm);    
		//Display d = m.getDefaultDisplay(); //
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = (int) (dm.widthPixels * 0.9);
	    win.setAttributes(p);
	}

    public void setContent(CharSequence content) {
        tvMsg.setText(content);
    }

    public void setOnOKListener(View.OnClickListener onOKListener) {
        this.onOKListener = onOKListener;
    }

    public void setCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public String getConfirmMsg() {
        return confirmMsg;
    }

    public void setConfirmMsg(String confirmMsg) {
        this.confirmMsg = confirmMsg;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public String getOkText() {
        return okText;
    }

    public void setOkText(String okText) {
        this.okText = okText;
    }

    public int getCancelTextColor() {
        return cancelTextColor;
    }

    public void setCancelTextColor(int cancelTextColor) {
        this.cancelTextColor = cancelTextColor;
    }

    public int getOkTextColor() {
        return okTextColor;
    }

    public void setOkTextColor(int okTextColor) {
        this.okTextColor = okTextColor;
    }

	@Override
	protected void afterDialogViews() {
	}

	@Override
	protected void initDialogViews() {
	}
    
}
