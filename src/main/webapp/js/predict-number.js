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

$(document).ready(function (){
    $("#btn-play").click(function (e){
        e.preventDefault()
        const This = $(this)

        const name = $('#enter-name').val()
        const select = 'playButton'

        $.ajax({
            method: 'POST',
            url: `http://localhost:8080/play`,
            data:{
                'name': name,
                'select': select
            }
        }).done(function (data){
            if(data.statusCode == 501){
                This.html('Chơi lại')
                $("#result-display").html('Mời '+data.data+' nhập số dự đoán')
            } else if(data.statusCode == 502){
                This.html('Bắt đầu')
                $("#result-display").html('Nhập tên và nhấn bắt đầu để chơi')
                alert('Game đã được reset')
            } else if(data.statusCode == 503){
                alert('Bạn chưa nhập tên người chơi')
            }
        })
    })

    $("#btn-submit").click(function (e){
        e.preventDefault()
        const number = $('#enter-number').val()
        const select = 'submitButton'

        $.ajax({
            method: 'POST',
            url: `http://localhost:8080/play`,
            data:{
                'number': number,
                'select': select
            }
        }).done(function (data){
            if(data.statusCode == 504) {
                $("#result-display").html('Số vừa đoán nhỏ hơn đáp án (Số lần đoán: '+data.data+')')
            } else if(data.statusCode == 505) {
                $("#result-display").html('Số vừa đoán lớn hơn đáp án (Số lần đoán: '+data.data+')')
            } else if(data.statusCode == 506) {
                $("#result-display").html('Chúc mừng bạn đã đoán đúng sau '+data.data+' lần đoán')
            } else if(data.statusCode == 507){
                alert('Bạn chưa nhập số dự đoán')
            } else if(data.statusCode == 508){
                alert('Số nhập phải nằm trong phạm vi 1-1000')
            } else if(data.statusCode == 509){
                alert('Bạn chưa nhấn nút bắt đầu')
            } else if(data.statusCode == 510){
                alert('Bạn đã đoán đúng số rồi')
            }
        })
    })
})