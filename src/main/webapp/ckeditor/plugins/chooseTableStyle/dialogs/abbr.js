CKEDITOR.dialog.add( 'insertChooseTableStyle', function ( editor ) {
    return {
        title: 'Choose style',
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
                        html:'<div id="placeForStyle"></div>'

                    },
                    // UI elements of the first tab    will be defined here.
                ]
            }
        ],
        onLoad: function () {
            var url = window.location.href + '/getTableStyles';
            $("#placeForStyle").load(url);
        }
        ,
        onOk: function() {
            var style = document.getElementById('currentTableStyle');
            style.innerText = $("#selectedStyle").val();
        }
    };
});
