package com.alextam.webviewdemo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;



/**
 * Created on 5/21/2015
 * @author Alex Tam
 *
 */
public class WebViewActicvity extends Activity{
	private WebView wv_main;
	//MAP - 存放要显示的图片信息
	private  ConcurrentHashMap<String, String> map = new  ConcurrentHashMap<String, String>();
	//图片文件夹
	private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ MainActivity.SEPERATOR + "WebViewDemo";
	
	private DAOHelper helper;
	//存放图片下载器信息
	private List<String> taskArray = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_act_main);
		//数据库操作类
		helper = new DAOHelper(WebViewActicvity.this);
		
		start();
	}
	
	private void start()
	{
		wv_main = (WebView)findViewById(R.id.wv_main);
		
		wv_main.getSettings().setJavaScriptEnabled(true);
		wv_main.setWebViewClient(new WebViewClient());
		// 单列显示
		wv_main.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// 添加JavaScript接口
		// "mylistner" 这个名字不能写错,
		// 因为在WebView加载的HTML中的JavaScript方法会回调mylistner里面的方法,所以两者名字要一致
		wv_main.addJavascriptInterface(new JavascriptInterface(WebViewActicvity.this), "mylistner");
		// 为了模拟向服务器请求数据,加载HTML, 我已提前写好一份,放在本地直接加载
		wv_main.loadUrl("file:///android_asset/wv_content.html");
	}
	
	
	private class JavascriptInterface 
	{
		private Context context;

		public JavascriptInterface(Context context) 
		{
			this.context = context;
		}
		
		//该方法被回调替换页面中的默认图片
		@android.webkit.JavascriptInterface
		public String replaceimg(String imgPosition , String imgUrl, String imgTagId)
		{
			if(!map.containsKey(imgUrl))
			{	//如果中介存储器MAP中存在该图片信息,就直接使用,不再去数据库查询
				String imgPath = helper.find(imgUrl);
				if(imgPath != null && new File(imgPath).exists())
				{
					map.put(imgUrl, imgPath);
					return imgPath;
				}
				else
				{	
					
					if(taskArray.indexOf(imgUrl) < 0)
					{	// 当图片链接不存在数据库中,同时也没有正在下载该链接的任务时, 就添加新的下载任务
						// 下载任务完成会自动替换
						taskArray.add(imgUrl);
						DownLoadTask task = new DownLoadTask(imgTagId, imgPosition, imgUrl);
						task.execute();
					}
					// 为了模拟默认图片的加载进度, 在这里返回另一张不一样的默认图片,
					// 具体应用中,可以根据需求将该处改为某些百分比之类的图片
					return "file:///android_asset/test.jpg";
				}
				
			}
			else
			{
				return map.get(imgUrl);
			}
		}
		
		
	}
	
	//图片下载器
	private class DownLoadTask extends AsyncTask<Void, Void, String>
	{
		String imageId; 		//标签id
		String imagePosition;	//图片数组位置标记
		String imgUrl;			//图片网络链接
		
		public DownLoadTask(String imageId, String imagePosition, String imgUrl)
		{
			this.imageId = imageId;
			this.imagePosition = imagePosition;
			this.imgUrl = imgUrl;
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			try 
			{
				// 下载图片
				URL url = new URL(imgUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(20 * 1000);  
				conn.setReadTimeout(20 * 1000);
		        conn.setRequestMethod("GET");  
		        conn.connect();
		        InputStream in = conn.getInputStream();
		        
		        byte[] myByte = readStream(in);
		        //压缩存储,有需要可以将bitmap放入别的缓存中,另作他用, 比如点击图片放大等等
		        Bitmap bitmap = BitmapFactory.decodeByteArray(myByte, 0, myByte.length);
		        
		        String fileName = Long.toString(System.currentTimeMillis()) + ".jpg";
		        File imgFile = new File(rootPath + MainActivity.SEPERATOR +fileName);
		        
		        BufferedOutputStream bos 
		        	= new BufferedOutputStream(new FileOutputStream(imgFile));  
		        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos); 
		        
		        bos.flush();  
		        bos.close();
		        
		        return imgFile.getAbsolutePath();
		        
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String imgPath)
		{
			super.onPostExecute(imgPath);
			if(imgPath != null)
			{
				//对页面调用js方法, 将默认图片替换成下载后的图片
				String url = 
						"javascript:(function(){" 
						+ "var img = document.getElementById(\""
						+ imageId
						+ "\");"
						+ "if(img !== null){"
						+ "img.src = \""
						+ imgPath
						+ "\"; }"
						+ "})()";
						
				wv_main.loadUrl(url);
				// 将将图片信息缓存进中介存储器
				map.put(imgUrl, imgPath);
				// 将图片信息缓存进数据库
				helper.save(imgUrl, imgPath);
			}
			else
			{
				Log.e("WebViewActicvity error", "DownLoadTask has a invalid imgPath...");
			}
			
		}
		
	}
	
	private byte[] readStream(InputStream inStream) throws Exception
	{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[2048];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }
	
	
	
	
	
}
