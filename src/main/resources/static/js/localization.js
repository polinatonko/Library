$(document).ready(function() {
    $('#locales li.dropdown-item button').on('click', function () {
        var selectedOption = $(this).val();
        console.log(selectedOption);
        if (selectedOption != '') {
            var location = window.location.href;
            if (location.indexOf('lang=') != -1) {
                location = location.replace(/(lang=)[a-z]{2}(&?)/,'$1' + selectedOption + '$2');
            } else if (location.indexOf('?') != -1) {
                location += '&lang=' + selectedOption;
            } else {
                location += '?lang=' + selectedOption;
            }
            window.location.replace(location);
        }
});
});