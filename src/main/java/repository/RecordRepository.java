package repository;

import model.RecordModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecordRepository {
    final String PATH = "C:\\baitap\\Record.txt";

    public boolean checkFileExistence(){
        File myObj = new File(PATH);
        if(myObj.exists()){
            return true;
        } else{
            return false;
        }
    }

    public List<RecordModel> loadData(){
        List<RecordModel> list = new ArrayList<RecordModel>();

        try {
            FileReader reader = new FileReader(PATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String row;

            while ((row = bufferedReader.readLine()) != null){
                RecordModel obj = new RecordModel();

                String[] data = row.split(",");
                obj.setName(data[0]);
                obj.setPredictQty(Integer.parseInt(data[1]));
                list.add(obj);
            }
            System.out.println("Load successfully");
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred when loading data from .txt file | "+ e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public void writeData(List<RecordModel> list,boolean append){
        try {
            FileWriter writer = new FileWriter(PATH,append);
            for (RecordModel model:list) {
                writer.write(model.getName()+","+model.getPredictQty()+"\n");
            }
            System.out.println("Write successfully");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing data from .txt file | "+ e.getMessage());
            e.printStackTrace();
        }
    }

}
