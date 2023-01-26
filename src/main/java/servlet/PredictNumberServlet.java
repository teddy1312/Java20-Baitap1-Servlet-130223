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
    // Meaning of Status Code
    // 501 - Bắt đầu chơi
    // 502 - Chơi lại game
    // 503 - Chưa nhập tên người chơi
    // 504 - Số đoán nhỏ hơn đáp án
    // 505 - Số đoán lớn hơn đáp án
    // 506 - Đoán số chính xác
    // 507 - Chưa nhập số đoán
    // 508 - Số đoán nằm ngoài phạm vi cho phép
    // 509 - Chưa nhấn nút bắt đầu
    // 510 - Đã đoán đúng số rồi đừng nhập số nữa

    final int START_GAME = 501;
    final int RESET_GAME = 502;
    final int NAME_MISSING = 503;
    final int SMALLER_NUM = 504;
    final int BIGGER_NUM = 505;
    final int CORRECTED = 506;
    final int NUMBER_MISSING = 507;
    final int OUT_RANGE = 508;
    final int NOT_START_YET = 509;
    final int ALREADY_CORRECT = 510;

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
        String selectButton = req.getParameter("select");
        if("playButton".equals(selectButton)){
            if(!startGame){
                playerName = req.getParameter("name");
                System.out.println("Player name: "+playerName);

                if("".equals(playerName)){   // Chưa điền tên người chơi
                    toJson(resp,NAME_MISSING);
                } else{
                    startGame(resp);
                    System.out.println("Random number: "+randomNumber);
                }
            }else{
                resetVariable();
                toJson(resp,RESET_GAME);
            }
        } else if("submitButton".equals(selectButton)){
            if(!startGame){
                toJson(resp,NOT_START_YET);
            }else{
                String predictNum = req.getParameter("number");
                System.out.println("Prediction number: "+predictNum);

                submitNumber(resp,predictNum);
            }
        }
    }

    private void submitNumber(HttpServletResponse resp,String predictNum) throws IOException {
        if("".equals(predictNum)){   // Chưa nhập số dự đoán
            toJson(resp,NUMBER_MISSING);
        } else{
            int num = Integer.parseInt(predictNum);
            if(num<1 || num>1000){
                toJson(resp,OUT_RANGE);
            } else{
                if(!predictCorrect){
                    compareNumber(resp,num);
                }else{
                    toJson(resp,ALREADY_CORRECT);
                }
            }
        }
    }

    private void startGame(HttpServletResponse response) throws IOException {
        resetVariable();
        startGame = true;
        randomNumber = createRandomNumber();

        toJson(response,START_GAME);
    }

    private void compareNumber(HttpServletResponse response, int predictNum) throws IOException {
        countQty++;
        if(predictNum < randomNumber){
            toJson(response,SMALLER_NUM);
        } else if(predictNum > randomNumber){
            toJson(response,BIGGER_NUM);
        } else{
            toJson(response,CORRECTED);

            predictCorrect = true;
            saveData(playerName,countQty);
        }
    }

    private void toJson(HttpServletResponse response,int statusCode) throws IOException {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatusCode(statusCode);
        switch (statusCode){
            case START_GAME:
                basicResponse.setMessage("Bắt đầu chơi");
                basicResponse.setData(playerName);
                break;
            case RESET_GAME:
                basicResponse.setMessage("Chơi lại game");
                basicResponse.setData(null);
                break;
            case NAME_MISSING:
                basicResponse.setMessage("Chưa nhập tên người chơi");
                basicResponse.setData(null);
                break;
            case SMALLER_NUM:
                basicResponse.setMessage("Số đoán nhỏ hơn đáp án");
                basicResponse.setData(countQty);
                break;
            case BIGGER_NUM:
                basicResponse.setMessage("Số đoán lớn hơn đáp án");
                basicResponse.setData(countQty);
                break;
            case CORRECTED:
                basicResponse.setMessage("Đoán số chính xác");
                basicResponse.setData(countQty);
                break;
            case NUMBER_MISSING:
                basicResponse.setMessage("Chưa nhập số đoán");
                basicResponse.setData(null);
                break;
            case OUT_RANGE:
                basicResponse.setMessage("Số đoán nằm ngoài phạm vi cho phép");
                basicResponse.setData(null);
                break;
            case NOT_START_YET:
                basicResponse.setMessage("Chưa nhấn nút bắt đầu");
                basicResponse.setData(null);
                break;
            case ALREADY_CORRECT:
                basicResponse.setMessage("Đã đoán đúng số rồi đừng nhập số nữa");
                basicResponse.setData(null);
                break;
            default:
                break;
        }

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
