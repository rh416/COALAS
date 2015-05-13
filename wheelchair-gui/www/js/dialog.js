/**
 * Created by Richard on 10/04/15.
 */

var dialog = {

    currentPositiveCallback : null,
    currentNegativeCallback : null,

    defaults : {
        title : 'Confirm Action',
        message : 'Are you sure',
        buttons : true,
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

        var settings = $.extend({}, dialog.defaults, options);

        var d = $('#dialog-task-confirmation');

        // Update dialog display
        d.find('.modal-title').text(settings.title);
        d.find('.modal-body').html(settings.message);
        d.find('.btn-danger').text(settings.positive.text);
        d.find('.btn-default').text(settings.negative.text);

        var buttons = d.find('.modal-footer');
        if(settings.buttons){
            buttons.show();
        } else {
            buttons.hide();
        }

        $('#dialog-task-confirmation .btn-danger').prop("disabled", false);

        // Store callbacks
        dialog.currentPositiveCallback = settings.positive.callback;
        dialog.currentNegativeCallback = settings.negative.callback;

        // Show dialog
        d.modal({show : true, keyboard : false});
    },

    hide : function(){

        $('#dialog-task-confirmation').modal('hide');
    },

    handlePositiveCallback : function(){

        // Hide the modal
        dialog.hide();

        if(dialog.currentPositiveCallback){
            dialog.currentPositiveCallback();
        }

        // Reset the callback so there's no chance of accidentally calling the wrong function
        dialog.currentPositiveCallback = null;
    },

    handleNegativeCallback : function(){

        // Hide the modal
        dialog.hide();

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