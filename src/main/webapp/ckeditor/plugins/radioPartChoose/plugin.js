CKEDITOR.plugins.add( 'radioPartChoose', {
    icons: 'radioPartChoose',
    init: function( editor ) {
        editor.addCommand( 'insertChooseRadioPart', new CKEDITOR.dialogCommand( 'insertChooseRadioPart' ));
        editor.ui.addButton( 'radioPartChoose', {
            label: 'Место для вариативного элемента',
            command: 'insertChooseRadioPart',
            toolbar: 'others, 99'
        });
        CKEDITOR.dialog.add( 'insertChooseRadioPart', this.path + 'dialogs/abbr.js' );
    }
});


