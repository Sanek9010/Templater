CKEDITOR.plugins.add( 'sdtList', {
    icons: 'sdtlist',
    init: function( editor ) {
        editor.addCommand( 'insertSdtList', new CKEDITOR.dialogCommand( 'insertSdtList' ));
        editor.ui.addButton( 'sdtList', {
            label: 'Insert sdtList',
            command: 'insertSdtList',
            toolbar: 'insert'
        });
        CKEDITOR.dialog.add( 'insertSdtList', this.path + 'dialogs/abbr.js' );
    }
});

