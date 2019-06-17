CKEDITOR.plugins.add( 'sdt', {
    icons: 'sdt',
    init: function( editor ) {
        editor.addCommand( 'insertSdt', new CKEDITOR.dialogCommand( 'insertSdt' ));
        editor.ui.addButton( 'sdt', {
            label: 'Место для текста',
            command: 'insertSdt',
            toolbar: 'insert,75'
        });
        CKEDITOR.dialog.add( 'insertSdt', this.path + 'dialogs/abbr.js' );
    }
});

