define(['jquery',
        'handlebars',
        'text!templates/index.hbs',
        'text!templates/templates.hbs'], function($, Handlebars, index, templates) {

    'use strict';

    function API() {

        this.CONFIG = {
            placeholder_id: 'placeholder'
        };

    }

    API.prototype.init = function (config) {

        /* Extend default configuration. */
        this.CONFIG = $.extend(true, {}, this.CONFIG, config);

        /* Register partials. */
        Handlebars.registerPartial('home_groups', $(templates).filter('#home_groups').html());

        /* Load template. */
        var source = $(index).filter('#structure').html();
        var template = Handlebars.compile(source);
        var dynamic_data = {};
        var html = template(dynamic_data);
        $('#' + this.CONFIG.placeholder_id).html(html);

    };

    return API;

});