package xuwei.com.myjquerymobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/*
 *  @项目名：  HybridDeveloperDemo 
 *  @包名：    xuwei.com.myjquerymobileapp;
 *  @文件名:   JsMethods
 *  @创建者:   xuwei引用 Army
 *  @创建时间:  2017/6/29
 *  @描述：    此类用于Html5交互数据
 */
public class JavaScriptMethods {
    private static final String TAG = "JsMethods";
    private Context mContext;
    private WebView mWebView;

    Handler mHandler=new Handler();

    public JavaScriptMethods(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

    /**
     * 弹出信息
     * @param msg
     */
    @JavascriptInterface
    public void showMsg(String msg){
        Toast.makeText(mContext,msg+"我", Toast.LENGTH_SHORT).show();
    }

    /**
     * js callback sample
     * @param msg
     */
    @JavascriptInterface
    public void getDetail(final String msg){
        try {
            JSONObject jsonObject =new JSONObject(msg);
            final String method     =jsonObject.optString("callback");
            showMsg(msg);
            //回调,记住mWebView.loadUrl必须放在主线程中调用
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:"+method+"("+msg+")");

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     * 跳转SecondActivity
     * @param msg
     */
    @JavascriptInterface
    public  void  toSecondActivity(String msg)
    {
        Toast.makeText(mContext,msg+"我", Toast.LENGTH_SHORT).show();
        mContext.startActivity(new Intent(mContext,SecondActivity.class));
    }
}
