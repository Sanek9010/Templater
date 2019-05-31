CKEDITOR.plugins.add( 'sdt', {
    icons: 'sdt',
    init: function( editor ) {
        editor.addCommand( 'insertSdt', new CKEDITOR.dialogCommand( 'insertSdt' ));
        editor.ui.addButton( 'sdt', {
            label: 'Insert sdt',
            command: 'insertSdt',
            toolbar: 'insert'
        });
        CKEDITOR.dialog.add( 'insertSdt', this.path + 'dialogs/abbr.js' );
    }
});

