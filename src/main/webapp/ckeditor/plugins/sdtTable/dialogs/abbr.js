CKEDITOR.dialog.add( 'insertSdtTable', function ( editor ) {
    return {
        title: 'Table sdt creation',
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
                        label: 'name',
                        'default': '',
                        validate: function() {
                            if ( !this.getValue() ) {
                                api.openMsgDialog( '', 'NumberOfColumns cannot be empty.' );
                                return false;
                            }
                        }
                    },
                    {
                        type: 'text',
                        id: 'numberOfColumns',
                        label: 'numberOfColumns',
                        'default': '',
                        validate: function() {
                            if ( !this.getValue() ) {
                                api.openMsgDialog( '', 'NumberOfColumns cannot be empty.' );
                                return false;
                            }
                        }
                    },
                    {
                        type: 'button',
                        id: 'buttonId',
                        label: 'Create header row',
                        title: 'Create header row',
                        onClick: function() {
                            var i;
                            var dialog = this.getDialog();
                            var numberOfColumns= dialog.getValueOf('tab-basic','numberOfColumns');
                            for (i = 0; i < numberOfColumns; i++) {
                                var p = new CKEDITOR.dom.element( 'INPUT' );
                                p.setAttributes({
                                    'type':'text',
                                    'id': 'cell'+i
                                });
                                p.setStyle('border', '2px solid black');
                                p.setStyle('background-color', '#e5e5e5');
                                var test = dialog.getContentElement('tab-basic','tableRow').getElement();
                                test.append(p);
                            }
                        }
                    },
                    {
                        id: 'tableRow',
                        type: 'html',
                        html:'<div></div>'

                    },
                    // UI elements of the first tab    will be defined here.
                ]
            }
        ],
        onOk: function() {
            // "this" is now a CKEDITOR.dialog object.
            var document = this.getElement().getDocument();
            // document = CKEDITOR.dom.document
            var name = this.getValueOf('tab-basic','name');
            editor.insertText('{{type:TableSdt, name:'+name+'}}');

            var numberOfColumns= this.getValueOf('tab-basic','numberOfColumns');
            var array = [];
            for (i = 0; i < numberOfColumns; i++) {
                array.push(document.getById('cell'+i).getValue());
            }
            // var test = dialog.getContentElement('tab-basic','tableRow').getElement();
            var tableSdt = {};
            tableSdt.name = name;
            tableSdt.headers = array;
            ajaxSubmit(JSON.stringify(tableSdt), 'TableSdt')
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