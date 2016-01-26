package com.xweather.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.xweather.bean.JuheInfo;

import android.content.Context;
/**
 *save and read object,for juheinfo. 
 */
public class ObjectUtil {

	private final static String fileName = "juheinfo"; 
	
	public static void saveJuheInfo(Context context,JuheInfo info){
		if(info == null) return;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(info);
			oos.flush();
			LogUtil.getInstance().info("write object success");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(oos != null) oos.close();
				if(fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static JuheInfo readJuheInfo(Context context){
		JuheInfo info = null;
		Object o = null ;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			o = ois.readObject();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			try {
				if(fis != null) fis.close();
				if(ois != null) ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(o != null && o instanceof JuheInfo){
			info = (JuheInfo) o;
			LogUtil.getInstance().info("read object success");
		}
		
		return info;
	}
	
}
