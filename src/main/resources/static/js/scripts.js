var editorType;
$(document).ready(function () {
    CKEDITOR.replace( 'editor');
    var url = window.location.href + '/getParts';
    $("#partsList").load(url);
    $("#getDocx").prop('href', window.location.href + '/getDocx');
    // editor= CKEDITOR.appendTo( 'editor'); можно потом заменить на https://ckeditor.com/docs/ckeditor4/latest/examples/saveajax.html
});

function createParagraph() {
    CKEDITOR.instances.editor.destroy();
    CKEDITOR.replace( 'editor', {
        toolbarGroups: [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'insert', groups: [ 'insert' ] },
            '/',
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        extraPlugins:'sdt, sdtList, sdtTable, chooseStyle',
        removeButtons: 'Source,Save,NewPage,Preview,Print,Templates,Cut,Paste,PasteText,PasteFromWord,Replace,SelectAll,Find,Undo,Redo,Copy,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,CopyFormatting,RemoveFormat,Outdent,Indent,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,EasyImageUpload,Flash,Table,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,Format,Font,FontSize,TextColor,BGColor,Maximize,ShowBlocks,About',

        //removeButtons: 'Source,Save,NewPage,Preview,Print,Templates,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,CopyFormatting,RemoveFormat,Blockquote,CreateDiv,Indent,Outdent,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,Flash,Table,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,BGColor,TextColor,Maximize,ShowBlocks,About,EasyImageUpload',
    });
    $('#createButtons').prop('hidden',true);
    // $('#createTableButton').prop('disabled', true);
    // $('#createPictureButton').prop('disabled', true);
    // $('#createParagraphButton').prop('disabled', true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Paragraph";
    //todo  изменить редактор(запретить контент которого тут не должно быть)
}
function createPicture() {
    CKEDITOR.instances.editor.destroy();
    CKEDITOR.replace( 'editor', {
        toolbarGroups: [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            '/',
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
         removeButtons: 'Source,NewPage,Preview,Print,Templates,Save,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,RemoveFormat,CopyFormatting,Outdent,Indent,Blockquote,CreateDiv,JustifyLeft,JustifyCenter,JustifyRight,JustifyBlock,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,BGColor,TextColor,ShowBlocks,Maximize,About,Subscript,Superscript,NumberedList,BulletedList,Bold,Italic,Underline,Table,Format,Font,FontSize'
    });
    $('#createButtons').prop('hidden',true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Picture";
}
function createTable() {

    CKEDITOR.instances.editor.destroy();
    CKEDITOR.replace( 'editor', {
        toolbarGroups: [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            '/',
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'styles', groups: [ 'styles' ] },
            '/',
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        removeButtons: 'Source,NewPage,Preview,Print,Templates,Save,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,RemoveFormat,CopyFormatting,Outdent,Indent,Blockquote,CreateDiv,JustifyLeft,JustifyCenter,JustifyRight,JustifyBlock,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,BGColor,TextColor,ShowBlocks,Maximize,About,Image,Subscript,Superscript,NumberedList,BulletedList'
    });
    $('#createButtons').prop('hidden',true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Table";
}
function postPart() {
    let numberOfParts = $('#numberOfParts').val()
    $('#numberOfParts').val(++numberOfParts);
    fireAjaxSubmit(CKEDITOR.instances.editor.getData());
}
function fireAjaxSubmit(content) {
    var contentObj={};
    contentObj.editorType = editorType;
    contentObj.content = content;
    var currentStyle = $("#currentStyle").text();
    contentObj.styleId = currentStyle;
    let urlString= window.location.href + "/create";
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: JSON.stringify(contentObj),
        cache: false
    }).done(function (data) {
        var url = window.location.href + '/getParts';
        $("#partsList").load(url);
        $('#createButtons').prop('hidden',false);
        // $('#paragraphEditor').prop('hidden', true);
        // $('#tableEditor').prop('hidden', true);
        // $('#pictureEditor').prop('hidden', true);
        $('#listSdt').prop('hidden', true);
        $('#tableSdt').prop('hidden', true);
        $('#paragraphForm').prop('hidden', true);
    });
}

function deletePart(key) {
    let urlString= window.location.href + "/deletePart";
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: JSON.stringify(key),
        cache: false
    }).done(function (data) {
        var url = window.location.href + '/getParts';
        $("#partsList").load(url);
        // $('#createButtons').prop('hidden',false);
        // // $('#paragraphEditor').prop('hidden', true);
        // // $('#tableEditor').prop('hidden', true);
        // // $('#pictureEditor').prop('hidden', true);
        // $('#listSdt').prop('hidden', true);
        // $('#tableSdt').prop('hidden', true);
        // $('#paragraphForm').prop('hidden', true);
    });
}

function editPart(key) {
    var textarea = key+'textarea';
    try {
        CKEDITOR.instances[textarea].destroy();
    } catch (e) {
        console.log(e);
    }
    CKEDITOR.replace( textarea, {
        toolbarGroups: [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'insert', groups: [ 'insert' ] },
            '/',
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        width:'90%',
        extraPlugins:'sdt, sdtList, sdtTable, chooseStyle',
        removeButtons: 'Source,Save,NewPage,Preview,Print,Templates,Cut,Paste,PasteText,PasteFromWord,Replace,SelectAll,Find,Undo,Redo,Copy,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,CopyFormatting,RemoveFormat,Outdent,Indent,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,EasyImageUpload,Flash,Table,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,Format,Font,FontSize,TextColor,BGColor,Maximize,ShowBlocks,About',

    });
    var savebutton = '#'+key+'saveButton';
    var editButton = '#'+key+'editButton';
    var deleteButton = '#'+key+'deleteButton';
    $(savebutton).prop('hidden',false);
    $(editButton).prop('hidden',true);
    $(deleteButton).prop('hidden',true);
}
function postEditedPart(key){
    let urlString= window.location.href + "/editPart";
    var textarea = key +'textarea';
    var savebutton = '#'+key+'saveButton';
    var editButton = '#'+key+'editButton';
    var deleteButton = '#'+key+'deleteButton';
    var data = CKEDITOR.instances[textarea].getData();
    var contentObj={};
    contentObj.partId = key;
    contentObj.content = data;
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: JSON.stringify(contentObj),
        cache: false
    }).done(function (data) {
        var url = window.location.href + '/getParts';
        $("#partsList").load(url);
        $(savebutton).prop('hidden',true);
        $(editButton).prop('hidden',false);
        $(deleteButton).prop('hidden',false);
        // $('#createButtons').prop('hidden',false);
        // // $('#paragraphEditor').prop('hidden', true);
        // // $('#tableEditor').prop('hidden', true);
        // // $('#pictureEditor').prop('hidden', true);
        // $('#listSdt').prop('hidden', true);
        // $('#tableSdt').prop('hidden', true);
        // $('#paragraphForm').prop('hidden', true);
    });
}