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
            '/',
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        removeButtons: 'Source,NewPage,Preview,Print,Templates,Save,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,RemoveFormat,CopyFormatting,Outdent,Indent,Blockquote,CreateDiv,JustifyLeft,JustifyCenter,JustifyRight,JustifyBlock,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,BGColor,TextColor,ShowBlocks,Maximize,About,Image,Table'
    });
    $('#createTableButton').prop('disabled', true);
    $('#createPictureButton').prop('disabled', true);
    $('#createParagraphButton').prop('disabled', true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Paragraph";
    //todo выключить кнопку, изменить редактор, добавить подобные редакторы для других кнопок, реализовать сохранение
    // var url = window.location.href + "/paragraph";
    // $("#contentBlock").load(url);
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
    $('#createTableButton').prop('disabled', true);
    $('#createPictureButton').prop('disabled', true);
    $('#createParagraphButton').prop('disabled', true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Picture";
    //todo выключить кнопку, изменить редактор, добавить подобные редакторы для других кнопок, реализовать сохранение
    // var url = window.location.href + "/paragraph";
    // $("#contentBlock").load(url);
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
    $('#createTableButton').prop('disabled', true);
    $('#createPictureButton').prop('disabled', true);
    $('#createParagraphButton').prop('disabled', true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Table";
    //todo выключить кнопку, изменить редактор, добавить подобные редакторы для других кнопок, реализовать сохранение
    // var url = window.location.href + "/paragraph";
    // $("#contentBlock").load(url);
}
function postPart() {
    fireAjaxSubmit();
    // $(document).ready(function () {
    //     $("#paragraphForm").submit(function (event) {
    //         event.preventDefault();
    //         fireAjaxSubmit();
    //     })
    // })
}
function fireAjaxSubmit() {
    var contentObj={};
    contentObj.editorType = editorType;
    contentObj.content = CKEDITOR.instances.editor.getData();
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
        $('#createTableButton').prop('disabled', false);
        $('#createPictureButton').prop('disabled', false);
        $('#createParagraphButton').prop('disabled', false);
        $('#paragraphEditor').prop('hidden', true);
        $('#tableEditor').prop('hidden', true);
        $('#pictureEditor').prop('hidden', true);
        $('#paragraphForm').prop('hidden', true);
    });
}