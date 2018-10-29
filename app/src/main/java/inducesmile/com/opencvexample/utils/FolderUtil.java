package inducesmile.com.opencvexample.utils;

import java.io.File;

public class FolderUtil {


    private FolderUtil(){
        //class cannot be instantiated
    }


    public static void createDefaultFolder(String dirPath){
        File directory = new File(dirPath);
        if(!directory.exists()){
           directory.mkdir();
        }
    }


    public boolean checkIfFileExist(String filePath){
        File file = new File(filePath);
        return file.exists();
    }
}
