# MyJqueryMobileApp
jQuery.mobile.js 和android 交互
### 站在巨人的肩膀上，只为看的更远
废物 不多说上图</br>![](https://github.com/xuwei1995/MyJqueryMobileApp/blob/master/app/src/main/res/drawable/jquery.gif?raw=true)
</br>
jQuery Mobile 是创建移动 web 应用程序的框架。</br>
jQuery Mobile 适用于所有流行的智能手机和平板电脑。</br>
jQuery Mobile 使用 HTML5 和 CSS3 通过尽可能少的脚本对页面进行布局。</br>
http://www.w3school.com.cn/jquerymobile/jquerymobile_intro.asp</br>
jQuery Mobile教程</br>
为什么要get jQuery Mobile</br>
您不需要为每种移动设备或 OS 编写一个应用程序：</br>
Android 和 Blackberry 用 Java 编写</br>
iOS 用 Objective C 编写</br>
Windows Phone 用 C# 和 .net 编写</br>
jQuery Mobile 解决了这个问题，因为它只用 HTML、CSS 和 JavaScript，这些技术都是所有移动 web 浏览器的标准。</br>
# 这里我主要讲的是和android 的交互 jQuery Mobile 的东西可以自己去看教程 我只用到了jQuery Mobile 的冰山一角。（本人对于jQuery Mobile 完全是个菜鸟没有去研究过）
## jQuery Mobile 在Androidstudio 中引入
首先选择project 如图</br>
![](https://github.com/xuwei1995/MyJqueryMobileApp/blob/master/app/src/main/res/drawable/jmasl1.png?raw=true)
新建assets</br>![](https://github.com/xuwei1995/MyJqueryMobileApp/blob/master/app/src/main/res/drawable/jmqsl2.png?raw=true)
</br>再把你写好的web工程复制到assets目录下 如图</br>
![](https://github.com/xuwei1995/MyJqueryMobileApp/blob/master/app/src/main/res/drawable/jmasl3.png?raw=true)
好了我们来看一看核心部分了我在MainActivity写了什么 下面是MainActivity
```java 
public class MainActivity extends AppCompatActivity
{
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mWebView.setWebChromeClient(new WebChromeClient(){
            //处理加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("xx","处理加载进度");
            }
            //处理标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("xx","处理标题");
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            //页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("xx","页面加载完成");
            }
            //页面启动时
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("xx","页面启动时");
            }
        });
    }

    private void initData() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//开启webview支持JS

        JavaScriptMethods javaScriptMethods=new JavaScriptMethods(MainActivity.this,mWebView);
        //第一个参数是对象，第二个参数是对象的映射字符串，js是通过映射字符串来调用java中的方法的
        mWebView.addJavascriptInterface(javaScriptMethods,"javaInterface");
    }

    private void initView() {
        mWebView= (WebView) findViewById(R.id.webView);
     //   mEturl = (EditText) findViewById(R.id.et_url);
    }
    public void goToWeb(View view){
        //从本地加载，上线时用,加载速度更快
        mWebView.loadUrl("file:///android_asset/jQueryMobileDemo/JscommAndroid.html");

    }
    public void callJs(View view){
        //webview.loadUrl("javascript:方法名(参数)");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("msg","let's conquer the world");
            jsonObject.put("name","hero");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        mWebView.loadUrl("javascript:showMsg("+ jsonObject.toString()+")");

    }
}
```
第一个按钮的点击事件就是goToWeb();
没啥好说的就是用webview组件去加载
file:///android_asset/jQueryMobileDemo/JscommAndroid.html 
这个网页（也就我assets目录下的网页）</br>
 等网页加载完成以后我们就可以使用第二个按钮了</br>
 第二个按钮的事件就是callJs();看他的名字我们就懂了这是在android中使用js的方法
 </br>使用规则 webview.loadUrl("javascript:方法名(参数)");而这里的 mWebView.loadUrl("javascript:showMsg("+ jsonObject.toString()+")");
 就是使用了js 中的showMsg()方法了 下面是js的showMsg();的源码我们看一看
 ```JavaScript
 	/**
			 * 显示信息
			 * @param {Object} msg
			 */
			function showMsg(msg) {
				console.log("showMsg");
				window.javaInterface.showMsg(JSON.stringify(msg));
			}
 ```
 	console.log("showMsg");这句话很好理解只要你有一点js基础
 这时你会说这个	window.javaInterface.showMsg(JSON.stringify(msg));是什么东西
 其实这句话又用js去调用了java的showMsg方法 下面我们就来看JavaScriptMethods这个类
  ```java
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

  ```
  睁大你的眼睛你会看到
    ```java
     /**
     * 弹出信息
     * @param msg
     */
    @JavascriptInterface
    public void showMsg(String msg){
        Toast.makeText(mContext,msg+"我", Toast.LENGTH_SHORT).show();
    }
      ```
  也就是我们又调用了这个方法 然后在这里吐司了 Toast.makeText(mContext,msg+"我", Toast.LENGTH_SHORT).show();
  当时msg还是当时我们put的jsonobject
  ### 这里我们就实现了java到js js再到android 
 ### 同样我们可以js到 android android再到js
 <button class="btn" id="jscallandroidCallBack">JS调用安卓方法(callback)</button>这个按钮
 在js中我们找到这段代码
  ```JavaScript
  	$("#jscallandroidCallBack").on("click", function() {
					console.log("jscallandroidCallBack");
					
					window.javaInterface.getDetail("{'name':'xuwei','msg':'go to hell','callback':'callBackMethod'}");
				});
			});
   ```
  这里我们在js中调用了android 的getDetail方法我们再汇过去看getDetail
   ```java
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
   ```
   请看  mWebView.loadUrl("javascript:"+method+"("+msg+")");
   method这里的值就是传过来的callback键对应的callBackMethod
   也就是再去调用js中的
   ```JavaScript
   /**
			 * 显示信息
			 * @param {Object} msg
			 */
			function callBackMethod(msg) {
				console.log("callBackMethod");
				alert(JSON.stringify(msg))
				//window.javaInterface.showMsg(JSON.stringify(msg));
			}
      ```
      我们看到的	alert(JSON.stringify(msg)) 就是那个弹窗咯
      ### 第三个html按钮就是在js中调用android代码做了一个简单的跳转 没什么可讲的了
      
      如果这个demo帮助到你了，请你给版主一个star谢谢，版主还是一个android菜鸟 希望和大家共同学习下面是我的QQ二维码 欢迎大家骚扰和我一起学习</br>
      ![](https://github.com/xuwei1995/MyJqueryMobileApp/blob/master/app/src/main/res/drawable/erweimaq.png?raw=true)
  
