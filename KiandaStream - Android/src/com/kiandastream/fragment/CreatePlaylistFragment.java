package com.kiandastream.fragment;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kiandastream.R;
import com.kiandastream.model.MainSingleTon;

public class CreatePlaylistFragment extends Fragment
{
	ImageView selectimage,createplaylist;
	EditText playlistname,playlistdesc;
	String selectedImagePath;
	boolean isimageselect=false;
	CheckBox ispublic;
	private  final int CAMERA_REQUEST = 1888,RESULT_LOAD_IMAGE=1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View rootview=inflater.inflate(R.layout.createplaylistfragment, container, false);
		selectimage=(ImageView)rootview.findViewById(R.id.playlistimage);
		createplaylist=(ImageView)rootview.findViewById(R.id.createplaylist);
		playlistname=(EditText)rootview.findViewById(R.id.playlistname);
		playlistdesc=(EditText)rootview.findViewById(R.id.playlistdesc);
		ispublic=(CheckBox)rootview.findViewById(R.id.checkBox1);
		selectimage.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				
			}
		});
		createplaylist.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(isimageselect)
				{
				if(!playlistname.getText().toString().isEmpty())
				{
					if(!playlistdesc.getText().toString().isEmpty())
					{
						if(ispublic.isChecked())
						{
							new CreatePlaylist().execute(playlistname.getText().toString(),playlistdesc.getText().toString(),"1",selectedImagePath);
						}else
						{
							new CreatePlaylist().execute(playlistname.getText().toString(),playlistdesc.getText().toString(),"0",selectedImagePath);
						}
					}else
					{
						Toast.makeText(getActivity(), "Please Enter description", Toast.LENGTH_LONG).show();
					}
				}else
				{
					Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_LONG).show();
				}
				}else
				{
					Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_LONG).show();
				}
			}
		});
		return rootview;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if (requestCode == RESULT_LOAD_IMAGE && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String  picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			//image.setImageURI(selectedImage);
			 selectedImagePath = getPath(selectedImage);
			
			Bitmap bitmap=decodeSampledBitmapFromResource(selectedImagePath,  800, 800);
			//Bitmap bitmap=BitmapFactory.decodeFile(selectedImagePath);
			bitmap=Bitmap.createScaledBitmap(bitmap, 600, 600, true);
			ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bmpStream);
			isimageselect=true;
			selectimage.setImageBitmap(bitmap);
			}
		}
	
	public String getPath(Uri uri) 
	{
		  String[] projection = { MediaStore.Images.Media.DATA };
		  Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
		  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		  cursor.moveToFirst();
		  return cursor.getString(column_index);
		 
	}
	public  Bitmap decodeSampledBitmapFromResource(String res,  int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(res,  options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(res,  options);
		}
		public  int calculateInSampleSize(
		    BitmapFactory.Options options, int reqWidth, int reqHeight) {
		//Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

		final int halfHeight = height / 2;
		final int halfWidth = width / 2;

		// Calculate the largest inSampleSize value that is a power of 2 and keeps both
		// height and width larger than the requested height and width.
		while ((halfHeight / inSampleSize) > reqHeight
		        && (halfWidth / inSampleSize) > reqWidth) {
		    inSampleSize *= 2;
		}
		}

		return inSampleSize;
		}
		
	class CreatePlaylist extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			String fileName=params[3];
			String paramter = MainSingleTon.Main_url+"t=playlist&user_id="+MainSingleTon.userid+"&access_token="+MainSingleTon.accesstokem+"&action=create&name="+params[0]+"&descr="+params[1]+"&access="+params[2];

			
			  //System.out.println("phone no is "+phoneno);
			  HttpURLConnection conn = null;
			  DataOutputStream dos = null;  
			  String lineEnd = "\r\n";
			  String twoHyphens = "--";
			  String boundary = "*****";
			  int bytesRead, bytesAvailable, bufferSize;
			  byte[] buffer;
			  int maxBufferSize = 1 * 1024 * 1024; 
			  String response="" ,str;
			  System.out.println("sourceFileUri="+fileName);
			  
				   int serverResponseCode;
				try 
				   { 
					    byte[] image;
					    
					  
					  //http://groupino.globusapps.com/android/postquestion/?type=0&id=917&question=dsfdgfh&url=xxx?&catagory=12
					    URL url = new URL(paramter);
			
					    // Open a HTTP  connection to  the URL
					    conn = (HttpURLConnection) url.openConnection(); 
					    conn.setDoInput(true); // Allow Inputs
					    conn.setDoOutput(true); // Allow Outputs
					    conn.setRequestMethod("POST");
					    conn.setRequestProperty("Connection", "Keep-Alive");
					    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
					    
					    dos = new DataOutputStream(conn.getOutputStream());
					    dos.writeBytes(twoHyphens + boundary + lineEnd); 
					     if(fileName.length()==0)
					    {
					    	
					    }
					    else
					    {
					    	File sourceFile = new File(fileName);
						    FileInputStream fileInputStream = new FileInputStream(sourceFile);
							if (!sourceFile.isFile())
							{
							}
							else
							{
								    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename="+ fileName + "" + lineEnd);
								    dos.writeBytes(lineEnd);
								    System.out.println("file name   "+fileName);
								    
						
								    // create a buffer of  maximum size
								    bytesAvailable = fileInputStream.available(); 
						
								    bufferSize = Math.min(bytesAvailable, maxBufferSize);
								    buffer = new byte[bufferSize];
						
								    // read file and write it into form...
								    bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
								   
								    while (bytesRead > 0)
								    {
									     dos.write(buffer, 0, bufferSize);
									     bytesAvailable = fileInputStream.available();
									     bufferSize = Math.min(bytesAvailable, maxBufferSize);
									     bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
						
								    }
						
								    // send multipart form data necesssary after file data...
								    dos.writeBytes(lineEnd);
								    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
							  } 
							fileInputStream.close();
					    }
							    
					    // Responses from the server (code and message)
					    serverResponseCode = conn.getResponseCode();
					    String serverResponseMessage = conn.getResponseMessage();
					    
					    System.out.println("*******URL***********    "+conn.getURL());
					    System.out.println("******************    "+serverResponseCode);
					    System.out.println("******************    "+serverResponseMessage);
					    System.out.println("******************    "+conn.getRequestProperty("ID"));
					    System.out.println("******************    "+conn.getRequestProperty("unique_que"));
					    
					    Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
			
					    String serverResponseMessage1 = conn.getResponseMessage();
					    try
					    {
					    	DataInputStream inStream = new DataInputStream(conn.getInputStream());
						    while ((str = inStream.readLine()) != null) 
						    {
							     response=response+str;
							     Log.e("Debug", "Server Response " + str);
						    }
						    inStream.close();
					   } 
					   catch (IOException ioex)
					   {
						   Log.e("Debug", "error: " + ioex.getMessage(), ioex);
					   }
					      System.out.println("******************    "+serverResponseMessage1);
					      Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage1 + ": " + serverResponseCode);
			
					    //close the streams //
					    
					    dos.flush();
					    dos.close();
		
				   }
				   catch (MalformedURLException ex)
				   {
					    ex.printStackTrace();
					    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
				   } 
				   catch (Exception e)
				   {
					    e.printStackTrace();
					    Log.e("Upload file to server Exception", "Exception : "+ e.getMessage(), e);  
				   }
				   conn.disconnect();

			return response;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			System.out.println("result from playlsit "+result);
			if(result!=null && !result.isEmpty())
			{
				try {
					JSONObject object=new JSONObject(result);
					if(object.getInt("status_code")==200)
					{
						Toast.makeText(getActivity(), "Playlist Created", Toast.LENGTH_LONG).show();
						playlistname.setText("");
						playlistdesc.setText("");
						selectimage.setImageResource(R.drawable.selectimage);
						
					}else
					{
						Toast.makeText(getActivity(), "Error in creating /n Please try again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}
		
	}	
}
