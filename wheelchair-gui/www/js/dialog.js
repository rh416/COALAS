/**
 * Created by Richard on 10/04/15.
 */

var dialog = {

    currentPositiveCallback : null,
    currentNegativeCallback : null,

    defaults : {
        title : 'Confirm Action',
        message : 'Are you sure',
        positive : {
            text : 'Yes',
            callback : function(){}
        },
        negative : {
            text : 'No',
            callback : function(){}
        }
    },

    show : function(options){

        var settings = $.extend(dialog.defaults, options);

        var d = $('#dialog-task-confirmation');

        // Update dialog display
        d.find('.modal-title').text(settings.title);
        d.find('.modal-body').html(settings.message);
        d.find('.btn-danger').text(settings.positive.text);
        d.find('.btn-default').text(settings.negative.text);

        // Store callbacks
        dialog.currentPositiveCallback = settings.positive.callback;
        dialog.currentNegativeCallback = settings.negative.callback;

        // Show dialog
        d.modal({show : true, keyboard : false});
    },

    handlePositiveCallback : function(){

        // Hide the modal
        $('#dialog-task-confirmation').modal('hide');

        if(dialog.currentPositiveCallback){
            dialog.currentPositiveCallback();
        }

        // Reset the callback so there's no chance of accidentally calling the wrong function
        dialog.currentPositiveCallback = null;
    },

    handleNegativeCallback : function(){

        // Hide the modal
        $('#dialog-task-confirmation').modal('hide');

        if(dialog.currentNegativeCallback){
            dialog.currentNegativeCallback();
        }

        // Reset the callback so there's no chance of accidentally calling the wrong function
        dialog.currentNegativeCallback = null;
    }
}

// Setup the callback handlers
var d = $('#dialog-task-confirmation');
d.find('.btn-danger').click(dialog.handlePositiveCallback);
d.find('.btn-default').click(dialog.handleNegativeCallback);