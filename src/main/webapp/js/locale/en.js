define("locale/en", [], function () {
    return {

        //By default all values are in english, no need to translate
        dictionary: {
            "a:second": "second" // accusative case
        },

        pluralForm: {
            "second": ["second", "seconds"]
        },

        pluralIndex: function (n) {
            return n != 1 ? 1 : 0;
        }
    };

});
