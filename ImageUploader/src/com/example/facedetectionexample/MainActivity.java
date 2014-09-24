package com.example.facedetectionexample;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity 
{
	private static final String ipv4Address = "192.168.1.5";
	private static final int portNumber = 8080;
	private static final int TAKE_PICTURE_REQUEST = 1;
	private Bitmap cameraBitmap = null;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Button)findViewById(R.id.take_picture)).setOnClickListener(btnClick);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(TAKE_PICTURE_REQUEST == requestCode)
		{
			processCameraImage(data);
		}
	}
	private void openCamera()
	{
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}
	private void processCameraImage(Intent intent)
	{
		setContentView(R.layout.detectlayout);
		((Button)findViewById(R.id.detect_face)).setOnClickListener(btnClick);
		ImageView imageView = (ImageView)findViewById(R.id.image_view);
		cameraBitmap = (Bitmap)intent.getExtras().get("data");
		imageView.setImageBitmap(cameraBitmap);
		//detectFaces();
	}
	private void detectFaces()
	{
		new Thread(new Runnable() 
		{
			public void run() 
			{

				try
				{
					URL url = new URL("http://"+ipv4Address+":"+portNumber+"/MyServerTest/DoubleMeServlet");
					URLConnection connection = url.openConnection();
					connection.setDoOutput(true);
					OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					byte[] imageBytes = baos.toByteArray();
					String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
					out.write(encodedImage);
					out.close();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String returnString= "";
					while ((returnString = in.readLine()) != null) 
					{
						//doubledValue= Integer.parseInt(returnString);
						//returnString = in.readLine();	
					}
					Log.d(returnString,"");
					in.close();
				}catch(Exception e)
				{
					Log.d("Exception",e.toString());
				}
			}
		}).start();
	}
	/*
	private void showMessage(String msg)
	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	*/
	private View.OnClickListener btnClick = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			switch(v.getId()){
			case R.id.take_picture:         openCamera();   break;
			case R.id.detect_face:          detectFaces();  break;  
			}
		}
	};
}