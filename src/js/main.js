require.config({

    baseUrl: './src/js/libs',

    paths: {
        templates: '../../../html'
    },

    shim: {
        bootstrap: ['jquery'],
        prettify: ['jquery'],
        underscore: {
            exports: '_'
        }
    }

});

require(['../application'], function(API) {

    /* Initiate components. */
    var api = new API();

    /* Initiate the application. */
    api.init();

});