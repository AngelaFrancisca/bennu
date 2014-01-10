define([
    'jquery.bootstrap',
    'bootstrap.notify',
    'backbone',
    'marionette',
    'app',
    'text!templates/MenuForm.html',
    'views/MenuModalAdd',
], function(b,bn, Backbone, Marionette, App, tpl, MenuModalAddView) {
	

	return Backbone.Marionette.ItemView.extend({

		template: tpl,
		
		events : {
	    	"click #confirm" : "createMenu",
	    	"click #delete"  : "deleteMenu",
	    	"click #add-menu" : "addMenu",
	    },
	    
	    modelEvents : {
	    	"change" : "render",
	    },
	    
	    initialize : function () {
	    	this.modalMenuView = new MenuModalAddView();
	    },
	    
	    serializeData: function() {
	    	var model = this.model.toJSON();
	    	model.hasMenu = this.model.menu != undefined && this.model.menu.length > 0;
	    	model.locales = BennuPortal.locales;
	    	model["_mlsLocale"] = function() {
	    		return function(data) {
	    			if (model[data]) {
	    				if (model[data][this.tag]) {
	    					return model[data][this.tag];
	    				}
	    			}
	    			return "";
	    		};
	    	};
	    	return model;
	    },
	    
	    createMenu : function(e) {
	    	var that = this;
	    	$('input').each(function(index, input) {
	    		if ($(input).attr('lang')) {
	    			var mls = that.model.get(input.name) || {};
	    			mls[$(input).attr('lang')] = $(input).val();
	    			that.model.set(input.name, mls);
	    		} else {
	    			that.model.set(input.name, $(input).val());
	    		}
	    	});
	    	
	    	this.model.save(null,{ success : function () {
	    		$('.top-right').notify({
	    		    message: { text: 'Ok!' }
	    		  }).show();
	    	} });
	    },
	    
	    deleteMenu : function(e) {
	    	var that = this;
	    	this.model.destroy({ success : function() {
	    		that.remove();
	    	}});
	    },
	    
	    addMenu: function(e) {
	    	this.modalMenuView.model = App.apps;
	    	this.modalMenuView.parentMenu = this.model;
	    	App.menuLayout.modal.show(this.modalMenuView);
	    	$('#modal-add-menu').modal("show");
	    },
	    
	});
});