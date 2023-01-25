package config;

import java.io.File;
import java.io.IOException;

public class DataConfig {
    public boolean createTextFile(){
        File dir = new File("/baitap");
        if (!dir.exists()){
            dir.mkdirs();
        }

        try{
            String path = "C:\\baitap\\Record.txt";
            File recordFile = new File(path);
            if(recordFile.createNewFile()){
                System.out.println("File created: "+recordFile.getName());
            } else{
                System.out.println("File already exists!");
            }
        } catch (IOException e) {
            System.out.println("An error occurred when creating .txt file | " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
