CKEDITOR.plugins.add( 'radioPart', {
    icons: 'radioPart',
    init: function( editor ) {
        editor.addCommand( 'insertRadioPart', new CKEDITOR.dialogCommand( 'insertRadioPart' ));
        editor.ui.addButton( 'radioPart', {
            label: 'Выбрать группу вариантов',
            command: 'insertRadioPart',
            toolbar: 'insert, 100'
        });
        CKEDITOR.dialog.add( 'insertRadioPart', this.path + 'dialogs/abbr.js' );
    }
});

