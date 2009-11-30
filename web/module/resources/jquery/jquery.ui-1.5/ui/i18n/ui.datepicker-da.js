???/* Danish initialisation for the jQuery UI date picker plugin. */
/* Written by Jan Christensen ( deletestuff@gmail.com). */
jQuery(function($){
    $.datepicker.regional['da'] = {clearText: 'Nulstil', clearStatus: 'Nulstil den aktuelle dato',
		closeText: 'Luk', closeStatus: 'Luk uden ??ndringer',
        prevText: '&#x3c;Forrige', prevStatus: 'Vis forrige m??ned',
		nextText: 'N??ste&#x3e;', nextStatus: 'Vis n??ste m??ned',
		currentText: 'Idag', currentStatus: 'Vis aktuel m??ned',
        monthNames: ['Januar','Februar','Marts','April','Maj','Juni', 
        'Juli','August','September','Oktober','November','December'],
        monthNamesShort: ['Jan','Feb','Mar','Apr','Maj','Jun', 
        'Jul','Aug','Sep','Okt','Nov','Dec'],
		monthStatus: 'Vis en anden m??ned', yearStatus: 'Vis et andet ??r',
		weekHeader: 'Uge', weekStatus: '??rets uge',
		dayNames: ['S??ndag','Mandag','Tirsdag','Onsdag','Torsdag','Fredag','L??rdag'],
		dayNamesShort: ['S??n','Man','Tir','Ons','Tor','Fre','L??r'],
		dayNamesMin: ['S??','Ma','Ti','On','To','Fr','L??'],
		dayStatus: 'S??t DD som f??rste ugedag', dateStatus: 'V??lg D, M d',
        dateFormat: 'dd-mm-yy', firstDay: 0, 
		initStatus: 'V??lg en dato', isRTL: false};
    $.datepicker.setDefaults($.datepicker.regional['da']); 
});
