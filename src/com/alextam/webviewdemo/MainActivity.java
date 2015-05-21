package com.alextam.webviewdemo;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created on 5/21/2015
 * @author Alex Tam
 *
 */
public class MainActivity extends Activity {
	private Button btn_start;
	private String rootPath;
	public static final String SEPERATOR = "/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
	}
	
	private void init()
	{
		btn_start = (Button)findViewById(R.id.btn_start);
		
		createCacheFolder();
		
		btn_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(createCacheFolder())	//先检测是否成功创建本地缓存文件夹
				{
					Intent goIntent = new Intent(MainActivity.this,WebViewActicvity.class);
					startActivity(goIntent);
				}
			}
		});
	}
	
	
	
	//创建本地缓存文件夹 - 存放图片
	private boolean createCacheFolder()
	{
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ SEPERATOR + "WebViewDemo";
			
			File cFile = new File(rootPath);
			if(!cFile.exists())
			{
				cFile.mkdir();
			}
			return true;
		}
		else
		{
			t(this, "无法创建本地文件夹,请插入SD卡");
			
			return false;
		}
	}
	
	
	public static final void t(Context context , String c)
	{
		Toast.makeText(context, c, Toast.LENGTH_SHORT).show();
	}
	

}
