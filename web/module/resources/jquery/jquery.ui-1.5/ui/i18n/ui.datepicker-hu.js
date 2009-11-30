/* Hungarian initialisation for the jQuery UI date picker plugin. */
/* Written by Istvan Karaszi (jquerycalendar@spam.raszi.hu). */
jQuery(function($){
	$.datepicker.regional['hu'] = {clearText: 't????rl????s', clearStatus: '',
		closeText: 'bez????r????s', closeStatus: '',
		prevText: '&laquo;&nbsp;vissza', prevStatus: '',
		nextText: 'el????re&nbsp;&raquo;', nextStatus: '',
		currentText: 'ma', currentStatus: '',
		monthNames: ['Janu????r', 'Febru????r', 'M????rcius', '????prilis', 'M????jus', 'J????nius',
		'J????lius', 'Augusztus', 'Szeptember', 'Okt????ber', 'November', 'December'],
		monthNamesShort: ['Jan', 'Feb', 'M????r', '????pr', 'M????j', 'J????n',
		'J????l', 'Aug', 'Szep', 'Okt', 'Nov', 'Dec'],
		monthStatus: '', yearStatus: '',
		weekHeader: 'H????', weekStatus: '',
		dayNames: ['Vas????map', 'H????tf????', 'Kedd', 'Szerda', 'Cs????t????rt????k', 'P????ntek', 'Szombat'],
		dayNamesShort: ['Vas', 'H????t', 'Ked', 'Sze', 'Cs????', 'P????n', 'Szo'],
		dayNamesMin: ['V', 'H', 'K', 'Sze', 'Cs', 'P', 'Szo'],
		dayStatus: 'DD', dateStatus: 'D, M d',
		dateFormat: 'yy-mm-dd', firstDay: 1, 
		initStatus: '', isRTL: false};
	$.datepicker.setDefaults($.datepicker.regional['hu']);
});
