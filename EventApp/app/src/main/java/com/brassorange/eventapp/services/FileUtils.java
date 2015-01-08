package com.brassorange.eventapp.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileUtils {

	private Context ctx;

	public FileUtils(Context ctx) {
		this.ctx = ctx;
	}
	
	// Tools for dealing with Internal Files
	public void writeFileToInternalStorage(String fileName, String fileContents) {
		String eol = System.getProperty("line.separator");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(ctx.openFileOutput(fileName, Context.MODE_PRIVATE)));
			writer.write(fileContents + eol);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void writeImageToInternalStorage(String fileName, Bitmap bitmap) {
		try {
			FileOutputStream pictureFile = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, pictureFile);
			pictureFile.close();
	    } catch (FileNotFoundException e) {
	        Log.d(getClass().getSimpleName(), "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d(getClass().getSimpleName(), "Error accessing file: " + e.getMessage());
		}
	}

	public String readFileFromInternalStorage(String fileName) {
		String eol = System.getProperty("line.separator");
		String fileContents = "";
		if (!ctx.getFileStreamPath(fileName).exists())
			return fileContents;
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = input.readLine()) != null) {
                buffer.append(line)
                      .append(eol);
			}
			fileContents = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileContents;
	}

	public Bitmap readImageFromInternalStorage(String fileName) {
		Bitmap bm = null;
		try {
			InputStream is = ctx.openFileInput(fileName);
			bm = BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bm;
	}

	public File getFileFromInternalStorage(String fileName) {
		return new File(ctx.getFilesDir().getAbsolutePath() + "/" + fileName);
	}

	public List<String> getFileNamesFromInternalStorage(String filePattern) {
		String path = ctx.getFilesDir().getAbsolutePath();
		List<String> fileNames = new Vector<>();
	    for (final File fileEntry : (new File(path)).listFiles()) {
	        if (fileEntry.isFile() 
	        && fileEntry.getName().matches(filePattern)) {
	        	fileNames.add(fileEntry.getName());
	        }
	    }
	    Collections.sort(fileNames);
		return fileNames;
	}

	//-----------------------------------------------------------
    /*
	public String retrieve(String filePath) throws Exception {
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		//Make sure you close all streams.
		fin.close();        
		return ret;
	}
	*/
    /*
	private String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
		  sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}
    */

}
