/*<![CDATA[*/

$(document).ready(function () {
    onLoadTable();
});

function addItem(parent) {
    var list = document.getElementById(parent);
    var item = document.createElement('LI');
    var buttonAddItem = document.getElementById(parent+'buttonRow');
    item.setAttribute('contenteditable','true');
    item.setAttribute('class','list-group-item py-1');
    list.insertBefore(item, buttonAddItem);

}

function addInnerItem(parent,lvl, count) {
    var list = document.getElementById(parent+lvl.toString()+count);
    var itemOl = document.createElement('list-group');
    var parentCount = list.getAttribute('data');
    itemOl.setAttribute('id',parent+(lvl+1)+parentCount);
    itemOl.setAttribute('data', '0');
    itemOl.setAttribute('class','list-group-item');
    var itemLi = document.createElement('li');
    itemLi.setAttribute('contenteditable','true');
    itemLi.setAttribute('class','list-group-item py-1');
    itemOl.prepend(itemLi);

    var buttonRow = document.createElement('div');
    buttonRow.setAttribute('class','row');
    buttonRow.setAttribute('id',parent+(lvl+1)+parentCount+'buttonRow');

    var buttonLi = document.createElement('button');
    buttonLi.setAttribute('type','button');
    buttonLi.setAttribute('class','btn btn-sm btn-primary mt-1 ml-3');
    buttonLi.setAttribute('onclick','addItem(\''+parent+(lvl+1).toString()+parentCount+'\')');
    buttonLi.innerText = 'Add item';
    buttonRow.appendChild(buttonLi);

    var buttonOL = document.createElement('button');
    buttonOL.setAttribute('type','button');
    buttonOL.setAttribute('class','btn btn-sm btn-primary mt-1 ml-3');
    buttonOL.setAttribute('onclick','addInnerItem(\''+parent+'\','+(lvl+1)+','+parentCount+')');
    buttonOL.innerText = 'Add inner item';

    buttonRow.appendChild(buttonOL);
    itemOl.appendChild(buttonRow);


    var buttonAddItem = document.getElementById(parent+lvl.toString()+count+'buttonRow');
    list.setAttribute('data',(++parentCount).toString());
    list.insertBefore(itemOl, buttonAddItem);
}

function onLoadTable(){
    let numberOfPlaceholders = $("#numberOfPlaceholders").attr('data');
    let j = 0;
    do {
        let i;
        try {
            var table = document.getElementById('Table' + j);
            let headers = table.getAttribute('data1');
            headers = JSON.parse(headers);
            let row = table.insertRow(0);
            for (i = 0; i < headers.length; i++) {
                row.insertCell(i).innerText = headers[i];

            }
        } catch (e) {
        }
        j++;
    }while (j<=numberOfPlaceholders);
}

function addRow(headersFromServer,tableFromServer) {
    let headers = headersFromServer;
    let i;
    headers = JSON.parse(headers);
    let table = document.getElementById(tableFromServer);
    let row = table.insertRow(-1);
    for (i = 0; i < headers.length; i++) {
        var cell = row.insertCell(i);
        cell.setAttribute('contenteditable','true');
        cell.setAttribute('style','white-space:normal');
    }
}
function save(inputId,tableFromServer) {
    let table = document.getElementById(tableFromServer);
    let inputContent = document.getElementById(inputId);
    inputContent.value = table.outerHTML;
}
/*]]>*/