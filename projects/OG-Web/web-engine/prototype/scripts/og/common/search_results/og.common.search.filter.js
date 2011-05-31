/**
 * @copyright 2009 - 2011 by OpenGamma Inc
 * @license See distribution for license
 *
 * Binds routes.go to each search form filter element
 */
$.register_module({
    name: 'og.common.search.filter',
    dependencies: ['og.common.routes'],
    obj: function () {
        var module = this, routes = og.common.routes,
            select = ['type'], // identify select form elements so we can handle these differently
            calendar = ['ob_date'], // calendar fields
            fields = ['name', 'type', 'quantity', 'data_source', 'identifier', 'data_provider', 'data_field',
                'ob_time', 'ob_date', 'status'];
        return function (obj) { // obj holds a selector with the location of the filters container
            fields.forEach(function (filter) {
                var event_type = ~select.indexOf(filter) || ~calendar.indexOf(filter) ? 'change' : 'keyup',
                    $selector = $(obj.location + ' .og-js-' + filter + '-filter');
                if (!$selector.length) return;
                !!~calendar.indexOf(filter) && $selector.datepicker({firstDay: 1, dateFormat: 'yy-mm-dd'});
                $selector.val(routes.current().args[filter]);
                $selector.unbind(event_type).bind(event_type, function () {
                    var last = routes.last(), view = og.views[last.page.substring(1)], hash, obj;
                    obj = {}, obj[filter] = $(this).val(), obj.filter = true;
                    hash = routes.hash(view.rules.load_filter, $.extend(true, {}, last.args, obj));
                    clearTimeout(module.t), module.t = setTimeout(function () {routes.go(hash), delete module.t;}, 200);
                });
            });
        }
    }
});