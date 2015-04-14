/**
 * Created by Richard on 10/04/15.
 */


function formatFilesize(filesize, round){

    // round is an optional parameter - default value is 1
    if(typeof(round) === 'undefined'){round = 1};

    var suffixes = ["B", "KB", "MB", "GB"];
    var suffixIndex = 0;

    while(filesize >= 1024){
        filesize /= 1024;
        suffixIndex++;
    }

    return Math.round(filesize, round) + ' ' + suffixes[suffixIndex];
}

function stringContains(inString, checkArray){

    for(var x in checkArray){
        if(inString.indexOf(checkArray[x]) > -1){
            return true;
        }
    }

    return false;
}

function padLeft(nr, n, str){
    return Array(n-String(nr).length+1).join(str||'0')+nr;
}

function btnText(btn, text){

    btn.find(".text").text(text);
    return btn;
}