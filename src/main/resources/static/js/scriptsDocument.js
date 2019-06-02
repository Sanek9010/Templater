$(document).ready(function () {
    $("#documentForm").submit(function (event) {
        // //stop submit the form, we will post it manually.
        // event.preventDefault();
        // fireAjaxSubmit("documentForm");
    });
});
function fireAjaxSubmit(content) {
    var token = $('#_csrf').attr('content');
    var header = $('#_csrf_header').attr('content');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: window.location.href,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        data: $("#"+content).serialize(),
        cache: false
    }).done(function (data) {
        // var url = window.location.href + '/getParts';
        // $("#partsList").load(url);
        // $('#createButtons').prop('hidden',false);
        // // $('#paragraphEditor').prop('hidden', true);
        // // $('#tableEditor').prop('hidden', true);
        // // $('#pictureEditor').prop('hidden', true);
        // $('#listSdt').prop('hidden', true);
        // $('#tableSdt').prop('hidden', true);
        // $('#paragraphForm').prop('hidden', true);
    });
}

function createTable(placeholderId, placeholderContent) {

}