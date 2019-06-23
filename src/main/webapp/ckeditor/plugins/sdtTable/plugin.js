CKEDITOR.plugins.add( 'sdtTable', {
    icons: 'sdttable',
    init: function( editor ) {
        editor.addCommand( 'insertSdtTable', new CKEDITOR.dialogCommand( 'insertSdtTable' ));
        editor.ui.addButton( 'sdtTable', {
            label: 'Место для таблицы',
            command: 'insertSdtTable',
            toolbar: 'others,60'
        });
        CKEDITOR.dialog.add( 'insertSdtTable', this.path + 'dialogs/abbr.js' );
    }
});

