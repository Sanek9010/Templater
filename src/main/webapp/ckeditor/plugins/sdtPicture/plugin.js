CKEDITOR.plugins.add( 'sdtPicture', {
    icons: 'sdtpicture',
    init: function( editor ) {
        editor.addCommand( 'insertSdtPicture', new CKEDITOR.dialogCommand( 'insertSdtPicture' ));
        editor.ui.addButton( 'sdtPicture', {
            label: 'Insert sdtPicture',
            command: 'insertSdtPicture',
            toolbar: 'insert'
        });
        CKEDITOR.dialog.add( 'insertSdtPicture', this.path + 'dialogs/abbr.js' );
    }
});

