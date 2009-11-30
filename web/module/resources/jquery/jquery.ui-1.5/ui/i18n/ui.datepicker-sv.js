???/* Swedish initialisation for the jQuery UI date picker plugin. */
/* Written by Anders Ekdahl ( anders@nomadiz.se). */
jQuery(function($){
    $.datepicker.regional['sv'] = {clearText: 'Rensa', clearStatus: '',
		closeText: 'St??ng', closeStatus: '',
        prevText: '&laquo;F??rra',  prevStatus: '',
		nextText: 'N??sta&raquo;', nextStatus: '',
		currentText: 'Idag', currentStatus: '',
        monthNames: ['Januari','Februari','Mars','April','Maj','Juni', 
        'Juli','Augusti','September','Oktober','November','December'],
        monthNamesShort: ['Jan','Feb','Mar','Apr','Maj','Jun', 
        'Jul','Aug','Sep','Okt','Nov','Dec'],
		monthStatus: '', yearStatus: '',
		weekHeader: 'Ve', weekStatus: '',
		dayNamesShort: ['S??n','M??n','Tis','Ons','Tor','Fre','L??r'],
		dayNames: ['S??ndag','M??ndag','Tisdag','Onsdag','Torsdag','Fredag','L??rdag'],
		dayNamesMin: ['S??','M??','Ti','On','To','Fr','L??'],
		dayStatus: 'DD', dateStatus: 'D, M d',
        dateFormat: 'yy-mm-dd', firstDay: 0, 
		initStatus: '', isRTL: false};
    $.datepicker.setDefaults($.datepicker.regional['sv']); 
});
