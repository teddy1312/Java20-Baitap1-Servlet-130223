package service;

import model.RecordModel;
import repository.RecordRepository;

import java.util.List;

public class RecordService {
    RecordRepository recordRepository = new RecordRepository();

    public boolean checkExistFile(){
        return recordRepository.checkFileExistence();
    }

    public List<RecordModel> getData(){
        return recordRepository.loadData();
    }

    public void setData(List<RecordModel> list,boolean append){
        recordRepository.writeData(list,append);
    }


}
