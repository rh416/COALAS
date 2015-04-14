/**
 * Created by Richard on 13/04/15.
 */

var users = {

    list : null,

    get : function(successCallback, errorCallback){

        api("/users/get", function(response){

                users.list = response.users;
        },
        successCallback,
        errorCallback);
    },

    save : function(successCallback, errorCallback){

        apiWithData("/users/save", {users : JSON.stringify(users.list)}, null, successCallback, errorCallback);
    }

}
