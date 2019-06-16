CKEDITOR.plugins.add( 'sdtTable', {
    icons: 'sdttable',
    init: function( editor ) {
        editor.addCommand( 'insertSdtTable', new CKEDITOR.dialogCommand( 'insertSdtTable' ));
        editor.ui.addButton( 'sdtTable', {
            label: 'Insert sdtTable',
            command: 'insertSdtTable',
            toolbar: 'insert,60'
        });
        CKEDITOR.dialog.add( 'insertSdtTable', this.path + 'dialogs/abbr.js' );
    }
});

