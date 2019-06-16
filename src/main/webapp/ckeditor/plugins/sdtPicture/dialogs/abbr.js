CKEDITOR.dialog.add( 'insertSdtPicture', function ( editor ) {
    return {
        title: 'Picture sdt creation',
        minWidth: 400,
        minHeight: 200,
        contents: [
            {
                id: 'tab-basic',
                label: 'Settings',
                elements: [
                    {
                        type: 'text',
                        id: 'name',
                        label: 'Name',
                        validate: CKEDITOR.dialog.validate.notEmpty( "Name field cannot be empty." )
                    }
                    // UI elements of the first tab    will be defined here.
                ]
            }
        ],
        onOk: function() {
            var name = this.getValueOf('tab-basic','name');
            editor.insertText('{{type:PictureSdt, name:'+name+'}}');
            ajaxSubmit(name, 'PictureSdt')
        }
    };
});
function ajaxSubmit(content, editorType) {
    let numberOfParts = $('#numberOfParts').val();
    $('#numberOfParts').val(++numberOfParts);
    var key = parseInt($("#key").text(),10);
    var contentObj={};
    contentObj.editorType = editorType;
    contentObj.content = content;
    contentObj.numberOfPart = key+1;
    let urlString= window.location.href + "/create";
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: JSON.stringify(contentObj),
        cache: false
    }).done(function (data) {
        // var url = window.location.href + '/getParts';
        // $("#partsList").load(url);
        // $('#createButtons').prop('hidden',false);
        // // $('#paragraphEditor').prop('hidden', true);
        // // $('#tableEditor').prop('hidden', true);
        // // $('#pictureEditor').prop('hidden', true);
        // // $('#listSdt').prop('hidden', true);
        // // $('#tableSdt').prop('hidden', true);
        // $('#paragraphForm').prop('hidden', true);
    });
}
