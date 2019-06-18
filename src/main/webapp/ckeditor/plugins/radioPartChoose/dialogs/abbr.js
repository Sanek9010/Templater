CKEDITOR.dialog.add( 'insertChooseRadioPart', function ( editor ) {
    return {
        title: 'Место для вариативного элемента',
        minWidth: 400,
        minHeight: 20,

        contents: [
            {
                id: 'tab-basic',
                label: 'Settings',
                elements: [
                    {
                        id: 'tableRow',
                        type: 'html',
                        html:'<div id="placeForStyle2"></div>'

                    },
                    // UI elements of the first tab    will be defined here.
                ]
            }
        ],
        onLoad: function () {
            var url = window.location.href + '/getPartGroups';
            $("#placeForStyle2").load(url);
        }
        ,
        onOk: function() {
            var partGroup = $("#selectPartGroup");
            var selectedGroup = partGroup.children("option:selected");
            editor.insertText('{{type:PartGroup, name:'+selectedGroup.text()+'}}');
            ajaxSubmit(selectedGroup.text(), 'PartGroup')
        }
    };
});
function ajaxSubmit(content, editorType) {
    var contentObj={};
    contentObj.editorType = editorType;
    contentObj.content = content;
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
