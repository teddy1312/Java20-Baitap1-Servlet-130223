package servlet;

import com.google.gson.Gson;
import config.DataConfig;
import model.RecordModel;
import payload.BasicResponse;
import service.RecordService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "PredictNumberServlet",urlPatterns = {"/play","/records"})
public class PredictNumberServlet extends HttpServlet {
    private String playerName = "";
    private int countQty = 0;
    private int randomNumber = 0;
    private boolean startGame = false;
    private boolean predictCorrect = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RecordService recordService = new RecordService();

        String servletPath = req.getServletPath();
        switch (servletPath){
            case ("/play"):
                playerName = "";
                resetVariable();

                req.setAttribute("play","Bắt đầu");
                req.setAttribute("result","Nhập tên và nhấn bắt đầu để chơi");

                req.getRequestDispatcher("play.jsp").forward(req,resp);
                break;
            case ("/records"):
                if(!recordService.checkExistFile()){
                    DataConfig config = new DataConfig();
                    config.createTextFile();
                }
                List<RecordModel> recordList = recordService.getData();
                req.setAttribute("record_data",arrangeListHighToLow(recordList));

                req.getRequestDispatcher("record.jsp").forward(req,resp);
                break;
            default:
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicResponse basicResponse = new BasicResponse();

        String selectButton = req.getParameter("select");
        if("playButton".equals(selectButton)){
            if(!startGame){
                playerName = req.getParameter("name");
                System.out.println("Player name: "+playerName);

                if("".equals(playerName)){   // Chưa điền tên người chơi
                    basicResponse.setMessage("nameMissing");
                    toJson(resp,basicResponse);
                } else{
                    startGame(resp);
                    System.out.println("Random number: "+randomNumber);
                }
            }else{
                resetVariable();
                basicResponse.setMessage("resetGame");
                toJson(resp,basicResponse);
            }
        } else if("submitButton".equals(selectButton)){
            if(!startGame){
                basicResponse.setMessage("notStartedYet");
                toJson(resp,basicResponse);
            }else{
                submitNumber(req,resp,basicResponse);
            }
        }
    }

    private void submitNumber(HttpServletRequest req,HttpServletResponse resp,BasicResponse basicResponse) throws IOException {
        String predictNum = req.getParameter("number");
        System.out.println("Prediction number: "+predictNum);

        if("".equals(predictNum)){   // Chưa nhập số dự đoán
            basicResponse.setMessage("numberMissing");
            toJson(resp,basicResponse);
        } else{
            int num = Integer.parseInt(predictNum);
            if(num<1 || num>1000){
                basicResponse.setMessage("outRange");
                toJson(resp,basicResponse);
            } else{
                if(!predictCorrect){
                    compareNumber(resp,num);
                }else{
                    basicResponse.setMessage("finished");
                    toJson(resp,basicResponse);
                }
            }
        }
    }

    private void startGame(HttpServletResponse response) throws IOException {
        resetVariable();
        startGame = true;
        randomNumber = createRandomNumber();

        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setMessage("startPlay");
        basicResponse.setData(playerName);
        toJson(response,basicResponse);
    }

    private void compareNumber(HttpServletResponse response, int predictNum) throws IOException {
        BasicResponse basicResponse = new BasicResponse();

        countQty++;
        if(predictNum < randomNumber){
            basicResponse.setMessage("smaller");
        } else if(predictNum > randomNumber){
            basicResponse.setMessage("bigger");
        } else{
            basicResponse.setMessage("bingo");
            predictCorrect = true;
            saveData(playerName,countQty);
        }
        basicResponse.setData(countQty);
        toJson(response,basicResponse);
    }

    private void toJson(HttpServletResponse response,BasicResponse basicResponse) throws IOException {
        Gson gson = new Gson();
        String dataJson = gson.toJson(basicResponse);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        PrintWriter printWriter = response.getWriter();
        printWriter.print(dataJson);
        printWriter.flush();
        printWriter.close();
    }

    private int createRandomNumber(){
        final int MAX = 1000;
        final int MIN = 1;

        return MIN+(int)(Math.random()*(MAX-MIN+1));
    }

    private List<RecordModel> arrangeListHighToLow(List<RecordModel> list){
        for(int i=0;i<list.size()-1;i++){
            for(int j=i+1;j<list.size();j++){
                if(list.get(i).getPredictQty()>list.get(j).getPredictQty()){
                    Collections.swap(list,i,j);
                }
            }
        }

        return list;
    }

    private void resetVariable(){
        randomNumber = 0;
        countQty = 0;
        startGame = false;
        predictCorrect = false;
    }

    private void saveData(String name, int predictQty){
        RecordService recordService = new RecordService();
        boolean duplicateName = false;

        if(!recordService.checkExistFile()){
            DataConfig config = new DataConfig();
            config.createTextFile();
        }

        List<RecordModel> recordList = recordService.getData();
        List<RecordModel> newDataList = new ArrayList<>();
        RecordModel recordModel = new RecordModel();

        for (RecordModel model:recordList) {
            if(name.equals(model.getName())){
                duplicateName = true;

                recordModel.setName(name);
                recordModel.setPredictQty(predictQty);
                newDataList.add(recordModel);
            } else{
                newDataList.add(model);
            }
        }

        if(duplicateName){
            recordService.setData(newDataList,false);
        }else{
            newDataList.clear();
            recordModel.setName(name);
            recordModel.setPredictQty(predictQty);
            newDataList.add(recordModel);

            recordService.setData(newDataList,true);
        }
    }
}
