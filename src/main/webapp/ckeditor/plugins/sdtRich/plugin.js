CKEDITOR.plugins.add( 'sdtRich', {
    icons: 'sdtrich',
    init: function( editor ) {
        editor.addCommand( 'insertSdtRich', new CKEDITOR.dialogCommand( 'insertSdtRich' ));
        editor.ui.addButton( 'sdtRich', {
            label: 'Место для раздела',
            command: 'insertSdtRich',
            toolbar: 'insert,10'
        });
        CKEDITOR.dialog.add( 'insertSdtRich', this.path + 'dialogs/abbr.js' );
    }
});

