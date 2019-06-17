CKEDITOR.plugins.add( 'sdtPicture', {
    icons: 'sdtpicture',
    init: function( editor ) {
        editor.addCommand( 'insertSdtPicture', new CKEDITOR.dialogCommand( 'insertSdtPicture' ));
        editor.ui.addButton( 'sdtPicture', {
            label: 'Место для изображения',
            command: 'insertSdtPicture',
            toolbar: 'insert,65'
        });
        CKEDITOR.dialog.add( 'insertSdtPicture', this.path + 'dialogs/abbr.js' );
    }
});

