CKEDITOR.dialog.add( 'insertRadioPart', function ( editor ) {
    return {
        title: 'Выбрать группу вариантов',
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
                        html:'<div id="placeForStyle1"></div>'

                    },
                    {
                        type: 'text',
                        id: 'numberOfColumns',
                        label: 'Название новой группы',
                        'default': ''
                    },
                    {
                        type: 'button',
                        id: 'buttonId',
                        label: 'Создать новую группу элементов',
                        title: 'Создать новую группу элементов',
                        onClick: function() {
                            var i;
                            var dialog = this.getDialog();
                            var numberOfColumns= dialog.getValueOf('tab-basic','numberOfColumns');
                            ajaxSubmitRadioPart(numberOfColumns);
                        }
                    },
                    // UI elements of the first tab    will be defined here.
                ]
            }
        ],
        onLoad: function () {
            var url = window.location.href + '/getPartGroups';
            $("#placeForStyle1").load(url);
        }
        ,
        onOk: function() {
            var radioPart = document.getElementById('radioPart');
            radioPart.innerText = $("#selectPartGroup").val();
        }
    };
});
function ajaxSubmitRadioPart(name) {
    let urlString= window.location.href + "/createPartGroup";
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: name,
        cache: false
    }).done(function (data) {
        var url = window.location.href + '/getPartGroups';
        $("#placeForStyle1").load(url);
    });
}
