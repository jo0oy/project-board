function sendFile(file, el) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    let form_data = new FormData();
    form_data.append('file', file);
    $.ajax({
        type: 'POST',
        url: '/api/v1/files',
        cache: false,
        contentType: false,
        enctype: 'multipart/form-data',
        beforeSend : function(xhr)
        {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
            xhr.setRequestHeader(header, token);
        },
        processData: false,
        data: form_data,
        success: function (response) {
            $(el).summernote('insertImage', response, function ($image) {
                $image.css('width', '30%');
            });
        }
    });
}

function deleteFile(src) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    let index = src.lastIndexOf('/');
    let filename = src.substring(index + 1);

    console.log('filename=' + filename);

    $.ajax({
        type: 'DELETE',
        url: '/api/v1/files/' + filename,
        cache: false,
        async: false,
        beforeSend : function(xhr)
        {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
            xhr.setRequestHeader(header, token);
        },
        success: function () {
            console.log('이미지가 정상적으로 삭제되었습니다.');
        }
    });
}
