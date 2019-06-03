/*<![CDATA[*/

$(document).ready(function () {
    onLoadTable();
    $("#getDocx").prop('href', window.location.href + '/getDocx');

});

function addItem(currentElement) {
    var lastLi = $(currentElement).parent().parent();
    var newLi = document.createElement('li');
    newLi.setAttribute('class','py-0 border-top border-bottom border-secondary ');

    var newDiv = document.createElement('div');
    newDiv.setAttribute('class','row');

    var newP = document.createElement('p');
    newP.setAttribute('contenteditable',true);
    newP.setAttribute('class','col m-0');
    newDiv.appendChild(newP);

    var buttons = lastLi.children('div').children();
    buttons.each(function (index) {
        var el= this;
        if($(this).is('button')){
            let attr = this.getAttribute('onclick');
            if(attr === 'addItem(this)')
                newDiv.appendChild(this);
            else{
                let button = $(this).clone().get()[0];
                newDiv.appendChild(button);
            }
        }
    });
    newLi.appendChild(newDiv);
    lastLi.after(newLi);
}
function addInnerItem(currentElement) {
    var closestLi = currentElement.closest('li');
    var newOl = document.createElement('ol');

    var newLi = document.createElement('li');
    newLi.setAttribute('class','py-0 border-top border-bottom border-secondary ');
    var newDiv = document.createElement('div');
    newDiv.setAttribute('class','row');

    var newP = document.createElement('p');
    newP.setAttribute('contenteditable',true);
    newP.setAttribute('class','col m-0');
    newDiv.appendChild(newP);

    var buttonLi = document.createElement('button');
    buttonLi.setAttribute('type','button');
    buttonLi.setAttribute('class','btn btn-secondary btn-sm col-auto');
    buttonLi.setAttribute('onclick','addItem(this)');
    buttonLi.innerText = 'Add item';

    var buttonOl = document.createElement('button');
    buttonOl.setAttribute('type','button');
    buttonOl.setAttribute('class','btn btn-secondary btn-sm col-auto');
    buttonOl.setAttribute('onclick','addInnerItem(this)');
    buttonOl.innerText = 'Add inner item';


    newDiv.appendChild(buttonLi);
    newDiv.appendChild(buttonOl);
    newLi.appendChild(newDiv);
    newOl.appendChild(newLi);
    closestLi.appendChild(newOl);
}

// function addItem(parent) {
//     var list = document.getElementById(parent);
//     var item = document.createElement('LI');
//     var buttonAddItem = document.getElementById(parent+'buttonRow');
//     item.setAttribute('id',parent)
//     item.setAttribute('contenteditable','true');
//     item.setAttribute('class','list-group-item py-1');
//     list.insertBefore(item, buttonAddItem);
//
// }

// function addInnerItem(parent,lvl, count) {
//     var list = document.getElementById(parent+lvl.toString()+count);
//     var patentSelector= '#' + parent+ lvl.toString()+
//     var parent = $(#)
//     var itemOl = document.createElement('list-group');
//     var parentCount = list.getAttribute('data');
//     itemOl.setAttribute('id',parent+(lvl+1)+parentCount);
//     itemOl.setAttribute('data', '0');
//     itemOl.setAttribute('class','list-group-item');
//     var itemLi = document.createElement('li');
//     itemLi.setAttribute('contenteditable','true');
//     itemLi.setAttribute('class','list-group-item py-1');
//     itemOl.prepend(itemLi);
//
//     var buttonRow = document.createElement('div');
//     buttonRow.setAttribute('class','row');
//     buttonRow.setAttribute('id',parent+(lvl+1)+parentCount+'buttonRow');
//
//     var buttonLi = document.createElement('button');
//     buttonLi.setAttribute('type','button');
//     buttonLi.setAttribute('class','btn btn-sm btn-primary mt-1 ml-3');
//     buttonLi.setAttribute('onclick','addItem(\''+parent+(lvl+1).toString()+parentCount+'\')');
//     buttonLi.innerText = 'Add item';
//     buttonRow.appendChild(buttonLi);
//
//     var buttonOL = document.createElement('button');
//     buttonOL.setAttribute('type','button');
//     buttonOL.setAttribute('class','btn btn-sm btn-primary mt-1 ml-3');
//     buttonOL.setAttribute('onclick','addInnerItem(\''+parent+'\','+(lvl+1)+','+parentCount+')');
//     buttonOL.innerText = 'Add inner item';
//
//     buttonRow.appendChild(buttonOL);
//     itemOl.appendChild(buttonRow);
//
//
//     var buttonAddItem = document.getElementById(parent+lvl.toString()+count+'buttonRow');
//     list.setAttribute('data',(++parentCount).toString());
//     list.insertBefore(itemOl, buttonAddItem);
// }

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

function addRow(numberOfTable) {

    // let headers = headersFromServer;
    // let i;
    // headers = JSON.parse(headers);
    var selector = '#'+numberOfTable;
    let table = $(selector);
    let tableBody = table.find('tbody');
    let trLast = tableBody.find('tr:last');
    let trNew = trLast.clone();
    trNew.find('td').each(function (index) {
       this.innerText='';
    });
    trLast.after(trNew);
    // let row = table.insertRow(-1);
    // for (i = 0; i < headers.length; i++) {
    //     var cell = row.insertCell(i);
    //     cell.setAttribute('contenteditable','true');
    //     cell.setAttribute('style','white-space:normal');
    // }


}
function save(inputId,tableFromServer,placeholderId) {
    let table = document.getElementById(tableFromServer);
    let filled = document.getElementById(placeholderId+'.filled');
    filled.value = true;
    let inputContent = document.getElementById(inputId);
    inputContent.value = table.outerHTML;
}
/*]]>*/