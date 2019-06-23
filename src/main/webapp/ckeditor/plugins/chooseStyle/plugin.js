CKEDITOR.plugins.add( 'chooseStyle', {
    icons: 'chooseStyle',
    init: function( editor ) {
        editor.addCommand( 'insertChooseStyle', new CKEDITOR.dialogCommand( 'insertChooseStyle' ));
        editor.ui.addButton( 'chooseStyle', {
            label: 'Выбрать стиль',
            command: 'insertChooseStyle',
            toolbar: 'styles, 100'
        });
        CKEDITOR.dialog.add( 'insertChooseStyle', this.path + 'dialogs/abbr.js' );
    }
});

