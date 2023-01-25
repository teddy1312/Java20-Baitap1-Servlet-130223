<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>

<div class="container">
  <div class="row mt-5">
    <div class="col-md-5 m-auto mt-5">
      <h3 class="text-center">GAME ĐOÁN SỐ</h3>
      <div class="p-4 border mt-4">
        <form method="post">
            <div class="form-group">
              <label>Nhập tên người chơi:</label>
              <input id="enter-name" type="text" class="form-control" minlength="1" maxlength="16">
            </div>

            <button id="btn-play" type="submit" class="btn btn-primary">${play}</button>
            </br></br>

            <div class="form-group">
                <label>Nhập số dự đoán:</label>
                <input id="enter-number" type="number" class="form-control" min="1" max="1000">
            </div>

            <button id="btn-submit" type="submit" class="btn btn-primary">Gửi đáp án</button>
            </br>

            <div class="form-group">
              <label>Kết quả:</label>
              <h4 id="result-display">${result}</h4>
            </div>


            <div class="text-right">
                <c:set var = "path" value = "http://localhost:${pageContext.request.serverPort}/records" />
                <a href="${path}" class="btn btn-sm btn-success">Xem thứ hạng</a>
            </div>

        </form>
      </div>
      </div>
  </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<script src="js/predict-number.js"></script>
</body>
</html>
