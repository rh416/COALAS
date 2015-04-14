/**
 * Created by Richard on 10/04/15.
 */

var apiErrorRequiredFields = {message : 'Unknown', type : 'Unknown', stack : 'EMPTY'};

function api(uri, preSuccessCallback, successCallback, errorCallback){

    apiWithData(uri, null, preSuccessCallback, successCallback, errorCallback);
}

function apiWithData(uri, data, preSuccessCallback, successCallback, errorCallback){

    $.ajax(uri, {
        cache : false,
        data : data,
        dataType : 'json',
        error : function(xhr, ajaxOptions, thrownError){
            // Format the error from jQuery so that it matches the format of errors from the Java backend - this makes writing errorCallbacks easier
            var errorObj = $.extend(apiErrorRequiredFields, {
                type : xhr.status,
                message : thrownError,
                stack : ajaxOptions.uri
            })
            apiErrorJSON(errorCallback, {message : thrownError, error : errorObj});
        },
        success : function(response){

            if(response.status == "okay"){
                if(preSuccessCallback){
                    preSuccessCallback(response);
                }
                if(successCallback){
                    successCallback(response);
                }
            } else {
                // Check if a detailed error has been returned, if not create one
                if(!response.error){
                    response.error = $.extend(apiErrorRequiredFields, {message : response.message});
                }
                apiErrorJSON(errorCallback, response);
            }
        }
    });
}

function apiErrorJSON(errorCallback, response){

    console.log("API Error: " + response.type + " - " + response.message + " (" + response.stack + ")");

    if(errorCallback){
        errorCallback(response);
    }
}