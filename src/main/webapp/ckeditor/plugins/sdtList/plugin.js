CKEDITOR.plugins.add( 'sdtList', {
    icons: 'sdtlist',
    init: function( editor ) {
        editor.addCommand( 'insertSdtList', new CKEDITOR.dialogCommand( 'insertSdtList' ));
        editor.ui.addButton( 'sdtList', {
            label: 'Место для списка',
            command: 'insertSdtList',
            toolbar: 'others, 70'
        });
        CKEDITOR.dialog.add( 'insertSdtList', this.path + 'dialogs/abbr.js' );
    }
});

