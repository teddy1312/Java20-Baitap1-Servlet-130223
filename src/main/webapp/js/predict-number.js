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
            if(data.message === 'startPlay'){
                This.html('Chơi lại')
                $("#result-display").html('Mời '+data.data+' nhập số dự đoán')
            }else if(data.message ==='nameMissing'){
                alert('Bạn chưa nhập tên người chơi')
            }else if(data.message ==='resetGame'){
                This.html('Bắt đầu')
                $("#result-display").html('Nhập tên và nhấn bắt đầu để chơi')
                alert('Game đã được reset')
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
            if(data.message === 'smaller') {
                $("#result-display").html('Số vừa đoán nhỏ hơn đáp án (Số lần đoán: '+data.data+')')
            } else if(data.message === 'bigger') {
                $("#result-display").html('Số vừa đoán lớn hơn đáp án (Số lần đoán: '+data.data+')')
            } else if(data.message === 'bingo') {
                $("#result-display").html('Chúc mừng bạn đã đoán đúng sau '+data.data+' lần đoán')
            } else if(data.message ==='numberMissing'){
                alert('Bạn chưa nhập số dự đoán')
            } else if(data.message ==='outRange'){
                alert('Số nhập phải nằm trong phạm vi 1-1000')
            } else if(data.message ==='notStartedYet'){
                alert('Bạn chưa nhấn nút bắt đầu')
            } else if(data.message ==='finished'){
                alert('Bạn đã đoán đúng số')
            }
        })
    })
})