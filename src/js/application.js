/*global define,document*/
define(['jquery',
        'handlebars',
        'text!templates/index.hbs',
        'text!templates/templates.hbs',
        'text!templates/snippets.hbs',
        'codemirror',
        'bootstrap'], function($, Handlebars, index, templates, snippets, CodeMirror) {

    'use strict';

    function API() {

        this.CONFIG = {
            placeholder_id: 'placeholder'
        };

    }

    API.prototype.init = function (config) {

        /* Variables. */
        var editor, source, template, dynamic_data, html, buffer = [];

        /* Extend default configuration. */
        this.CONFIG = $.extend(true, {}, this.CONFIG, config);

        /* Register partials. */
        Handlebars.registerPartial('menu', $(templates).filter('#menu').html());
        Handlebars.registerPartial('base_url', $(templates).filter('#base_url').html());
        Handlebars.registerPartial('introduction', $(templates).filter('#introduction').html());
        Handlebars.registerPartial('introduction_datasources', $(templates).filter('#introduction_datasources').html());
        Handlebars.registerPartial('introduction_languages', $(templates).filter('#introduction_languages').html());
        Handlebars.registerPartial('introduction_output_structure', $(templates).filter('#introduction_output_structure').html());
        Handlebars.registerPartial('introduction_http_codes', $(templates).filter('#introduction_http_codes').html());
        Handlebars.registerPartial('javascript_client', $(templates).filter('#javascript_client').html());
        Handlebars.registerPartial('table_headers', $(templates).filter('#table_headers').html());
        Handlebars.registerPartial('table_commons', $(templates).filter('#table_commons').html());
        //Handlebars.registerPartial('home', $(templates).filter('#home').html());
        Handlebars.registerPartial('home_groups', $(templates).filter('#home_groups').html());
        Handlebars.registerPartial('abbreviations', $(templates).filter('#abbreviations').html());
        Handlebars.registerPartial('classifications', $(templates).filter('#classifications').html());
        Handlebars.registerPartial('codes', $(templates).filter('#codes').html());
        Handlebars.registerPartial('data', $(templates).filter('#data').html());
        Handlebars.registerPartial('dimensions', $(templates).filter('#dimensions').html());
        Handlebars.registerPartial('domains', $(templates).filter('#domains').html());
        Handlebars.registerPartial('glossary', $(templates).filter('#glossary').html());
        Handlebars.registerPartial('methodologies', $(templates).filter('#methodologies').html());
        Handlebars.registerPartial('methodology', $(templates).filter('#methodology').html());
        Handlebars.registerPartial('home_db_updates', $(templates).filter('#home_db_updates').html());
        Handlebars.registerPartial('home_news', $(templates).filter('#home_news').html());
        Handlebars.registerPartial('home_coming_up', $(templates).filter('#home_coming_up').html());
        //Handlebars.registerPartial('download', $(templates).filter('#download').html());
        Handlebars.registerPartial('download_groups_domains', $(templates).filter('#download_groups_domains').html());
        Handlebars.registerPartial('download_metadata', $(templates).filter('#download_metadata').html());
        Handlebars.registerPartial('download_bulk', $(templates).filter('#download_bulk').html());
        //Handlebars.registerPartial('download_filters', $(templates).filter('#download_filters').html());
        //Handlebars.registerPartial('download_codelists', $(templates).filter('#download_codelists').html());
        //Handlebars.registerPartial('download_data', $(templates).filter('#download_data').html());
        //Handlebars.registerPartial('standards', $(templates).filter('#standards').html());
        Handlebars.registerPartial('standards_methodologies', $(templates).filter('#standards_methodologies').html());
        Handlebars.registerPartial('standards_methodology', $(templates).filter('#standards_methodology').html());
        //Handlebars.registerPartial('standards_classification', $(templates).filter('#standards_classification').html());
        Handlebars.registerPartial('standards_units', $(templates).filter('#standards_units').html());
        Handlebars.registerPartial('standards_glossary', $(templates).filter('#standards_glossary').html());
        //Handlebars.registerPartial('standards_abbreviations', $(templates).filter('#standards_abbreviations').html());
        //Handlebars.registerPartial('compare', $(templates).filter('#compare').html());
        //Handlebars.registerPartial('search', $(templates).filter('#search').html());
        //Handlebars.registerPartial('analysis', $(templates).filter('#analysis').html());
        //Handlebars.registerPartial('browse', $(templates).filter('#browse').html());

        /* Load template. */
        source = $(index).filter('#structure').html();
        template = Handlebars.compile(source);
        dynamic_data = {};
        html = template(dynamic_data);
        $('#' + this.CONFIG.placeholder_id).html(html);

        /* Load the cURL snippet and store it into the buffer. */
        editor = CodeMirror.fromTextArea(document.getElementById('home_groups_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#home_groups_in_curl').html().trim());
        buffer.push('home_groups_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('download_groupsanddomains_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#download_groupsanddomains_in_curl').html().trim());
        buffer.push('download_groupsanddomains_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('download_bulkdownloads_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#download_bulkdownloads_in_curl').html().trim());
        buffer.push('download_bulkdownloads_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('abbreviations_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#abbreviations_in_curl').html().trim());
        buffer.push('abbreviations_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('classifications_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#classifications_in_curl').html().trim());
        buffer.push('classifications_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('codes_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#codes_in_curl').html().trim());
        buffer.push('codes_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('data_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#data_in_curl').html().trim());
        buffer.push('data_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('dimensions_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#dimensions_in_curl').html().trim());
        buffer.push('data_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('domains_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#domains_in_curl').html().trim());
        buffer.push('domains_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('glossary_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#glossary_in_curl').html().trim());
        buffer.push('glossary_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('methodologies_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#methodologies_in_curl').html().trim());
        buffer.push('methodologies_in_curl');

        editor = CodeMirror.fromTextArea(document.getElementById('methodology_in_curl_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#methodology_in_curl').html().trim());
        buffer.push('methodology_in_curl');

        /* Output snippet. */
        editor = CodeMirror.fromTextArea(document.getElementById('home_groups_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#home_groups_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('download_groupsanddomains_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#download_groupsanddomains_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('download_bulkdownloads_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#download_bulkdownloads_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('abbreviations_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#abbreviations_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('classifications_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#classifications_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('codes_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#codes_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('data_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#data_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('dimensions_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#dimensions_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('domains_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#domains_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('glossary_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#glossary_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('methodologies_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#methodologies_out').html().trim());

        editor = CodeMirror.fromTextArea(document.getElementById('methodology_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#methodology_out').html().trim());

        /* Introduction > Output structure example. */
        editor = CodeMirror.fromTextArea(document.getElementById('introduction_output_structure_out_content'), {lineNumbers: true});
        editor.setValue($(snippets).filter('#introduction_output_structure_out_content').html().trim());

        /* Load other snippets on tab change to avoid Bootstrap's tab rendering problems. */
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var target = $(e.target).attr('href'), local_editor;
            target = target.substring(1);
            if ($.inArray(target, buffer) < 0) {
                buffer.push(target);
                local_editor = CodeMirror.fromTextArea(document.getElementById(target + '_content'), {lineNumbers: true});
                local_editor.setValue($(snippets).filter('#' + target).html().trim());
            }
        });


    };

    return API;

});