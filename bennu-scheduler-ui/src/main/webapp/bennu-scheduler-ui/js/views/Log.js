define([
    'jquery',
    'backbone',
    'marionette',
    'moment',
    'app',
    'views/AddCustomTask',
    'text!templates/Log.html'
], function($, Backbone, Marionette, moment, App, AddCustomTaskView, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        
        modelEvents: {
        	"change" : "render"
        },
        
        events: {
        	"click .btn-load-code": "loadCode",
        },
        
        onClose: function() {
        	if (this.logging != 0) {
        		console.log("Clear " + this.logging);
        		clearInterval(this.logging);
        	}
        },
        
        onShow: function() {
        	if (this.model.get("javaCode") != undefined) {
            	require(['codemirror'], function(CodeMirror) {
            		require(['codemirror-clike'], function(CLike) {
            			var codeArea = $('#code')[0];
            			CodeMirror.fromTextArea(codeArea, {lineNumbers : true, mode:"text/x-java", theme:"eclipse", viewportMargin: Infinity, readOnly: "nocursor"});
            		});
    			});
        	}
        },
        
        url: function() {
        	var url = "../api/bennu-scheduler/";
        	if (this.model.get("javaCode") == undefined) {
        		return url + "log/";
        	} 
        	return url + "custom/"; 
        },
        
        refreshLog: function() {
        	var id = this.model.get("id");
        	$.get( this.url() + "cat/" + id, null, function(log) {
				$("#logs").html(log);
				});
        },
        
        loadCode: function() {
        	var model = this.model;
        	App.page.show(new AddCustomTaskView({model : model}));
        },
        
        initialize: function() {
        	var that = this;
        	this.logging = 0;
        	this.refreshLog();
        	
        	if (this.model.get("end") === undefined) {
        		this.logging = setInterval(
        				function() {
        					that.refreshLog();
        				}, 3000);
        		console.log("setInterval: " + this.logging);
        	}
        },
        
        serializeData: function() {
        	var data = this.model.toJSON();
        	var that = this;
        	if (data.files) {
        		files = new Array();
            	$(data.files).each(function(i,e) {
            		var file = {};
            		file.url = that.url() + data.id + "/" + e;
            		file.name = e;
            		files.push(file);
            	});
            	data.files = files;
        	}
        	
        	$(data).each(function(i,e) {
        		e.start = moment(e.start).format("MMMM Do YYYY, h:mm:ss a");
        		if (e.end) {
        			e.end = moment(e.end).format("MMMM Do YYYY, h:mm:ss a");
        			e.finished = true;
        		} else {
        			e.end = "N/A";
        			e.finished = false;
        		}
        	});
        	
        	return data;
        }
        
        
        
    });
});