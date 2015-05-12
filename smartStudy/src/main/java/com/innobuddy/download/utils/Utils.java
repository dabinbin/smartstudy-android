package com.innobuddy.download.utils;

import java.security.MessageDigest;

public class Utils {

	// MD5加密，32位  
    public static String MD5(String str)  
    {  
    	
    	if (str == null || str.length() == 0) {
			return "";
		}
    	
        MessageDigest md5 = null;  
        try  
        {  
            md5 = MessageDigest.getInstance("MD5"); 
        } catch (Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++)  
        {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++)  
        {  
            int val = ((int)md5Bytes[i])&0xff;  
            if(val < 16)  
            {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    } 

        public static String getExtensionName(String filename) {    
            if ((filename != null) && (filename.length() > 0)) {    
                int dot = filename.lastIndexOf('.');    
                if ((dot >-1) && (dot < (filename.length() - 1))) {    
                    return filename.substring(dot + 1);    
                }    
            }    
            return "xxx";    
        }
        
        public static String getFileNameNoEx(String filename) {    
            if ((filename != null) && (filename.length() > 0)) {    
                int dot = filename.lastIndexOf('.');    
                if ((dot >-1) && (dot < (filename.length()))) {    
                    return filename.substring(0, dot);    
                }    
            }    
            return filename;    
        }   
    
}
