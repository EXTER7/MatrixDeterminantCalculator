
function normalizeInput(input) {
    var val=input.value.replace(/[^0-9\\/\\-]/g, "");
    if(val.length > 0) {
        var frac = val.split('/');
        if(frac.length == 2) {
            var num = parseInt(frac[0]);
            var den = parseInt(frac[1]);
            if(num.toString() == "NaN" || den.toString() == "NaN") {
                input.value = input.getAttribute("focusValue");
            } else {
                if(den < 0) {
                    num = -num;
                    den = -den;
                }
                input.value = num.toString()+'/'+den.toString();
            }
        } else if(frac.length == 1) {
            this.value = parseInt(frac[0]).toString();
            if(input.value.toString() == "NaN") {
                input.value = input.getAttribute("focusValue");
            }
        } else {
            input.value = input.getAttribute("focusValue");
        }
    } else {
        input.value = input.getAttribute("focusValue");
    }
}
 
function focusInput(input) {
    input.setAttribute("focusValue",input.value);
    input.value = "";
}
 
function createInputCell(i, j, styleClass) {
    var td = document.createElement("td");
    td.className = styleClass;
    var input = document.createElement("input");
    input.type = "text";
    input.value = i == j ? "1" : "0";
    input.id = input.name = 'matrix_' + i + '_' + j;
    input.style = "width: 50px;";
    input.addEventListener("blur",function(e) { normalizeInput(this); });
    input.addEventListener("focus",function(e) { focusInput(this); });
    td.appendChild(input);
    return td;
}

function setSize() {
    var sizeInput = document.getElementById("size");
    var oldSize = parseInt(sizeInput.getAttribute("focusValue")) || 1;
    var size = parseInt(sizeInput.value) || parseInt(sizeInput.getAttribute("focusValue")) || 1;
    if(size < 1) {
        size = 1;
    }
    sizeInput.value = size.toString();
   
    if(size != oldSize) {
        var grid = document.getElementById("matrixGrid");
        while (grid.firstChild) {
           grid.removeChild(grid.firstChild);
        }

        var table = document.createElement("table");
        table.className = "matrixTable";

        var i;
        for(i = 0; i < size; i++) {
            var tr = document.createElement("tr");
            var j;
            for(j = 0; j < size; j++) {
                var td = createInputCell(i,j,"matrixCell");
                tr.appendChild(td);
            }
            table.appendChild(tr);
        }
        grid.appendChild(table);
     }
}

document.addEventListener("DOMContentLoaded", setSize);
