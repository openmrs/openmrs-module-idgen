/* Slovak initialisation for the jQuery UI date picker plugin. */
/* Written by Vojtech Rinik (vojto@hmm.sk). */
jQuery(function($){
	$.datepicker.regional['sk'] = {clearText: 'Zmaza????', clearStatus: '',
		closeText: 'Zavrie????', closeStatus: '',
		prevText: '&lt;Predch????dzaj????ci',  prevStatus: '',
		nextText: 'Nasleduj????ci&gt;', nextStatus: '',
		currentText: 'Dnes', currentStatus: '',
		monthNames: ['Janu????r','Febru????r','Marec','Apr????l','M????j','J????n',
		'J????l','August','September','Okt????ber','November','December'],
		monthNamesShort: ['Jan','Feb','Mar','Apr','M????j','J????n',
		'J????l','Aug','Sep','Okt','Nov','Dec'],
		monthStatus: '', yearStatus: '',
		weekHeader: 'Ty', weekStatus: '',
		dayNames: ['Nedel\'a','Pondelok','Utorok','Streda','????tvrtok','Piatok','Sobota'],
		dayNamesShort: ['Ned','Pon','Uto','Str','????tv','Pia','Sob'],
		dayNamesMin: ['Ne','Po','Ut','St','????t','Pia','So'],
		dayStatus: 'DD', dateStatus: 'D, M d',
		dateFormat: 'dd.mm.yy', firstDay: 0, 
		initStatus: '', isRTL: false};
	$.datepicker.setDefaults($.datepicker.regional['sk']);
});
