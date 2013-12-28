package com.encore;

import android.app.Service;

public abstract class UploadService extends Service {
//	private static String tag = "UploadService";
//	private static String serverUrl = "http://158.130.153.103:8000/upload/";
//	String path;
//	
//	ProgressDialog dialog = null;
//	
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Log.d(tag, "onCreate() Upload Service");
//	}
//	
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		super.onStartCommand(intent, flags, startId);
//		return Service.START_NOT_STICKY;
//	}
//	
//	@Override
//	public ComponentName startService(Intent i){
//		super.startService(i);
//		Log.d(tag, "startService() UploadService");
//		path = i.getStringExtra("path");
//		return null;
//	}
//
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return null;
//	}
//	
//	
//	public class UploadTask extends AsyncTask<Void,Void,Void> {
//		
//		public UploadTask () {
//			
//		}
//		
//		@Override
//		protected Void doInBackground(Void... params) {
//			uploadFile(path);
//			stopSelf();
//			return null;
//		}
//		
//	}
//	public int uploadFile(String sourceFileUri) {
//        
//        
//        String fileName = sourceFileUri;
//
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;  
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024; 
//        File sourceFile = new File(sourceFileUri); 
//         
//        if (!sourceFile.isFile()) {
//             
//        	Toast.makeText(this, "Invalid File or path"
//                    , Toast.LENGTH_LONG).show();
//          
//        }
//        else
//        {
//             try { 
//                  
//                   // open a URL connection to the Servlet
//                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                 URL url = new URL(serverUrl);
//                  
//                 // Open a HTTP  connection to  the URL
//                 conn = (HttpURLConnection) url.openConnection(); 
//                 conn.setDoInput(true); // Allow Inputs
//                 conn.setDoOutput(true); // Allow Outputs
//                 conn.setUseCaches(false); // Don't use a Cached Copy
//                 conn.setRequestMethod("POST");
//                 conn.setRequestProperty("Connection", "Keep-Alive");
//                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                 conn.setRequestProperty("uploaded_file", fileName); 
//                  
//                 dos = new DataOutputStream(conn.getOutputStream());
//        
//                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
//                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
//                                           + fileName + "\"" + lineEnd);
//                  
//                 dos.writeBytes(lineEnd);
//        
//                 // create a buffer of  maximum size
//                 bytesAvailable = fileInputStream.available(); 
//        
//                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                 buffer = new byte[bufferSize];
//        
//                 // read file and write it into form...
//                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
//                    
//                 while (bytesRead > 0) {
//                      
//                   dos.write(buffer, 0, bufferSize);
//                   bytesAvailable = fileInputStream.available();
//                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
//                    
//                  }
//        
//                 // send multipart form data necesssary after file data...
//                 dos.writeBytes(lineEnd);
//                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//        
//                 // Responses from the server (code and message)
//                 int serverResponseCode = conn.getResponseCode();
//                 String serverResponseMessage = conn.getResponseMessage();
//                   
//                 Log.i("uploadFile", "HTTP Response is : "
//                         + serverResponseMessage + ": " + serverResponseCode);
//                  
//                 if(serverResponseCode == 200){
//                      
//                     runOnUiThread(new Runnable() {
//                          public void run() {
//                               
//                              String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                            +" http://www.androidexample.com/media/uploads/"
//                                            +uploadFileName;
//                               
//                              messageText.setText(msg);
//                              Toast.makeText(UploadToServer.this, "File Upload Complete.", 
//                                           Toast.LENGTH_SHORT).show();
//                          }
//                      });                
//                 }    
//                  
//                 //close the streams //
//                 fileInputStream.close();
//                 dos.flush();
//                 dos.close();
//                   
//            } catch (MalformedURLException ex) {
//                 
//                dialog.dismiss();  
//                ex.printStackTrace();
//                 
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
//                        Toast.makeText(UploadToServer.this, "MalformedURLException", 
//                                                            Toast.LENGTH_SHORT).show();
//                    }
//                });
//                 
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
//            } catch (Exception e) {
//                 
//                dialog.dismiss();  
//                e.printStackTrace();
//                 
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(UploadToServer.this, "Got Exception : see logcat ", 
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Log.e("Upload file to server Exception", "Exception : "
//                                                 + e.getMessage(), e);  
//            }
//            dialog.dismiss();       
//            return serverResponseCode; 
//             
//         } // End else block 
//        
//       }

}
