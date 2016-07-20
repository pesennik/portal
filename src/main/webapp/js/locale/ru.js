define("locale/ru", [], function () {
    return {
        dictionary: {
            "second": "секунда",
            "a:second": "секунд" // винительный пажеж
        },

        pluralForm: {
            "секунда": ["секунда", "секунды", "секунд"],
            "секунд": ["секунду", "секунды", "секунд"]
        },

        pluralIndex: function (n) {
            return (n % 10 == 1 && n % 100 != 11 ? 0 : n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);
        }
    };
});
