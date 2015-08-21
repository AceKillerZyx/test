package com.education.zhxy.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class FileUtil {
	
	private static final long ONE_DAY_IN_MILLIS = 1l* 1000 * 60 * 60 * 24;
	
	public static boolean outOfDate(File f, int days) {
		if (null == f) {
			return true;
		}
		
		try {
			long l = f.lastModified();
			long extra = l + ONE_DAY_IN_MILLIS * days;
			
			return System.currentTimeMillis() > extra;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f = null;
		}
		
		return true;
	}

	public static boolean outOfDate(String fileName, int days) {
		if (StringUtil.isEmpty(fileName)) {
			return true;
		}
		
		return outOfDate(new File(fileName), days);
	}
	
	public static boolean exists(String fileName) {
		if (StringUtil.isEmpty(fileName)) {
			return false;
		}
		
		File f = null;
		try {
			f = new File(fileName);
			return f.exists();
		} catch (Exception e) {
			
		} finally {
			f = null;
			fileName = null;
		}
		
		return false;
	}
	
	public static byte[] read2ByteArr(String fileName) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		
		byte[] buffer = null;
		int len = 0;
		
		try {
			fis = new FileInputStream(fileName);
			baos = new ByteArrayOutputStream();
			
			buffer = new byte[fis.available()];
			
			while((len = fis.read(buffer)) != -1){
				baos.write(buffer, 0, len);
			}
			
			if (null != baos) {
				return baos.toByteArray();
			}
		} catch (Exception e) {
			
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) { }
			
			try {
				if (null != baos) {
					baos.close();
				}
			} catch (Exception e) { }
			
			fis = null;
			baos = null;
			buffer = null;
		}
		
		return null;
	}
    
    public static InputStream read2Stream(String fileName) {
        try {
            return new FileInputStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileName = null;
        }
        
        return null;
    }
	
	public static String read(String fileName) {
		if (StringUtil.isEmpty(fileName)) {
			return null;
		}
		
		byte[] buffer = null;
		try {
		    buffer = read2ByteArr(fileName);
		    if (! ArrayUtil.isEmptyArray(buffer)) {
		        return new String(buffer);
		    }
		} catch (Exception e) {
			
		} finally {
			fileName = null;
			buffer = null;
		}
		
		return null;
	}

	public static boolean rmdir(String dir) {
	    if(dir == null){
	        return true;
	    }
		File f = new File(dir);
		if (!f.exists()) {
			return true;
		}
		
		if (f.isDirectory()) {
			for (String sub : f.list()) {
				rmdir(dir + File.separator + sub);
			}
		}

		return f.delete();
	}
	
	public static boolean mkdir(String dir) {
        if (StringUtil.isEmpty(dir)) {
            return false;
        }
        
        File file = null;
        try {
            file = new File(dir);
            if (! file.exists()) {
                file.mkdirs();
            }
            
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            file = null;
            dir = null;
        }
        
        return false;
    }
	
	public static boolean writeFile(String fileAbsPath, byte[] buffer) {
        FileOutputStream fos = null;
        
        try {
        	if (! ArrayUtil.isEmptyArray(buffer)) {
	            fos = new FileOutputStream(fileAbsPath);
	            fos.write(buffer);
	            
	            return true;
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception e) { }
                
                fos = null;
            }
            
            fileAbsPath = null;
            buffer = null;
        }
        
        return false;
    }

    public static long dirSize(File dir) {
        long result = 0;
        File[] fileList = dir.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                result += dirSize(fileList[i]);
            } else {
                result += fileList[i].length();
            }
        }
        return result;
    }

    public static String fileSize(String path){
        File dir = new File(path);
        if(!dir.exists()){
            return "0M";
        }
        long result = 0;
        if(dir.isFile()){
            result = dir.length();
        }else{
            result = dirSize(dir);
        }
        if(result < 10240){
            return "0M";
        }else{
            return (result / 1024 / 1024) + "." + ((result % (1024 * 1024)) * 100 / 1024 /1024) + "M";
        }
    }
    
    /**
	 * @param path 所在目录
	 * @return 文件
	 */
	public static File createFile(String path) {
		File file = new File(path);
		// 寻找父目录是否存在
		File parent = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)));
		// 如果父目录不存在，则递归寻找更上一层目录
		if (!parent.exists()) {
			createFile(parent.getPath());
			// 创建父目录
			parent.mkdirs();
		}
		return file;
	}
	
	//从file转为byte[]
	   public static byte[] getBytesFromFile(File f){
	      if (f == null){
	          return null;
	      }
	      try {
	          FileInputStream stream = new FileInputStream(f);
	          ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
	          byte[] b = new byte[1000];
	          int n;
	          while ((n = stream.read(b)) != -1)
	              out.write(b, 0, n);
	           stream.close();
	           out.close();
	           return out.toByteArray();
	       } catch (IOException e){
	      }
	       return null;
	    }
	   
	   /**
		 * 读取表情配置文件
		 * 
		 * @param context
		 * @return
		 */
		public static List<String> getEmojiFile(Context context) {
			try {
				List<String> list = new ArrayList<String>();
				InputStream in = context.getResources().getAssets().open("emoji");
				BufferedReader br = new BufferedReader(new InputStreamReader(in,
						"UTF-8"));
				String str = null;
				while ((str = br.readLine()) != null) {
					list.add(str);
				}

				return list;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	
}
