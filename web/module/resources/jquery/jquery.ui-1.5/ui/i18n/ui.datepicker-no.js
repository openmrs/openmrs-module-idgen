/* Norwegian initialisation for the jQuery UI date picker plugin. */
/* Written by Naimdjon Takhirov (naimdjon@gmail.com). */

$(document).ready(function(){
    $.datepicker.regional['no'] = {clearText: 'T????m', clearStatus: '',
		closeText: 'Lukk', closeStatus: '',
        prevText: '&laquo;Forrige',  prevStatus: '',
		nextText: 'Neste&raquo;', nextStatus: '',
		currentText: 'I dag', currentStatus: '',
        monthNames: ['Januar','Februar','Mars','April','Mai','Juni', 
        'Juli','August','September','Oktober','November','Desember'],
        monthNamesShort: ['Jan','Feb','Mar','Apr','Mai','Jun', 
        'Jul','Aug','Sep','Okt','Nov','Des'],
		monthStatus: '', yearStatus: '',
		weekHeader: 'Uke', weekStatus: '',
		dayNamesShort: ['S????n','Man','Tir','Ons','Tor','Fre','L????r'],
		dayNames: ['S????ndag','Mandag','Tirsdag','Onsdag','Torsdag','Fredag','L????rdag'],
		dayNamesMin: ['S????','Ma','Ti','On','To','Fr','L????'],
		dayStatus: 'DD', dateStatus: 'D, M d',
        dateFormat: 'yy-mm-dd', firstDay: 0, 
		initStatus: '', isRTL: false};
    $.datepicker.setDefaults($.datepicker.regional['no']); 
});
