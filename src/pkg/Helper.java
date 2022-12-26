package pkg;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    
    public static void deleteFilesInDirectory(File directory){
        for(File file: directory.listFiles()){
            if(file.isDirectory()){
                deleteFilesInDirectory(file);
            }
            file.delete();  
        }
    }
    
    public  static String fillWithZeros(int number, int maxNumber){
        String numberStr = String.valueOf(number);
        String maxNumberStr = String.valueOf(maxNumber);
        String retVal = "";
        int zeroCount = maxNumberStr.length()-numberStr.length();
        for(int i = 0; i<zeroCount; i++){
            retVal += "0";
        }
        retVal += numberStr;
        return retVal;
    }
    
    public static String getDateOfToday() {
        return getDateOfToday(new Date());
    }

    public static String getDateOfToday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }
    
    public static void deleteFileContaining(File file, String phrase, boolean deleteContaining) {
        String fileName = file.getName();
        if(fileName.contains(phrase)){
            if(deleteContaining){
                deleteElement(file);
            }
        }else{
            if(!deleteContaining){
                deleteElement(file);
            }
        }
    }
    
    public static void replaceFileNamePhraseWith(File destinationDirectory, File file, String phrase, String replacement) {
        String fileName = file.getName();
        String newFileName = fileName.replace(phrase, replacement);
        File newFile = new File(newFileName);
        if(newFile.exists()){
            newFile.delete();
        }
        file.renameTo(new File(destinationDirectory, newFileName));
    }
    
    public static void deleteElement(File file) {
        if(file.isDirectory()){
            deleteFilesInDirectory(file);
        }
        file.delete();
    }
    
    public static String getFileBase(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        String fileBase = "";
        if (index>0 && index<(fileName.length()-1)){
            fileBase = fileName.substring(0, index);
        }
        return fileBase;
    }
    
    public static String getFileExtension(File file) {
        String fileExtension = "";
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (index > p) {
            fileExtension = fileName.substring(index);
        }
        return fileExtension;
    }
    
    public static void renameElement(File destinationDirectory, File file, String newBase) {        
        String fileExtension = getFileExtension(file);
        String fileName = "";
        fileName += newBase;
        fileName += fileExtension;
        file.renameTo(new File(destinationDirectory, fileName));
    }
    
    public static void createDirectory(File directory) {
        directory.mkdirs();
    }
    
    
}    
