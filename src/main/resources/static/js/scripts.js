var editorType;
$(document).ready(function () {
    let numberOfParts = $('#numberOfParts').val();
    if(numberOfParts > 0)
        $('#createPart').prop('hidden',true);

    var url = window.location.href + '/getParts';
    $("#partsList").load(url);
    $("#getDocx").prop('href', window.location.href + '/getDocx');
    // editor= CKEDITOR.appendTo( 'editor'); можно потом заменить на https://ckeditor.com/docs/ckeditor4/latest/examples/saveajax.html
});

function createParagraph0() {
    try {
        CKEDITOR.instances.editor.destroy();
    } catch (e) {
        console.log(e);
    }

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
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        extraPlugins:'sdt, sdtList, sdtTable, chooseStyle, sdtPicture, sdtRich, radioPart, radioPartChoose, chooseTableStyle',
        removeButtons: 'Source,Save,NewPage,Preview,Print,Templates,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,CopyFormatting,RemoveFormat,Indent,Outdent,CreateDiv,Blockquote,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,EasyImageUpload,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,Format,Font,FontSize,TextColor,BGColor,ShowBlocks,Maximize,About',
    });
    $('#createButtons').prop('hidden',true);
    $('#paragraphForm').prop('hidden', false);
    editorType="Paragraph";
}
function createPicture0() {
    var url = window.location.href + '/getPartGroups';
    $("#partGroupForPicture").load(url);
    $('#paragraphForm').prop('hidden', false);
    $('#createButtons').prop('hidden',true);
    $('#picture').prop('hidden', false);
    editorType="Picture";
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
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ],
        width:'90%',
        extraPlugins:'sdt, sdtList, sdtTable, chooseStyle, sdtPicture, sdtRich, radioPart, radioPartChoose, chooseTableStyle',
        removeButtons: 'Source,Save,NewPage,Preview,Print,Templates,Cut,Copy,Paste,PasteText,PasteFromWord,Redo,Undo,Find,Replace,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Strike,CopyFormatting,RemoveFormat,Indent,Outdent,CreateDiv,Blockquote,Language,BidiRtl,BidiLtr,Link,Unlink,Anchor,EasyImageUpload,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Styles,Format,Font,FontSize,TextColor,BGColor,ShowBlocks,Maximize,About',

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
    var currentStyle = $("#currentStyle").text();
    var currentTableStyle = $("#currentTableStyle").text();
    contentObj.styleId = currentStyle;
    contentObj.tableStyleId = currentTableStyle;
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
    });
}


function postPart() {
    let numberOfParts = $('#numberOfParts').val();
    $('#numberOfParts').val(++numberOfParts);
    var key = parseInt($("#key").text(),10);
    if(editorType!="Picture")
        fireAjaxSubmit(CKEDITOR.instances.editor.getData(),key);
    else
        pictureAjaxSubmit(key);
}

function pictureAjaxSubmit(key) {
    var picturePartGroup = $('#picturePartGroup');
    picturePartGroup.val( $("#selectPartGroup").val());
    var myform = document.getElementById('picture');
    var fd = new FormData(myform);
    key = (key+1);
    let urlString = window.location.href + "/createPicture/"+key;
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        processData: false,
        contentType: false,
        url: urlString,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: fd,
        cache: false
    }).done(function (data) {
        var url = window.location.href + '/getParts';
        $("#partsList").load(url);
        $("#createPart").prop('hidden',true);
        $("#paragraphForm").prop('hidden',true);
        $("#createButtons").prop('hidden',false);
    });
}

function fireAjaxSubmit(content,key) {

    var contentObj={};
    contentObj.editorType = editorType;
    contentObj.content = content;
    contentObj.partGroup ='';
    var currentStyle = $("#currentStyle").text();
    var currentTableStyle = $("#currentTableStyle").text();

    var partGroup = $("#radioPart").text();
    if(partGroup!=""){
        contentObj.partGroup=partGroup;
    }
    contentObj.styleId = currentStyle;
    contentObj.tableStyleId = currentTableStyle;
    contentObj.numberOfPart = key+1;
    let urlString;
    urlString= window.location.href + "/create";
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
        $("#radioPart").text("");
        $("#currentStyle").text("");
        $("#currentTableStyle").text("");
        $("#createPart").prop('hidden',true);
        $("#paragraphForm").prop('hidden',true);
        $("#createButtons").prop('hidden',false);
    });
}

function addPart(key) {
    $("#key").text(key);
    $("#createPart").prop('hidden',false);
}