/*<![CDATA[*/

$(document).ready(function () {
    onLoadTable();
    $("#getDocx").prop('href', window.location.href + '/getDocx');

});

function addItem(currentElement,inputId,tableFromServer,placeholderId) {
    var lastLi = $(currentElement).parent().parent();
    var newLi = document.createElement('li');
    newLi.setAttribute('class','py-0 border-top border-bottom border-secondary ');

    var newDiv = document.createElement('div');
    newDiv.setAttribute('class','row');

    var newP = document.createElement('p');
    newP.setAttribute('contenteditable',true);
    newP.setAttribute('class','col m-0');
    newDiv.appendChild(newP);

    //тут потом переделать на простое добавление, это все равно не работает
    var buttons = lastLi.children('div').children();
    buttons.each(function (index) {
        var el= this;
        if($(this).is('button')){
            let attr = this.getAttribute('onclick');
            let signature = 'addItem(this,'+inputId+','+tableFromServer+','+placeholderId+')';
            if(attr === signature)
                newDiv.appendChild(this);
            else{
                let button = $(this).clone().get()[0];
                newDiv.appendChild(button);
            }
        }
    });
    newLi.appendChild(newDiv);
    lastLi.after(newLi);
    save(inputId,tableFromServer,placeholderId);
}
function addInnerItem(currentElement,inputId,tableFromServer,placeholderId) {
    var closestLi = currentElement.closest('li');
    var newOl = document.createElement('ol');
    var newLi = $(closestLi).clone().get()[0];
    var a = newLi.getElementsByTagName('p')[0].innerHTML;
    newLi.getElementsByTagName('p')[0].innerHTML ="";
    newOl.appendChild(newLi);
    closestLi.appendChild(newOl);
    save(inputId,tableFromServer,placeholderId);
}

function deleteItem(currentElement,inputId,tableFromServer,placeholderId) {
    var closestLi = currentElement.closest('li');
    closestLi.parentNode.removeChild(closestLi);
    save(inputId,tableFromServer,placeholderId);
}

function onLoadTable(){
    let numberOfPlaceholders = $("#numberOfPlaceholders").attr('data');
    let j = 0;
    do {
        let i;
        try {
            let filled = (document.getElementById('placeholders'+j+'.filled').value === 'true');
            if(!filled){
                var table = document.getElementById('Table' + j);
                let headers = table.getAttribute('data1');
                headers = JSON.parse(headers);
                let row = table.insertRow(0);
                for (i = 0; i < headers.length; i++) {
                    row.insertCell(i).innerText = headers[i];

                }
                let row2 = table.insertRow(-1);
                row2.setAttribute('class','tr_clone');
                for (i = 0; i < headers.length; i++) {
                    var cell = row2.insertCell(i);
                    cell.setAttribute('contenteditable','true');
                    cell.setAttribute('style','white-space:normal');
                }
            }
        } catch (e) {
        }
        j++;
    }while (j<=numberOfPlaceholders);
}

function addRow(numberOfTable,inputId,tableFromServer,placeholderId) {
    var selector = '#'+numberOfTable;
    let table = $(selector);
    let tableBody = table.find('tbody');
    let trLast = tableBody.find('tr:last');
    let trNew = trLast.clone();
    trNew.find('td').each(function (index) {
       this.innerText='';
    });
    trLast.after(trNew);
    save(inputId,tableFromServer,placeholderId);
}
function deleteRow(numberOfTable,inputId,tableFromServer,placeholderId) {
    var selector = '#'+numberOfTable;
    let table = $(selector);
    let tableBody = table.find('tbody');
    let trLast = tableBody.find('tr:last');
    trLast.parentNode.removeChild(trLast);
    save(inputId,tableFromServer,placeholderId);
}

function save(inputId,tableFromServer,placeholderId) {
    let table = document.getElementById(tableFromServer);
    let filled = document.getElementById(placeholderId+'.filled');
    filled.value = true;
    let inputContent = document.getElementById(inputId);
    inputContent.value = table.outerHTML;
}

function savePicture(inputId, index, input) {
    if(input.files && input.files[0]){
        var reader = new FileReader();
        var picturePlace = '#Picture'+index;
        reader.onload = function (e) {
            $(picturePlace)
                .attr('src', e.target.result)
                .width(150)
                .height(200);
            $(inputId).val(e.target.result);
        };
        reader.readAsDataURL(input.files[0]);

        // var reader2 = new FileReader();
        // reader2.onload = function (e) {
        //     var arrayBuffer = this.result,
        //         array = new Uint8Array(arrayBuffer),
        //         binaryString = String.fromCharCode.apply(null, array);
        //     let inputContent = document.getElementById(inputId);
        //     inputContent.value = binaryString;
        // };
        // reader2.readAsArrayBuffer(input.files[0]);


    }
}

/*]]>*/