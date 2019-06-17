CKEDITOR.plugins.add( 'chooseTableStyle', {
    icons: 'chooseTableStyle',
    init: function( editor ) {
        editor.addCommand( 'insertChooseTableStyle', new CKEDITOR.dialogCommand( 'insertChooseTableStyle' ));
        editor.ui.addButton( 'chooseTableStyle', {
            label: 'Выбрать стиль',
            command: 'insertChooseTableStyle',
            toolbar: 'insert, 100'
        });
        CKEDITOR.dialog.add( 'insertChooseTableStyle', this.path + 'dialogs/abbr.js' );
    }
});

