(function () {

    window.Bennu = window.Bennu || {};

    if(window.BennuPortal) {
        $.extend(window.Bennu, window.BennuPortal);
    }

    Bennu.version = "1.0.0";

    Bennu.toString = function () {
        return "Bennu Toolkit v" + Bennu.version;
    };

    Bennu.gensym = function () {
        var text = "";
        var possible = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 5; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }

        return text;
    };

    Bennu.contextPath = window.contextPath;
    if(!Bennu.contextPath) {
        $("script").map(function (idx, el) {
            var src = el.getAttribute('src');
            if(src && src.indexOf('/bennu-toolkit/js/toolkit.js') != -1) {
                Bennu.contextPath = src.substring(0, src.indexOf('/bennu-toolkit/js/toolkit.js'));
            }
        });
    }

    Bennu.contextPath = Bennu.contextPath || "";

    function prefixForEvent(a) {
        a[0] = a[0].split(" ").map(function (e) {
            return "bennu-toolkit-" + e;
        }).reduce(function (x, y) {
            return x + " " + y;
        });
    }

    Bennu.on = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.on.apply(q, arguments);
    };

    Bennu.off = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.off.apply(q, arguments);
    };

    Bennu.trigger = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.trigger.apply(q, arguments);
    };

    Bennu.widgetHandler = Bennu.widgetHandler || {}
    Bennu.widgetHandler.makeFor = function (e, onremove){
        var events = [];
        var result = {
            get:function(){
                return $(e).val();
            },

            set:function(val){
                $(e).val(val);
                $(e).trigger("change");
            },

            clear:function(val){
                this.set("");
            },

            onchange:function(fn){
                events.push(fn);
            },

            trigger: function(){
                for (var i = 0; i < events.length; i++) {
                    events[i].apply(e,{
                        type:"change",
                        target:e
                    });
                }
            },

            remove: function(){
                $(e).data("input").remove();
                $(e).data("input", null);
                $(e).data("handler", null);
                $(e).off(".bennu");
            }
        }

        $(e).data("handler", result);
        return result;
    };

    if(Bennu.locales) {
        $(function (){
    		Bennu.trigger("load");
    	});
    } else {
        $.ajax({
            type: "GET",
            url: Bennu.contextPath + "/api/bennu-portal/data",
            async: false,
            dataType: "json",
            success: function (hostJson, status, response) {
                Bennu.username = hostJson.username;
                Bennu.locales = hostJson.locales;
                Bennu.locale = hostJson.locale;
                Bennu.groups = hostJson.groups;
                Bennu.lang = (function (locale) {
                    if (locale.indexOf("-") != -1) {
                        return locale.split("-")[0];
                    }
                    return locale;
                })(hostJson.locale.tag);
                Bennu.trigger("load");
            },
            error: function (xhr, status, errorThrown) {
                Bennu.username = null;
                Bennu.locales = [
                    {displayName: "English (United Kingdom)", lang: "en", tag: "en-GB"}
                ];
                Bennu.locale = {displayName: "English (United Kingdom)", lang: "en", tag: "en-GB"};
                Bennu.groups = null;
                Bennu.lang = "en";
                Bennu.trigger("load");

            }
        })
    };
}).call(this);
